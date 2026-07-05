-- Демонстрационная база картинг-центра «Апекс» для проверки клиентского приложения.
-- Покрывает следующую календарную неделю (пн–вс) относительно даты применения,
-- плюс буфер для 7-дневного окна API (FR-9) и несколько прошедших заездов.
--
-- Соответствует брифу и модели данных:
--   - 4 конфигурации трасс (новичковые ≤8, опытные ≤12 мест);
--   - 3 маршала-инструктора;
--   - прокатный фонд 12 комплектов экипировки на слот;
--   - статусы броней: active / cancelled / late_cancel;
--   - отменённые клубом слоты (погода).
--
-- Безопасно перезапускать: очищает только клиентские MVP-таблицы.
--
-- Применение (сохраняет кириллицу, предпочтительно на Windows):
--   docker compose -f compose.yaml cp seed/demonstration_base.sql db:/tmp/demonstration_base.sql
--   docker compose -f compose.yaml exec -T db psql -U apex -d apex -f /tmp/demonstration_base.sql
--
-- Не используйте PowerShell Get-Content без -Encoding UTF8 — иначе имена станут «??????».

BEGIN;

CREATE TEMP TABLE _seed_bounds ON COMMIT DROP AS
SELECT
    (date_trunc('week', current_date + interval '1 day'))::date AS week_start,
    ((date_trunc('week', current_date + interval '1 day') + interval '6 days')::date) AS week_end;

TRUNCATE TABLE
    idempotency_keys,
    bookings,
    slots,
    instructors,
    routes,
    otp_codes,
    auth_sessions,
    clients
RESTART IDENTITY CASCADE;

-- Справочники
INSERT INTO routes (id, name, type, capacity_cap, duration_min, geometry)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Короткая трасса', 'novice', 8, 90,
     '[[59.978,30.262],[59.981,30.271],[59.976,30.285]]'),
    ('22222222-2222-2222-2222-222222222222', 'Длинная трасса', 'experienced', 12, 120,
     '[[59.942,30.226],[59.951,30.214],[59.963,30.232]]'),
    ('33333333-3333-3333-3333-333333333333', 'Закатный маршрут', 'experienced', 12, 110,
     '[[59.963,30.232],[59.970,30.241],[59.975,30.255]]'),
    ('44444444-4444-4444-4444-444444444444', 'Городской маршрут', 'novice', 8, 75,
     '[[59.965,30.300],[59.968,30.312],[59.971,30.321]]');

INSERT INTO instructors (id, name)
VALUES
    ('aaaa1111-1111-1111-1111-111111111111', 'Мария'),
    ('bbbb2222-2222-2222-2222-222222222222', 'Алексей'),
    ('cccc3333-3333-3333-3333-333333333333', 'Ирина');

-- Тестовые клиенты (вход по OTP; код пишется в лог backend)
INSERT INTO clients (id, phone, name, created_at)
VALUES
    ('90000000-0000-4000-8000-000000000001', '+79990000001', 'Иван', now() - interval '14 days'),
    ('90000000-0000-4000-8000-000000000002', '+79990000002', 'Анна', now() - interval '10 days'),
    ('90000000-0000-4000-8000-000000000003', '+79990000003', 'Ольга', now() - interval '7 days'),
    ('90000000-0000-4000-8000-000000000004', '+79990000004', 'Сергей', now() - interval '5 days'),
    ('90000000-0000-4000-8000-000000000005', '+79990000005', 'Дмитрий', now() - interval '3 days'),
    ('90000000-0000-4000-8000-000000000006', '+79990000006', 'Екатерина', now() - interval '2 days'),
    ('90000000-0000-4000-8000-000000000007', '+79990000007', 'Павел', now() - interval '1 day'),
    ('90000000-0000-4000-8000-000000000008', '+79990000008', 'Наталья', now() - interval '12 hours');

