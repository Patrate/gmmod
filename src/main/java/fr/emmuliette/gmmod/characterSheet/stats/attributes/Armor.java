package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Armor extends AttributeStat {
	public Armor() {
		super(Attributes.ARMOR, AttributeModifier.Operation.ADDITION);
	}
}