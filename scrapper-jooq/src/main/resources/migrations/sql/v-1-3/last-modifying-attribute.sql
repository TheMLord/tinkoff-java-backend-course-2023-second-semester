ALTER TABLE link
    ADD COLUMN last_modifying TIMESTAMP WITH TIME ZONE DEFAULT NOW();