WITH seed_range AS (
    SELECT
        GREATEST(current_date - 2, (SELECT week_start FROM _seed_bounds)) AS seed_start,
        (SELECT week_end FROM _seed_bounds) + 2 AS seed_end
),
days AS (
    SELECT d::date AS day
    FROM seed_range r
    CROSS JOIN generate_series(r.seed_start, r.seed_end, interval '1 day') AS d
),
templates AS (
    SELECT *
    FROM (
        VALUES
            (1, 10, 0, 1, 1, 1, 2500, 800, 'Павильон у стартовой прямой', 59.978::double precision, 30.262::double precision, 'scheduled'),
            (2, 12, 30, 2, 2, 2, 3200, 800, 'Бокс на длинной трассе', 59.942, 30.226, 'scheduled'),
            (3, 15, 0, 3, 3, 3, 3400, 900, 'Набережная у закатного пирса', 59.963, 30.232, 'scheduled'),
            (4, 17, 30, 4, 1, 2, 2200, 700, 'Городской причал', 59.965, 30.300, 'scheduled')
    ) AS weekday(slot_idx, hour, minute, route_idx, instructor_idx, occupancy_pct, price, rental_price, meeting_point, meeting_point_lat, meeting_point_lng, status)
),
weekend_templates AS (
    SELECT *
    FROM (
        VALUES
            (1, 9, 0, 1, 1, 1, 2400, 750, 'Павильон у стартовой прямой', 59.978::double precision, 30.262::double precision, 'scheduled'),
            (2, 11, 0, 2, 2, 2, 3100, 800, 'Бокс на длинной трассе', 59.942, 30.226, 'scheduled'),
            (3, 13, 0, 3, 3, 3, 3300, 850, 'Набережная у закатного пирса', 59.963, 30.232, 'scheduled'),
            (4, 15, 0, 4, 1, 2, 2300, 700, 'Городской причал', 59.965, 30.300, 'scheduled'),
            (5, 17, 0, 1, 2, 4, 2600, 800, 'Северный залив', 59.981, 30.271, 'scheduled'),
            (6, 19, 0, 2, 3, 1, 3000, 800, 'Бокс на длинной трассе', 59.942, 30.226, 'scheduled')
    ) AS weekend(slot_idx, hour, minute, route_idx, instructor_idx, occupancy_pct, price, rental_price, meeting_point, meeting_point_lat, meeting_point_lng, status)
),
route_map AS (
    SELECT * FROM (
        VALUES
            (1, '11111111-1111-1111-1111-111111111111'::uuid, 8),
            (2, '22222222-2222-2222-2222-222222222222'::uuid, 12),
            (3, '33333333-3333-3333-3333-333333333333'::uuid, 12),
            (4, '44444444-4444-4444-4444-444444444444'::uuid, 8)
    ) AS t(route_idx, route_id, total_seats)
),
instructor_map AS (
    SELECT * FROM (
        VALUES
            (1, 'aaaa1111-1111-1111-1111-111111111111'::uuid),
            (2, 'bbbb2222-2222-2222-2222-222222222222'::uuid),
            (3, 'cccc3333-3333-3333-3333-333333333333'::uuid)
    ) AS t(instructor_idx, instructor_id)
),
planned AS (
    SELECT
        d.day,
        t.slot_idx,
        t.hour,
        t.minute,
        rm.route_id,
        im.instructor_id,
        rm.total_seats,
        t.occupancy_pct,
        t.price,
        t.rental_price,
        t.meeting_point,
        t.meeting_point_lat,
        t.meeting_point_lng,
        t.status,
        row_number() OVER (ORDER BY d.day, t.hour, t.minute, t.slot_idx) AS seq
    FROM days d
    JOIN LATERAL (
        SELECT * FROM templates WHERE extract(isodow FROM d.day) < 6
        UNION ALL
        SELECT * FROM weekend_templates WHERE extract(isodow FROM d.day) >= 6
    ) t ON true
    JOIN route_map rm ON rm.route_idx = t.route_idx
    JOIN instructor_map im ON im.instructor_idx = t.instructor_idx
),
planned_with_overrides AS (
    SELECT
        p.*,
        CASE
            WHEN p.day = (SELECT week_start + 2 FROM _seed_bounds) AND p.hour = 17 AND p.minute = 30 THEN 'cancelled'
            WHEN p.day = (SELECT week_start + 5 FROM _seed_bounds) AND p.hour = 15 AND p.minute = 0 THEN 'cancelled'
            ELSE p.status
        END AS final_status
    FROM planned p
)
INSERT INTO slots (
    id,
    route_id,
    instructor_id,
    start_at,
    total_seats,
    free_seats,
    rental_gear_total,
    free_rental_gear,
    price,
    rental_price,
    meeting_point,
    meeting_point_lat,
    meeting_point_lng,
    status
)
SELECT
    ('50000000-0000-4000-8000-' || lpad(to_hex(p.seq), 12, '0'))::uuid,
    p.route_id,
    p.instructor_id,
    ((p.day + make_interval(hours => p.hour, mins => p.minute))::timestamp AT TIME ZONE 'Europe/Moscow'),
    p.total_seats,
    p.total_seats,
    12,
    12,
    p.price,
    p.rental_price,
    p.meeting_point,
    p.meeting_point_lat,
    p.meeting_point_lng,
    p.final_status
