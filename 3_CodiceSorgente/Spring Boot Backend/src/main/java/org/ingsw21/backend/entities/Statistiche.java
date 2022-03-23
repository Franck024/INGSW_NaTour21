package org.ingsw21.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Statistiche {
	
	private long utenteCount;
	private long utenteAccessCount;
	private long itinerarioCount;
	private long chatCount;
	private long messaggioCount;
}
