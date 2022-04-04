package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttackKnockback extends AttributeStat {
	public AttackKnockback() {
		super(Attributes.ATTACK_KNOCKBACK, AttributeModifier.Operation.ADDITION);
	}
}