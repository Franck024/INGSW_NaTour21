package org.ingsw21.backend.entities;

import lombok.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("isAdmin")
	@NonNull private boolean isAdmin;
}
