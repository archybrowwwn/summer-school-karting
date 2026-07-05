package postgres

import (
	"context"
	"errors"
	"fmt"
	"time"

	"summer-school-2026/backend/internal/service/auth"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type AuthRepository struct {
	db *pgxpool.Pool
}

func NewAuthRepository(db *pgxpool.Pool) *AuthRepository {
	return &AuthRepository{db: db}
}

func (r *AuthRepository) LatestOTP(ctx context.Context, phone, purpose string) (auth.OTP, bool, error) {
	return latestOTP(ctx, r.db, phone, purpose)
}

func (r *AuthRepository) CreateOTP(ctx context.Context, phone, purpose, codeHash string, expiresAt time.Time) error {
	return createOTP(ctx, r.db, phone, purpose, codeHash, expiresAt)
}

func (r *AuthRepository) ConsumeOTP(ctx context.Context, id string, now time.Time) error {
	return consumeOTP(ctx, r.db, id, now)
}

func (r *AuthRepository) IncrementOTPAttempts(ctx context.Context, id string) error {
	return incrementOTPAttempts(ctx, r.db, id)
}

func (r *AuthRepository) FindClientByPhone(ctx context.Context, phone string) (auth.Client, bool, error) {
	var client auth.Client
	err := r.db.QueryRow(ctx, `
SELECT id::text, name, phone, created_at
FROM clients
WHERE phone = $1 AND deleted_at IS NULL`, phone).Scan(&client.ID, &client.Name, &client.Phone, &client.CreatedAt)
	if errors.Is(err, pgx.ErrNoRows) {
		return auth.Client{}, false, nil
	}
	if err != nil {
		return auth.Client{}, false, fmt.Errorf("find client by phone: %w", err)
	}
	return client, true, nil
}

func (r *AuthRepository) CreateClient(ctx context.Context, phone string, now time.Time) (auth.Client, error) {
	var client auth.Client
	err := r.db.QueryRow(ctx, `
INSERT INTO clients (phone, created_at)
VALUES ($1, $2)
RETURNING id::text, name, phone, created_at`, phone, now).Scan(&client.ID, &client.Name, &client.Phone, &client.CreatedAt)
	if err != nil {
		return auth.Client{}, fmt.Errorf("create client: %w", err)
	}
	return client, nil
}

func (r *AuthRepository) CreateSession(ctx context.Context, clientID, tokenHash string, expiresAt time.Time) error {
	_, err := r.db.Exec(ctx, `
INSERT INTO auth_sessions (client_id, token_hash, expires_at)
VALUES ($1, $2, $3)`, clientID, tokenHash, expiresAt)
	if err != nil {
		return fmt.Errorf("create session: %w", err)
	}
	return nil
}

func (r *AuthRepository) RevokeSession(ctx context.Context, tokenHash string, now time.Time) error {
	_, err := r.db.Exec(ctx, `
UPDATE auth_sessions
SET revoked_at = $2
WHERE token_hash = $1 AND revoked_at IS NULL`, tokenHash, now)
	if err != nil {
		return fmt.Errorf("revoke session: %w", err)
	}
	return nil
}
