CREATE FUNCTION delete_links() RETURNS trigger AS
'
    BEGIN
        DELETE
        FROM link
        WHERE id NOT IN (SELECT relation.link_id
                         FROM relation);
        RETURN NULL;
    END;
'
LANGUAGE plpgsql;

CREATE TRIGGER delete_links
    AFTER DELETE
    ON tgchat
    FOR EACH ROW
EXECUTE FUNCTION delete_links()
