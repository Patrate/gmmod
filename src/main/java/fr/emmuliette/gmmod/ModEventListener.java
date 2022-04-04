package fr.emmuliette.gmmod;

import fr.emmuliette.gmmod.commands.StatCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventListener {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		StatCommand.register(event.getDispatcher());
	}
}
