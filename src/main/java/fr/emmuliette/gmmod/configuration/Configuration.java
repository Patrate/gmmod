package fr.emmuliette.gmmod.configuration;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class Configuration {

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final Common SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	public static final Common CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	static // constructor
	{
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();

		Pair<Common, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		SERVER = serverSpecPair.getLeft();
		SERVER_SPEC = serverSpecPair.getRight();

		Pair<Common, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		CLIENT = clientSpecPair.getLeft();
		CLIENT_SPEC = clientSpecPair.getRight();
	}
}
