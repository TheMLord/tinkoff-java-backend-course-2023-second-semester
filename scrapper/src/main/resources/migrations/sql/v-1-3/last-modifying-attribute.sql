ALTER TABLE links
    ADD COLUMN last_modifying TIMESTAMP WITH TIME ZONE DEFAULT NOW();