FROM planned_with_overrides p;

-- Брони: разные статусы и сценарии для демонстрации экранов SCR-005/SCR-006
INSERT INTO bookings (id, slot_id, client_id, seats_count, rental_count, status, created_at, cancelled_at)
SELECT *
FROM (
    VALUES
        -- Иван: активные брони на следующую неделю
        (
            '60000000-0000-4000-8000-000000000001'::uuid,
            (SELECT s.id FROM slots s JOIN routes r ON r.id = s.route_id
             WHERE s.start_at = ((SELECT week_start FROM _seed_bounds) + time '10:00') AT TIME ZONE 'Europe/Moscow'
               AND r.type = 'novice' LIMIT 1),
            '90000000-0000-4000-8000-000000000001'::uuid,
            2, 1, 'active', now() - interval '2 days', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000002'::uuid,
            (SELECT s.id FROM slots s JOIN routes r ON r.id = s.route_id
             WHERE s.start_at = ((SELECT week_start FROM _seed_bounds) + time '10:00') AT TIME ZONE 'Europe/Moscow'
               AND r.type = 'novice' LIMIT 1),
            '90000000-0000-4000-8000-000000000005'::uuid,
            3, 2, 'active', now() - interval '1 day', NULL::timestamptz
        ),
        -- Понедельник 15:00 — заполненный заезд (12/12)
        (
            '60000000-0000-4000-8000-000000000003'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start FROM _seed_bounds) + time '15:00') AT TIME ZONE 'Europe/Moscow'
               AND s.status = 'scheduled' LIMIT 1),
            '90000000-0000-4000-8000-000000000002'::uuid,
            4, 2, 'active', now() - interval '3 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000015'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start FROM _seed_bounds) + time '15:00') AT TIME ZONE 'Europe/Moscow'
               AND s.status = 'scheduled' LIMIT 1),
            '90000000-0000-4000-8000-000000000006'::uuid,
            3, 1, 'active', now() - interval '2 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000016'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start FROM _seed_bounds) + time '15:00') AT TIME ZONE 'Europe/Moscow'
               AND s.status = 'scheduled' LIMIT 1),
            '90000000-0000-4000-8000-000000000007'::uuid,
            3, 0, 'active', now() - interval '90 minutes', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000017'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start FROM _seed_bounds) + time '15:00') AT TIME ZONE 'Europe/Moscow'
               AND s.status = 'scheduled' LIMIT 1),
            '90000000-0000-4000-8000-000000000008'::uuid,
            2, 2, 'active', now() - interval '1 hour', NULL::timestamptz
        ),
        -- Вторник 12:30 — частично занят (8/12)
        (
            '60000000-0000-4000-8000-000000000004'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 1 FROM _seed_bounds) + time '12:30') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000003'::uuid,
            2, 2, 'active', now() - interval '5 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000018'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 1 FROM _seed_bounds) + time '12:30') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000005'::uuid,
            3, 1, 'active', now() - interval '4 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000019'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 1 FROM _seed_bounds) + time '12:30') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000007'::uuid,
            3, 0, 'active', now() - interval '3 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000005'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 4 FROM _seed_bounds) + time '17:30') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000001'::uuid,
            1, 0, 'active', now() - interval '8 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000020'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 4 FROM _seed_bounds) + time '17:30') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000008'::uuid,
            1, 1, 'active', now() - interval '7 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000006'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 5 FROM _seed_bounds) + time '11:00') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000004'::uuid,
            5, 3, 'active', now() - interval '12 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000007'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 5 FROM _seed_bounds) + time '11:00') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000006'::uuid,
            3, 1, 'active', now() - interval '10 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000021'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 5 FROM _seed_bounds) + time '11:00') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000002'::uuid,
            2, 0, 'active', now() - interval '9 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000008'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 6 FROM _seed_bounds) + time '13:00') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000007'::uuid,
            4, 2, 'active', now() - interval '6 hours', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000009'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 6 FROM _seed_bounds) + time '13:00') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000008'::uuid,
            4, 2, 'active', now() - interval '4 hours', NULL::timestamptz
        ),
        -- Ранняя отмена (места освобождены)
        (
            '60000000-0000-4000-8000-000000000010'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 3 FROM _seed_bounds) + time '10:00') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000002'::uuid,
            2, 1, 'cancelled', now() - interval '3 days', now() - interval '2 days'
        ),
        -- Поздняя отмена (места не освобождены)
        (
            '60000000-0000-4000-8000-000000000011'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 3 FROM _seed_bounds) + time '12:30') AT TIME ZONE 'Europe/Moscow'
             LIMIT 1),
            '90000000-0000-4000-8000-000000000001'::uuid,
            2, 0, 'late_cancel', now() - interval '2 days', now() - interval '30 minutes'
        ),
        -- Бронь на слот, отменённый клубом (среда, погода)
        (
            '60000000-0000-4000-8000-000000000012'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at = ((SELECT week_start + 2 FROM _seed_bounds) + time '17:30') AT TIME ZONE 'Europe/Moscow'
               AND s.status = 'cancelled' LIMIT 1),
            '90000000-0000-4000-8000-000000000003'::uuid,
            2, 1, 'active', now() - interval '4 days', NULL::timestamptz
        ),
        -- Прошедший заезд (вчера/позавчера)
        (
            '60000000-0000-4000-8000-000000000013'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at < current_date::timestamptz
               AND s.status = 'scheduled'
             ORDER BY s.start_at DESC
             LIMIT 1),
            '90000000-0000-4000-8000-000000000001'::uuid,
            1, 1, 'active', now() - interval '4 days', NULL::timestamptz
        ),
        (
            '60000000-0000-4000-8000-000000000014'::uuid,
            (SELECT s.id FROM slots s
             WHERE s.start_at < current_date::timestamptz
               AND s.status = 'scheduled'
             ORDER BY s.start_at DESC
             OFFSET 1 LIMIT 1),
            '90000000-0000-4000-8000-000000000004'::uuid,
            2, 0, 'cancelled', now() - interval '5 days', now() - interval '3 days'
        )
) AS v(id, slot_id, client_id, seats_count, rental_count, status, created_at, cancelled_at)
WHERE slot_id IS NOT NULL;

-- Синхронизировать остатки мест с фактическими активными/поздними бронями
UPDATE slots s
SET
    free_seats = greatest(0, s.total_seats - b.held_seats),
    free_rental_gear = greatest(0, s.rental_gear_total - b.held_rental)
FROM (
    SELECT
        slot_id,
        sum(CASE WHEN status IN ('active', 'late_cancel') THEN seats_count ELSE 0 END) AS held_seats,
        sum(CASE WHEN status IN ('active', 'late_cancel') THEN rental_count ELSE 0 END) AS held_rental
    FROM bookings
    GROUP BY slot_id
) b
WHERE s.id = b.slot_id
  AND s.status = 'scheduled';

COMMIT;