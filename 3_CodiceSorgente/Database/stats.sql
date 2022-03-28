CREATE TABLE Statistiche
(
	id boolean DEFAULT TRUE,
	utenteCount bigint DEFAULT 0 NOT NULL,
	utenteAccessCount bigint DEFAULT 0 NOT NULL,
	itinerarioCount bigint DEFAULT 0 NOT NULL,
	chatCount bigint DEFAULT 0 NOT NULL,
	messaggioCount bigint DEFAULT 0 NOT NULL,
	
	CONSTRAINT STATISTICHE_PK PRIMARY KEY(id),
	CONSTRAINT STATISTICHE_FORCE_ONE_ROW CHECK(id = true)
);

CREATE TYPE countType AS ENUM ('utente', 'utenteAccess', 'itinerario', 'chat', 'messaggio');

CREATE OR REPLACE FUNCTION manage_user_count()
RETURNS trigger
LANGUAGE plpgsql
AS $$
	DECLARE
		countOperator int;
		queryPiece1 text = 'UPDATE Statistiche SET ';
		queryPiece2 text = 'Count = ';
		queryPiece3 text = 'Count + countOperator WHERE Statistiche.id = true';
	BEGIN
		IF (TG_ARGV[1]::boolean) THEN
			countOperator = 1;
		ELSE
			countOperator = -1;
		END IF;
		EXECUTE (queryPiece1 || TG_ARGV[0] || queryPiece2 || TG_ARGV[0] || queryPiece3);
		RETURN NULL;
	END
$$

CREATE TRIGGER ON_UTENTE_INSERT_STATCOUNT
	AFTER INSERT ON Utente
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('utente', true);
	
CREATE TRIGGER ON_UTENTE_DELETE_STATCOUNT
	AFTER DELETE ON Utente
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('utente', false);
	
CREATE TRIGGER ON_ITINERARIO_INSERT_STATCOUNT
	AFTER INSERT ON Itinerario
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('itinerario', true);
	
CREATE TRIGGER ON_ITINERARIO_DELETE_STATCOUNT
	AFTER DELETE ON Itinerario
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('itinerario', false);
	
CREATE TRIGGER ON_CHAT_INSERT_STATCOUNT
	AFTER INSERT ON Chat
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('chat', true);
	
CREATE TRIGGER ON_CHAT_DELETE_STATCOUNT
	AFTER DELETE ON Chat
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('chat', false);

CREATE TRIGGER ON_MESSAGGIO_INSERT_STATCOUNT
	AFTER INSERT ON Messaggio
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('messaggio', true);
	
CREATE TRIGGER ON_MESSAGGIO_DELETE_STATCOUNT
	AFTER DELETE ON Messaggio
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('messaggio', false);