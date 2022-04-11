package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class JumpStrength extends AttributeStat {
	public JumpStrength() {
		super(Attributes.JUMP_STRENGTH, AttributeModifier.Operation.ADDITION);
	}
}