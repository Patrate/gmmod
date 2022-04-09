package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MaxHealth extends AttributeStat {
	public MaxHealth() {
		super(Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION);
		this.setMin(0d);
	}

	@Override
	public void onChange(double oldValue, double newValue) throws StatOutOfBoundsException, MissingSheetDataException {
		super.onChange(oldValue, newValue);
		if (oldValue > newValue) {
			if (getOwner().getHealth() > getOwner().getMaxHealth())
				getOwner().setHealth(getOwner().getMaxHealth());
		}
	}
}