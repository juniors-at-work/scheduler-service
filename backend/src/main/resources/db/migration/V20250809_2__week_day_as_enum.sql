CREATE TYPE day_of_week AS ENUM (
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY'
  );

ALTER TABLE availability_rules ADD COLUMN new_weekday day_of_week NULL;

UPDATE availability_rules
SET
  new_weekday = CASE weekday
    WHEN 1 THEN 'MONDAY'::day_of_week
    WHEN 2 THEN 'TUESDAY'::day_of_week
    WHEN 3 THEN 'WEDNESDAY'::day_of_week
    WHEN 4 THEN 'THURSDAY'::day_of_week
    WHEN 5 THEN 'FRIDAY'::day_of_week
    WHEN 6 THEN 'SATURDAY'::day_of_week
    WHEN 7 THEN 'SUNDAY'::day_of_week
  END
WHERE weekday BETWEEN 1 AND 7;

ALTER TABLE availability_rules DROP COLUMN weekday;
ALTER TABLE availability_rules RENAME COLUMN new_weekday TO weekday;
ALTER TABLE availability_rules ALTER COLUMN weekday SET NOt NULL;
