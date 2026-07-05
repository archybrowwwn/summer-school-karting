-- +goose Up
ALTER TABLE slots RENAME COLUMN rental_boards_total TO rental_gear_total;
ALTER TABLE slots RENAME COLUMN free_rental_boards TO free_rental_gear;
ALTER TABLE slots RENAME CONSTRAINT slots_rental_boards_chk TO slots_rental_gear_chk;

-- +goose Down
ALTER TABLE slots RENAME CONSTRAINT slots_rental_gear_chk TO slots_rental_boards_chk;
ALTER TABLE slots RENAME COLUMN free_rental_gear TO free_rental_boards;
ALTER TABLE slots RENAME COLUMN rental_gear_total TO rental_boards_total;