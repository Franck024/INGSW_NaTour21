package org.ingsw21.backend.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Chat {
	
	@NonNull private String utenteOneId;
	@NonNull private String utenteTwoId;
	@NonNull private ArrayList<Messaggio> messaggi = new ArrayList<Messaggio>();

	
	public int getNumberOfMessaggio() {
		return (messaggi == null) ? 0 : messaggi.size();
	}
	
	//both input values are inclusive
	public LinkedList<Messaggio> getMessaggioInRange(int startRange, int endRange) throws IndexOutOfBoundsException{
		if (messaggi == null) return null;
		if (startRange >= endRange || startRange < 0 || endRange >= messaggi.size()) return null;
		LinkedList<Messaggio> result = new LinkedList<Messaggio>();
		for (int i = startRange; i <= endRange; i++) {
			result.add(messaggi.get(i));
		}
		return result;
	}
	
	public void addMessaggio(List<Messaggio> messaggio) {
		if (messaggio == null) messaggi = new ArrayList<Messaggio>();
		messaggi.addAll(messaggio);
	}
}
