package postgres

import (
	"context"
	"errors"
	"fmt"
	"time"

	"summer-school-2026/backend/internal/service/auth"
	"summer-school-2026/backend/internal/service/profile"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type ProfileRepository struct {
	db *pgxpool.Pool
}

func NewProfileRepository(db *pgxpool.Pool) *ProfileRepository {
	return &ProfileRepository{db: db}
}

func (r *ProfileRepository) ClientBySessionTokenHash(ctx context.Context, tokenHash string) (profile.Client, bool, error) {
	var client profile.Client
	err := r.db.QueryRow(ctx, `
SELECT c.id::text, c.name, c.phone, c.created_at
FROM auth_sessions s
JOIN clients c ON c.id = s.client_id
WHERE s.token_hash = $1
  AND s.revoked_at IS NULL
  AND s.expires_at > now()
  AND c.deleted_at IS NULL`, tokenHash).Scan(&client.ID, &client.Name, &client.Phone, &client.CreatedAt)
	if errors.Is(err, pgx.ErrNoRows) {
		return profile.Client{}, false, nil
	}
	if err != nil {
		return profile.Client{}, false, fmt.Errorf("query client by session: %w", err)
	}
	return client, true, nil
}

func (r *ProfileRepository) UpdateClientName(ctx context.Context, clientID, name string) (profile.Client, error) {
	var client profile.Client
	err := r.db.QueryRow(ctx, `
UPDATE clients
SET name = $2
WHERE id = $1 AND deleted_at IS NULL
RETURNING id::text, name, phone, created_at`, clientID, name).Scan(&client.ID, &client.Name, &client.Phone, &client.CreatedAt)
	if err != nil {
		return profile.Client{}, fmt.Errorf("update client name: %w", err)
	}
	return client, nil
}

func (r *ProfileRepository) FindClientByPhone(ctx context.Context, phone string) (profile.Client, bool, error) {
	var client profile.Client
	err := r.db.QueryRow(ctx, `
SELECT id::text, name, phone, created_at
FROM clients
WHERE phone = $1 AND deleted_at IS NULL`, phone).Scan(&client.ID, &client.Name, &client.Phone, &client.CreatedAt)
	if errors.Is(err, pgx.ErrNoRows) {
		return profile.Client{}, false, nil
	}
	if err != nil {
		return profile.Client{}, false, fmt.Errorf("find client by phone: %w", err)
	}
	return client, true, nil
}

func (r *ProfileRepository) LatestOTP(ctx context.Context, phone, purpose string) (auth.OTP, bool, error) {
	return latestOTP(ctx, r.db, phone, purpose)
}

func (r *ProfileRepository) CreateOTP(ctx context.Context, phone, purpose, codeHash string, expiresAt time.Time) error {
	return createOTP(ctx, r.db, phone, purpose, codeHash, expiresAt)
}

func (r *ProfileRepository) ConsumeOTP(ctx context.Context, id string, now time.Time) error {
	return consumeOTP(ctx, r.db, id, now)
}

func (r *ProfileRepository) IncrementOTPAttempts(ctx context.Context, id string) error {
	return incrementOTPAttempts(ctx, r.db, id)
}

func (r *ProfileRepository) ChangeClientPhone(ctx context.Context, clientID, newPhone, otpID string, now time.Time) (profile.Client, error) {
	tx, err := r.db.Begin(ctx)
	if err != nil {
		return profile.Client{}, fmt.Errorf("begin change phone: %w", err)
	}
	defer tx.Rollback(ctx)

	var client profile.Client
	err = tx.QueryRow(ctx, `
UPDATE clients
SET phone = $2
WHERE id = $1 AND deleted_at IS NULL
RETURNING id::text, name, phone, created_at`, clientID, newPhone).Scan(&client.ID, &client.Name, &client.Phone, &client.CreatedAt)
	if err != nil {
		return profile.Client{}, fmt.Errorf("change client phone: %w", err)
	}
	if _, err := tx.Exec(ctx, `UPDATE otp_codes SET consumed_at = $2 WHERE id = $1`, otpID, now); err != nil {
		return profile.Client{}, fmt.Errorf("consume phone change otp: %w", err)
	}
	if err := tx.Commit(ctx); err != nil {
		return profile.Client{}, fmt.Errorf("commit change phone: %w", err)
	}
	return client, nil
}

func (r *ProfileRepository) DeleteClientAccount(ctx context.Context, clientID string, now time.Time) error {
	tx, err := r.db.Begin(ctx)
	if err != nil {
		return fmt.Errorf("begin delete account: %w", err)
	}
	defer tx.Rollback(ctx)

	if _, err := tx.Exec(ctx, `UPDATE auth_sessions SET revoked_at = $2 WHERE client_id = $1 AND revoked_at IS NULL`, clientID, now); err != nil {
		return fmt.Errorf("revoke client sessions: %w", err)
	}
	if _, err := tx.Exec(ctx, `
UPDATE bookings
SET status = 'cancelled', cancelled_at = $2
WHERE client_id = $1 AND status = 'active'`, clientID, now); err != nil {
		return fmt.Errorf("cancel client bookings: %w", err)
	}
	if _, err := tx.Exec(ctx, `
UPDATE clients
SET name = NULL,
    phone = '+1' || lpad(abs(hashtext(id::text))::text, 13, '0'),
    deleted_at = $2
WHERE id = $1 AND deleted_at IS NULL`, clientID, now); err != nil {
		return fmt.Errorf("anonymize client: %w", err)
	}
	if err := tx.Commit(ctx); err != nil {
		return fmt.Errorf("commit delete account: %w", err)
	}
	return nil
}
