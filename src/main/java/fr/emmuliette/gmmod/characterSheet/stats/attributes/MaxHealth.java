package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import fr.emmuliette.gmmod.exceptions.InvalidStatException;
import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MaxHealth extends AttributeStat {
	public MaxHealth() {
		super(Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public void onChange(int oldValue, int newValue)
			throws StatOutOfBoundsException, MissingSheetDataException, InvalidStatException {
		super.onChange(oldValue, newValue);
		if (oldValue > newValue) {
			if (getOwner().getHealth() > getOwner().getMaxHealth())
				getOwner().setHealth(getOwner().getMaxHealth());
		}
	}
}