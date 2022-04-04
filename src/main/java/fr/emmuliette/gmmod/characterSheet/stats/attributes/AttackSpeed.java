package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttackSpeed extends AttributeStat {
	public AttackSpeed() {
		super(Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADDITION);
	}
}