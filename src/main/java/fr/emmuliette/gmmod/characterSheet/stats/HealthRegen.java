package fr.emmuliette.gmmod.characterSheet.stats;

import java.util.HashMap;
import java.util.Map;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class HealthRegen extends Stat {
	public HealthRegen() {
		super(GmMod.MOD_ID + ".HealthRegen");
	}

	@Override
	public void onChange(double oldValue, double newValue) {
		if (newValue < 0 || newValue > 10) {
			// TODO throw error
			return;
		}
		if (newValue == 0) {
			HealthRegenListener.removePlayer((Player) this.getOwner());
		} else if (oldValue == 0) {
			HealthRegenListener.registerPlayer((Player) this.getOwner(), newValue);
		} else {
			HealthRegenListener.updatePlayer((Player) this.getOwner(), newValue - oldValue);
		}
	}

	@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class HealthRegenListener {
		private static final Map<Player, Integer> playerMap = new HashMap<Player, Integer>();
		private static final double MAX_VAL = 200., STEP_VAL = 20.;

		@SubscribeEvent
		public static void regenTickCommands(PlayerTickEvent event) {
			if (playerMap.containsKey(event.player)) {
				CharacterSheet sheet = event.player.getCapability(CharacterSheet.SHEET_CAPABILITY).orElse(null);
				if (sheet == null) {
					// TODO throw error;
					playerMap.remove(event.player);
					return;
				}
				int val = playerMap.get(event.player) - 1;
				if (val <= 0) {
					event.player.heal(1f);
					val = (int) (MAX_VAL - STEP_VAL * sheet.getStat(HealthRegen.class).getValue());
				}
				playerMap.put(event.player, val);
			}
		}

		static void removePlayer(Player p) {
			playerMap.remove(p);
		}

		static void registerPlayer(Player p, double level) {
			playerMap.put(p, (int) (MAX_VAL - STEP_VAL * level));
		}

		static void updatePlayer(Player p, double diff) {
			playerMap.put(p, playerMap.get(p) - (int) (STEP_VAL * diff));
		}

	}

}
