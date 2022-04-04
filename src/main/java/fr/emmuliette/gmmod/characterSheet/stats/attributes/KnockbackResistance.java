package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class KnockbackResistance extends AttributeStat {
	public KnockbackResistance() {
		super(Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADDITION);
	}
}