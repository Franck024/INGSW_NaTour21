CREATE TABLE Statistiche
(
	id boolean DEFAULT TRUE,
	utenteCount bigserial DEFAULT 0 NOT NULL,
	utenteAccessCount bigserial DEFAULT 0 NOT NULL,
	itinerarioCount bigserial DEFAULT 0 NOT NULL,
	chatCount bigserial DEFAULT 0 NOT NULL,
	messaggioCount bigserial DEFAULT 0 NOT NULL,
	
	CONSTRAINT STATISTICHE_PK PRIMARY KEY(id),
	CONSTRAINT STATISTICHE_FORCE_ONE_ROW CHECK(id = true)
);

CREATE TYPE countType AS ENUM ('utente', 'utenteAccess', 'itinerario', 'chat', 'messaggio');

CREATE OR REPLACE FUNCTION manage_user_count(countType countType, isInserted boolean)
RETURNS trigger
LANGUAGE plpgsql
AS $$
	DECLARE
		countOperator int;
		queryPiece1 = 'UPDATE Statistiche SET ';
		queryPiece2 = 'Count = ';
		queryPiece3 = 'Count + countOperator WHERE Statistiche.id = true';
	BEGIN
		IF (isInserted) THEN
			countOperator = 1;
		ELSE
			countOperator = -1;
		END IF;
		EXECUTE (queryPiece1 || countType::text || queryPiece2 || countType::text || queryPiece3);
		RETURN;
	END
$$

CREATE TRIGGER ON_UTENTE_INSERT_STATCOUNT
	AFTER INSERT ON Utente
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('utente'::countType, true);
	
CREATE TRIGGER ON_UTENTE_DELETE_STATCOUNT
	AFTER DELETE ON Utente
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('utente'::countType, false);
	
CREATE TRIGGER ON_ITINERARIO_INSERT_STATCOUNT
	AFTER INSERT ON Itinerario
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('itinerario'::countType, true);
	
CREATE TRIGGER ON_ITINERARIO_DELETE_STATCOUNT
	AFTER DELETE ON Itinerario
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('itinerario'::countType, false);
	
CREATE TRIGGER ON_CHAT_INSERT_STATCOUNT
	AFTER INSERT ON Chat
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('chat'::countType, true);
	
CREATE TRIGGER ON_CHAT_DELETE_STATCOUNT
	AFTER DELETE ON Chat
	FOR EACH ROW
	EXECUTE PROCEDURE manage_user_count('chat'::countType, false);

CREATE TRIGGER ON_MESSAGGIO_INSERT_STATCOUNT
	AFTER INSERT ON Messaggio
	FOR EACH ROW
	EXECUTE PROCEDURE manager_user_count('messaggio'::countType, true);
	
CREATE TRIGGER ON_MESSAGGIO_DELETE_STATCOUNT
	AFTER DELETE ON Messaggio
	FOR EACH ROW
	EXECUTE PROCEDURE manager_user_count('messaggio'::countType, false);