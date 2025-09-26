ALTER TABLE availability_rules DROP CONSTRAINT IF EXISTS availability_rules_weekday_check;
ALTER TABLE availability_rules ADD CONSTRAINT availability_rules_weekday_check CHECK (weekday BETWEEN 0 AND 6);
ALTER TABLE availability_rules ADD CONSTRAINT availability_rules_start_before_end CHECK (start_time < end_time);