package fr.emmuliette.gmmod.characterSheet;

import fr.emmuliette.gmmod.SyncHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class SheetImpl implements ISheet {
	private Entity owner;
	private int logCount;

	@Override
	public Entity getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Entity owner) {
		this.owner = owner;
	}

	@Override
	public CompoundTag toNBT() {
		return null;
	}

	@Override
	public void fromNBT(CompoundTag nbt) {
		
	}

	@Override
	public void sync(ServerPlayer player) {
		player.getCapability(SheetCapability.SHEET_CAPABILITY).ifPresent(c -> c.sync());
	}

	@Override
	public void sync(ISheet player) {
		/*this.power = caster.getPower();
		this.currentMana = caster.getMana();
		this.maxMana = caster.getMaxMana();
		this.manaRegen = caster.getManaRegen();
		this.currentManaCd = caster.getManaRegenTick();
		this.grimoire = caster.getGrimoire();*/
	}

	@Override
	public void sync() {
		if (getOwner() instanceof ServerPlayer) {
			SyncHandler.sendTo(new SheetPacket(this.toNBT()), (ServerPlayer) getOwner());
		}
	}

	@Override
	public int getCount() {
		return logCount;
	}

	@Override
	public void addCount() {
		logCount += 1;
	}

}
