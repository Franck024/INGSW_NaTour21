CREATE OR REPLACE FUNCTION get_last_messages(utenteOneId text, utenteTwoId text, numberOfMessagesToGet integer)
RETURNS table
(id bigint, testo text, ts timestamp with time zone, isUtenteOneSender boolean)
LANGUAGE plpgsql
AS $$
	BEGIN
		RETURN QUERY EXECUTE ('SELECT M.id, M.testo, M.ts, M.isUtenteOneSender ' ||
		'FROM Messaggio AS M WHERE M.utenteOne = $1 AND M.utenteTwo = $2 ' ||
		'ORDER BY M.id DESC LIMIT $3') USING utenteOneId, utenteTwoId, numberOfMessagesToGet;
	END
$$