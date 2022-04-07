package fr.emmuliette.gmmod.characterSheet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;

public class SheetTickEvent extends TickEvent {
	public final LivingEntity owner;
	public final CharacterSheet sheet;

	public SheetTickEvent(TickEvent tickEvent, CharacterSheet sheet) {
		super(Type.PLAYER, tickEvent.side, tickEvent.phase);
		this.owner = sheet.getOwner();
		this.sheet = sheet;
	}

}
