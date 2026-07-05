-- +goose Up
UPDATE routes SET name = 'Короткая трасса' WHERE id = '11111111-1111-1111-1111-111111111111';
UPDATE routes SET name = 'Длинная трасса' WHERE id = '22222222-2222-2222-2222-222222222222';
UPDATE slots SET meeting_point = 'Павильон у стартовой прямой' WHERE id = '55555555-5555-5555-5555-555555555555';
UPDATE slots SET meeting_point = 'Бокс на длинной трассе' WHERE id = '66666666-6666-6666-6666-666666666666';

-- +goose Down
UPDATE routes SET name = 'Острова и каналы' WHERE id = '11111111-1111-1111-1111-111111111111';
UPDATE routes SET name = 'Большая вода' WHERE id = '22222222-2222-2222-2222-222222222222';
UPDATE slots SET meeting_point = 'Лодочная станция у Елагина моста' WHERE id = '55555555-5555-5555-5555-555555555555';
UPDATE slots SET meeting_point = 'Пирс у Крестовского острова' WHERE id = '66666666-6666-6666-6666-666666666666';