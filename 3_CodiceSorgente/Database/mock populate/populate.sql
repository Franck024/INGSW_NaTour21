INSERT INTO Statistiche VALUES(DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO Utente VALUES('test@test.com', 'Test', 'Man', NULL, NULL, false);
INSERT INTO Utente VALUES('test2@test.com', 'Test', 'Man 2', NULL, NULL, false);
INSERT INTO Utente VALUES('test3@test.com', 'Test', 'Man 3', NULL, NULL, false);
INSERT INTO Utente VALUES('test4@test.com', 'Test', 'Man 4', NULL, NULL, false);

INSERT INTO Itinerario VALUES(DEFAULT, 'test@test.com', 'Itinerario Test', 61, 'POINT(-2.2 0)', 'Punto iniziale test', 'facile', NULL, NULL, NULL, NULL);
INSERT INTO Itinerario VALUES(DEFAULT, 'test2@test.com', 'Itinerario Test 2', 121, 'POINT(157.658 -2.809)', 'Punto iniziale test', 'media', NULL, NULL, false, false);
INSERT INTO Itinerario VALUES(DEFAULT, 'test3@test.com', 'Itinerario Test 3', 478, 'POINT(157.658 -2.809)', 'Punto iniziale test', 'difficile', NULL, NULL, true, false);
INSERT INTO Itinerario VALUES(DEFAULT, 'test@test.com', 'Itinerario Test 4', 2, 'POINT(14.2681244 40.8517746)', 'Punto iniziale test', 'facile', NULL, NULL, true, true);

INSERT INTO Segnalazione VALUES('test@test.com', 2, 'Percorso inaccessibile', 'Il percorso è inaccessibile, essendo esso situato nell''Oceano Pacifico.');

INSERT INTO Chat VALUES('test@test.com', 'test2@test.com', DEFAULT);
INSERT INTO Chat VALUES('test3@test.com', 'test@test.com', DEFAULT);

INSERT INTO Messaggio VALUES(DEFAULT, 'test3@test.com', 'test@test.com', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum', now(), true);

INSERT INTO CorrezioneItinerario VALUES('test@test.com', 2, NULL, 'difficile');
INSERT INTO CorrezioneItinerario VALUES('test3@test.com', 2, 70, 'facile');