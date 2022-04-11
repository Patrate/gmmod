package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FollowRange extends AttributeStat {
	public FollowRange() {
		super(Attributes.FOLLOW_RANGE, AttributeModifier.Operation.ADDITION);
	}
}