package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MovementSpeed extends AttributeStat {
	public MovementSpeed() {
		super(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADDITION, 50);
	}
}