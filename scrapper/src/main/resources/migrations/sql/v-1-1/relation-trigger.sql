CREATE FUNCTION check_duplicate() RETURNS trigger AS
'
    BEGIN
        IF EXISTS (SELECT 1
                   FROM relation
                   WHERE chat_id = NEW.chat_id
                     AND link_id = NEW.link_id) THEN
            RAISE EXCEPTION ''Duplicate relation: chat_id %, link_id %'', NEW.chat_id, NEW.link_id;
        END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER check_duplicate
    BEFORE INSERT OR UPDATE
    ON relation
    FOR EACH ROW
EXECUTE PROCEDURE check_duplicate();
