package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FlyingSpeed extends AttributeStat {
	public FlyingSpeed() {
		super(Attributes.FLYING_SPEED, AttributeModifier.Operation.ADDITION);
	}
}