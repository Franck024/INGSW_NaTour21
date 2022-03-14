package org.ingsw21.backend.entities;

import lombok.NonNull;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Utente {

	@NonNull private String email;
	@NonNull private String nome;
	@NonNull private String cognome;
	private String cellulare;
	private String citta;
	@NonNull private boolean isAdmin;
}
