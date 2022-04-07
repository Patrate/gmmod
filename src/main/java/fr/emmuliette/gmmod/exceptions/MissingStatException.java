package fr.emmuliette.gmmod.exceptions;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;

public class MissingStatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6863609629313137462L;

	public MissingStatException(CharacterSheet sheet, Class<? extends Stat> statClass) {
		super("Stat " + statClass.getSimpleName() + " is missing from sheet of " + sheet.getOwner());
	}
}
