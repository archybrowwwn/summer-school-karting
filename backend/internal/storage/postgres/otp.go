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

func latestOTP(ctx context.Context, db *pgxpool.Pool, phone, purpose string) (auth.OTP, bool, error) {
	var otp auth.OTP
	err := db.QueryRow(ctx, `
SELECT id::text, code_hash, created_at, expires_at, consumed_at, attempt_count
FROM otp_codes
WHERE phone = $1 AND purpose = $2
ORDER BY created_at DESC
LIMIT 1`, phone, purpose).Scan(&otp.ID, &otp.CodeHash, &otp.CreatedAt, &otp.ExpiresAt, &otp.ConsumedAt, &otp.AttemptCount)
	if errors.Is(err, pgx.ErrNoRows) {
		return auth.OTP{}, false, nil
	}
	if err != nil {
		return auth.OTP{}, false, fmt.Errorf("query latest otp: %w", err)
	}
	return otp, true, nil
}

func createOTP(ctx context.Context, db *pgxpool.Pool, phone, purpose, codeHash string, expiresAt time.Time) error {
	_, err := db.Exec(ctx, `
INSERT INTO otp_codes (phone, purpose, code_hash, expires_at)
VALUES ($1, $2, $3, $4)`, phone, purpose, codeHash, expiresAt)
	if err != nil {
		return fmt.Errorf("create otp: %w", err)
	}
	return nil
}

func consumeOTP(ctx context.Context, db *pgxpool.Pool, id string, now time.Time) error {
	_, err := db.Exec(ctx, `UPDATE otp_codes SET consumed_at = $2 WHERE id = $1`, id, now)
	if err != nil {
		return fmt.Errorf("consume otp: %w", err)
	}
	return nil
}

func incrementOTPAttempts(ctx context.Context, db *pgxpool.Pool, id string) error {
	_, err := db.Exec(ctx, `UPDATE otp_codes SET attempt_count = attempt_count + 1 WHERE id = $1`, id)
	if err != nil {
		return fmt.Errorf("increment otp attempts: %w", err)
	}
	return nil
}