package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MaxHealth extends AttributeStat {
	public MaxHealth() {
		super(Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION);
	}
}