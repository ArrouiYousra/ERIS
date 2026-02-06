-- Add topic and is_private columns to channel table
ALTER TABLE channel ADD COLUMN topic VARCHAR(1024) DEFAULT NULL;
ALTER TABLE channel ADD COLUMN is_private BOOLEAN DEFAULT FALSE;
