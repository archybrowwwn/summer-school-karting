-- +goose Up
ALTER TABLE bookings DROP CONSTRAINT bookings_seats_chk;
ALTER TABLE bookings ADD CONSTRAINT bookings_seats_chk CHECK (seats_count BETWEEN 1 AND 5);

ALTER TABLE routes DROP CONSTRAINT routes_capacity_chk;
ALTER TABLE routes ADD CONSTRAINT routes_capacity_chk CHECK (
    capacity_cap > 0
    AND ((type = 'novice' AND capacity_cap <= 8) OR (type = 'experienced' AND capacity_cap <= 14))
);

-- +goose Down
ALTER TABLE routes DROP CONSTRAINT routes_capacity_chk;
ALTER TABLE routes ADD CONSTRAINT routes_capacity_chk CHECK (
    capacity_cap > 0
    AND ((type = 'novice' AND capacity_cap <= 8) OR (type = 'experienced' AND capacity_cap <= 12))
);

ALTER TABLE bookings DROP CONSTRAINT bookings_seats_chk;
ALTER TABLE bookings ADD CONSTRAINT bookings_seats_chk CHECK (seats_count BETWEEN 1 AND 3);