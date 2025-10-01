ALTER TABLE event_templates ADD COLUMN event_type VARCHAR(10) NULL;

UPDATE event_templates SET
    event_type = CASE is_group_event
        WHEN true THEN 'GROUP'
        WHEN false THEN 'ONE2ONE'
    END;

ALTER TABLE event_templates DROP COLUMN is_group_event;
ALTER TABLE event_templates ALTER COLUMN event_type SET NOT NULL;