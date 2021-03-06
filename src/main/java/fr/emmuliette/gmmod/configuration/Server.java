package fr.emmuliette.gmmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Server {
	private static final boolean defaultCreativeGM = true;
	private static final int defaultDefaultRegenTimer = 10, defaultRegenLevelStep = 1, defaultStrongStomachMax = 15;

	public final ConfigValue<Boolean> creativeGM;
	public final ConfigValue<Integer> defaultRegenTimer, regenLevelStep, strongStomachMax;

	public Server(ForgeConfigSpec.Builder builder) {
		builder.push("Game Master");
		this.creativeGM = builder
				.comment("Everyone in creative has the gm interface. Otherwise they must be made gm with a command")
				.define("Creative is GM", defaultCreativeGM);
		builder.pop();

		builder.push("Stats");
		this.defaultRegenTimer = builder.comment("How long a level 1 regen stat take to heal one hearth in seconds.")
				.define("At level 1, heal every _ seconds", defaultDefaultRegenTimer);
		this.regenLevelStep = builder.comment("How many seconds are shaved of for each regen level")
				.define("Every level make regen _ seconds faster", defaultRegenLevelStep);
		this.strongStomachMax = builder.comment("What's the max food bonus for strong stomach stat ?")
				.define("At level 10, food input is multiplied by _", defaultStrongStomachMax);
		builder.pop();
	}
}
