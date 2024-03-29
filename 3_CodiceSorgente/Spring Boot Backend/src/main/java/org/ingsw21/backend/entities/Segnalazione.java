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
public class Segnalazione {

	@NonNull private String authorId;
	@NonNull private long itinerarioId;
	@NonNull private String titolo;
	private String descrizione;
}
