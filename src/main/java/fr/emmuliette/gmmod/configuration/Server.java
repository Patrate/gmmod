package fr.emmuliette.gmmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Server {
	private static final boolean defaultCreativeGM = true;

	public final ConfigValue<Boolean> creativeGM;

	public Server(ForgeConfigSpec.Builder builder) {
		builder.push("Game Master");
		this.creativeGM = builder.comment("Everyone in creative has the gm interface. Otherwise they must be made gm with a command")
				.define("Creative is GM", defaultCreativeGM);
		builder.pop();
	}
}
