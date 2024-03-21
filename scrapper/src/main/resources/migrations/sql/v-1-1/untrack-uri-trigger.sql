CREATE FUNCTION delete_links() RETURNS trigger AS
'
    BEGIN
        DELETE
        FROM links
        WHERE id NOT IN (SELECT link_id
                         FROM subscriptions);
        RETURN NULL;
    END;
'LANGUAGE plpgsql;

CREATE TRIGGER delete_links
    AFTER DELETE
    ON tgchats
    FOR EACH ROW
EXECUTE FUNCTION delete_links()
