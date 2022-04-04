package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttackDamage extends AttributeStat {
	public AttackDamage() {
		super(Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION);
	}
}