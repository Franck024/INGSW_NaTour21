package org.ingsw21.backend.entities;


import org.ingsw21.backend.enums.DifficoltaItinerario;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class Itinerario {
	
	@NonNull private long id;
	@NonNull private String authorId;
	@NonNull private String nome;
	private Double puntoInizialeLat;
	private Double puntoInizialeLong;
	@NonNull private String nomePuntoIniziale;
	private int durata;
	@NonNull private DifficoltaItinerario difficoltaItinerario;
	private String descrizione;
	@JsonProperty("tracciatoKey")
	private String tracciatoKey;
	@JsonProperty("isAccessibleMobilityImpairment")
	private Boolean isAccessibleMobilityImpairment;
	@JsonProperty("isAccessibleVisualImpairment")
	private Boolean isAccessibleVisualImpairment;
}
