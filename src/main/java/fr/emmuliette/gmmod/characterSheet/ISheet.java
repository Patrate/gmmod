package fr.emmuliette.gmmod.characterSheet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface ISheet {

	public Entity getOwner();

	public void setOwner(Entity owner);

	public int getCount();
	
	public void addCount();
	
	public CompoundTag toNBT();

	public void fromNBT(CompoundTag nbt);

	public void sync(ServerPlayer player);

	public void sync(ISheet player);

	public void sync();
}
