package org.ingsw21.backend.entities;

import lombok.NonNull;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Messaggio {

	@NonNull private long id;
	@NonNull private String testo;
	@NonNull private OffsetDateTime timestamp;
	@JsonProperty("isUtenteOneSender")
	private boolean isUtenteOneSender;
	
}
