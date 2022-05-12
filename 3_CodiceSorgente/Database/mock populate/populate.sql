INSERT INTO Utente VALUES('test@test.com', 'Test', 'Man', NULL, NULL, false);
INSERT INTO Utente VALUES('test2@test.com', 'Test', 'Man 2', NULL, NULL, false);
INSERT INTO Utente VALUES('test3@test.com', 'Test', 'Man 3', NULL, NULL, false);
INSERT INTO Utente VALUES('test4@test.com', 'Test', 'Man 4', NULL, NULL, false);

INSERT INTO Itinerario VALUES(DEFAULT, 'test@test.com', 'Itinerario Test', 61, 'Punto iniziale test', 'facile', NULL, NULL);
INSERT INTO Itinerario VALUES(DEFAULT, 'test2@test.com', 'Itinerario Test 2', 121, 'Punto iniziale test', 'media', NULL, NULL);
INSERT INTO Itinerario VALUES(DEFAULT, 'test3@test.com', 'Itinerario Test 3', 478, 'Punto iniziale test', 'difficile', NULL, NULL);
INSERT INTO Itinerario VALUES(DEFAULT, 'test@test.com', 'Itinerario Test 4', 5, 'Punto iniziale test', 'facile', NULL, NULL);

INSERT INTO Segnalazione VALUES('test@test.com', 2, 'Percorso inaccessibile', 'Il percorso è inaccessibile, essendo esso situato nell''Oceano Pacifico.');

INSERT INTO Chat VALUES('test@test.com', 'test2@test.com', DEFAULT);
INSERT INTO Chat VALUES('test3@test.com', 'test3@test.com', DEFAULT);

INSERT INTO Messaggio VALUES(DEFAULT