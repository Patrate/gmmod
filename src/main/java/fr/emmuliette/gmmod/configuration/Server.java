package fr.emmuliette.gmmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Server {
	private static final boolean defaultCreativeGM = true;
	private static final double defaultDefaultRegenTimer = 10., defaultRegenLevelStep = 1.;

	public final ConfigValue<Boolean> creativeGM;
	public final ConfigValue<Double> defaultRegenTimer, regenLevelStep;

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
		builder.pop();
	}
}
