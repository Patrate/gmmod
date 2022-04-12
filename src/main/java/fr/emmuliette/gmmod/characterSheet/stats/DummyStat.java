package fr.emmuliette.gmmod.characterSheet.stats;

import fr.emmuliette.gmmod.exceptions.InvalidStatException;
import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;

public class DummyStat extends Stat {

	public DummyStat(Class<? extends Stat> statClass) {
		super(Stat.toKey(statClass));
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void init() throws StatOutOfBoundsException, MissingSheetDataException, InvalidStatException {
	}

}
