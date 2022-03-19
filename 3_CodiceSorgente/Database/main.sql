-- See https://stackoverflow.com/a/201378/8047328
CREATE OR REPLACE FUNCTION controllo_formato_email(email text)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
	BEGIN
		RETURN (email ~ 
		'(?:[a-z0-9!#$%&''*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&''*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])');
	END
$$

CREATE TABLE Utente
(
	email text,
	nome text NOT NULL,
	cognome text NOT NULL,
	cellulare text,
	citta text,
	isAdmin boolean NOT NULL,
	CONSTRAINT UTENTE_PK PRIMARY KEY(email),
	CONSTRAINT UTENTE_EMAIL_FORMAT CHECK(controllo_formato_email(email)),
	CONSTRAINT UTENTE_CELLULARE_FORMAT CHECK(cellulare ~ '^[0-9]+$'),
	CONSTRAINT UTENTE_NOME_LENGTH CHECK(LENGTH(nome) <= 128),
	CONSTRAINT UTENTE_COGNOME_LENGTH CHECK(LENGTH(cognome) <= 128)
);

CREATE TYPE difficoltaItinerario AS ENUM ('facile', 'media', 'difficile');

CREATE TABLE Itinerario
(
	id bigserial,
	authorId text,
	nome text NOT NULL,
	durata integer NOT NULL,
	nomePuntoIniziale text NOT NULL,
	difficolta difficoltaItinerario NOT NULL,
	descrizione text,
	tracciatoKey text UNIQUE NOT NULL,
	CONSTRAINT ITINERARIO_PK PRIMARY KEY(id),
	CONSTRAINT ITINERARIO_FK_AUTHOR FOREIGN KEY (authorId) REFERENCES Utente(email),
	CONSTRAINT ITINERARIO_DURATA_MIN CHECK (durata > 0),
	CONSTRAINT ITINERARIO_NOMEPUNTOINIZIALE_LENGTH CHECK(LENGTH(nomePuntoIniziale) <= 256),
	CONSTRAINT ITINERARIO_DESCRIZIONE_LENGTH CHECK(LENGTH(descrizione) <= 1024)
);

CREATE TABLE Segnalazione
(
	authorId text,
	itinerarioId bigint,
	titolo text NOT NULL,
	descrizione text,
	CONSTRAINT SEGNALAZIONE_PK PRIMARY KEY(authorId, itinerarioId),
	CONSTRAINT SEGNALAZIONE_FK_ITINERARIOID FOREIGN KEY (itinerarioId) REFERENCES Itinerario(id),
	CONSTRAINT SEGNALAZIONE_FK_AUTHORID FOREIGN KEY (authorId) REFERENCES Utente(email),
	CONSTRAINT SEGNALAZIONE_TITOLO_LENGTH CHECK(LENGTH(titolo) <= 128),
	CONSTRAINT SEGNALAZIONE_DESCRIZIONE_LENGTH CHECK(LENGTH(descrizione) <= 512)
);


CREATE OR REPLACE FUNCTION check_users_not_already_in_chat(utenteOne text, utenteTwo text)
RETURNS boolean
LANGUAGE plpgsql
AS $$
	BEGIN
		RETURN (CASE WHEN (utenteTwo,  utenteOne) NOT IN (SELECT C.utenteOne, C.utenteTwo FROM Chat as C) THEN true ELSE false END);
	END
$$

CREATE TABLE Chat
(
	utenteOne text,
	utenteTwo text,
	messageCount bigint DEFAULT 0,
	CONSTRAINT CHAT_PK PRIMARY KEY(utenteOne, utenteTwo),
	CONSTRAINT CHAT_FK_UTENTEONE FOREIGN KEY (utenteOne) REFERENCES Utente(email),
	CONSTRAINT CHAT_FK_UTENTETWO FOREIGN KEY (utenteTwo) REFERENCES Utente(email),
	CONSTRAINT CHAT_CHECK_NOT_SAME_UTENTE CHECK (utenteOne <> utenteTwo),
	CONSTRAINT CHAT_MESSAGECOUNT_NOT_NEGATIVE CHECK (messageCount >= 0),
	CONSTRAINT CHAT_USERS_NOT_ALREADY_IN_CHAT CHECK (check_users_not_already_in_chat(utenteOne, utenteTwo))
);

CREATE OR REPLACE FUNCTION next_message_id(utenteOneId text, utenteTwoId text)
RETURNS bigint
LANGUAGE plpgsql
AS $$
	DECLARE
		nextId bigint;
	BEGIN
		SELECT C.messageCount INTO nextId
		FROM Chat as C
		WHERE C.utenteOne = utenteOneId AND C.utenteTwo = utenteTwoId;
		RETURN (nextId + 1);
	END
$$

--Timestamp is client generated
CREATE TABLE Messaggio
(
	id bigint DEFAULT 0,
	utenteOne text,
	utenteTwo text,
	testo text NOT NULL,
	ts timestamp with time zone NOT NULL,
	isUtenteOneSender boolean NOT NULL,
	CONSTRAINT MESSAGGIO_PK PRIMARY KEY(id, utenteOne, utenteTwo),
	CONSTRAINT MESSAGGIO_FK_UTENTE_PAIR FOREIGN KEY (utenteOne, utenteTwo) REFERENCES Chat(utenteOne, utenteTwo),
	CONSTRAINT MESSAGGIO_TESTO_LENGTH CHECK (LENGTH(testo) <= 4096)
);


CREATE OR REPLACE FUNCTION increase_chat_message_count_and_set_message_id()
RETURNS trigger
LANGUAGE plpgsql
AS $$
	BEGIN
		NEW.id = next_message_id(NEW.utenteOne, NEW.utenteTwo);
		UPDATE Chat
		SET messageCount = messageCount + 1
		WHERE utenteOne = NEW.utenteOne
		AND
		utenteTwo = NEW.utenteTwo;
		RETURN NEW;
	END
$$

CREATE TRIGGER ON_MESSAGE_INSERT
	BEFORE INSERT ON Messaggio
	FOR EACH ROW
	EXECUTE PROCEDURE increase_chat_message_count_and_set_message_id()
	
	