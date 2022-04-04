package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Luck extends AttributeStat {
	public Luck() {
		super(Attributes.LUCK, AttributeModifier.Operation.ADDITION);
	}
}