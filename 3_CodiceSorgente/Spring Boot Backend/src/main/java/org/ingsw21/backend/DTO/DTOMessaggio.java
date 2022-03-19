package org.ingsw21.backend.DTO;

import org.ingsw21.backend.entities.Messaggio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DTOMessaggio {
	
	private String sender;
	private Messaggio messaggio;
}
