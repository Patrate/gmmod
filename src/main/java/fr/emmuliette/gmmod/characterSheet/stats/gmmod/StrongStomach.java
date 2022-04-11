package fr.emmuliette.gmmod.characterSheet.stats.gmmod;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.configuration.Configuration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StrongStomach extends Stat {
	private static double MAX_BOOST;

	public StrongStomach() {
		super(GmMod.MOD_ID + ".StrongStomach");
		MAX_BOOST = Configuration.SERVER.strongStomachMax.get();
		this.setMin(0);
		this.setMax(10);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void eatFoodEvent(LivingEntityUseItemEvent.Finish event) {
		if (this.getOwner() instanceof Player && this.getOwner().equals(event.getEntity())
				&& event.getItem().isEdible()) {
			Player player = (Player) this.getOwner();
			FoodProperties foodproperties = event.getItem().getItem().getFoodProperties();
			double modifier = ((double) this.getValue()) * 0.1d;
			player.getFoodData().eat((int) (foodproperties.getNutrition() * ((MAX_BOOST - 1) * modifier) / 10),
					(float) (foodproperties.getSaturationModifier() * ((MAX_BOOST - 1) * modifier) / 10));
		}
	}

	@Override
	public void init() {
	}

	@Override
	public boolean isValid() {
		return getOwner() instanceof Player;
	}
}
