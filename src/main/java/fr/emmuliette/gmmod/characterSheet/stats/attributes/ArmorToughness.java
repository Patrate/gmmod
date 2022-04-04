package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ArmorToughness extends AttributeStat {
	public ArmorToughness() {
		super(Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADDITION);
	}
}