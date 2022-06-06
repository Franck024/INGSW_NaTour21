CREATE OR REPLACE FUNCTION get_last_n_messaggio(utenteOneId text, utenteTwoId text, numberOfMessaggioToGet bigint)
RETURNS table
(id bigint, testo text, ts timestamp with time zone, isUtenteOneSender boolean)
LANGUAGE plpgsql
AS $$
	BEGIN
		RETURN QUERY EXECUTE ('SELECT M.id, M.testo, M.ts, M.isUtenteOneSender ' ||
		'FROM Messaggio AS M WHERE M.utenteOne = $1 AND M.utenteTwo = $2 ' ||
		'ORDER BY M.id DESC LIMIT $3') USING utenteOneId, utenteTwoId, numberOfMessaggioToGet;
	END
$$

CREATE OR REPLACE FUNCTION get_last_n_itinerario(numberOfItinerarioToGet integer)
RETURNS TABLE (id bigint, authorId text, nome text, durata integer, puntoIniziale geography(POINT), nomePuntoIniziale text,
difficolta difficoltaItinerario, descrizione text, tracciatoKey text, isAccessibleMobilityImpairment boolean, 
isAccessibleVisualImpairment boolean)
LANGUAGE plpgsql
AS $$
	BEGIN
		RETURN QUERY EXECUTE ('SELECT * FROM Itinerario ORDER BY Itinerario.id DESC LIMIT $1') USING numberOfItinerarioToGet;
	END
$$

CREATE OR REPLACE FUNCTION get_last_n_itinerario_newer_than(newestId bigint, numberOfItinerarioToGet integer)
RETURNS TABLE (id bigint, authorId text, nome text, durata integer, puntoIniziale geography(POINT), nomePuntoIniziale text,
difficolta difficoltaItinerario, descrizione text, tracciatoKey text, isAccessibleMobilityImpairment boolean, 
isAccessibleVisualImpairment boolean)
LANGUAGE plpgsql
AS $$
	BEGIN
		RETURN QUERY EXECUTE ('SELECT * FROM Itinerario WHERE Itinerario.id > $1 ORDER BY Itinerario.id DESC LIMIT $2') 
		USING newestId, numberOfItinerarioToGet;
	END
$$

CREATE OR REPLACE FUNCTION get_last_n_itinerario_starting_from(startingFrom bigint, numberOfItinerarioToGet integer)
RETURNS TABLE (id bigint, authorId text, nome text, durata integer, puntoIniziale geography(POINT), nomePuntoIniziale text,
difficolta difficoltaItinerario, descrizione text, tracciatoKey text, isAccessibleMobilityImpairment boolean, 
isAccessibleVisualImpairment boolean)
LANGUAGE plpgsql
AS $$
	BEGIN
		RETURN QUERY EXECUTE ('SELECT * FROM Itinerario WHERE Itinerario.id <= $1 ORDER BY Itinerario.id DESC LIMIT $2') 
		USING startingFrom, numberOfItinerarioToGet;
	END
$$