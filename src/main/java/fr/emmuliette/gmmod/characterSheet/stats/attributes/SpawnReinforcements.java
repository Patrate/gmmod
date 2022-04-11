package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SpawnReinforcements extends AttributeStat {
	public SpawnReinforcements() {
		super(Attributes.SPAWN_REINFORCEMENTS_CHANCE, AttributeModifier.Operation.ADDITION);
	}
}