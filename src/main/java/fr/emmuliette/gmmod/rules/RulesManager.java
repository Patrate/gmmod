package fr.emmuliette.gmmod.rules;

import java.util.Arrays;
import java.util.List;

import fr.emmuliette.gmmod.GmMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "gmmod")
public class RulesManager extends SavedData {
	private static RulesManager RULES;

	// Différencier ce qui peut juste être dans la config et ce qui peut *aussi*
	// être une règle
	// Eg la config pour décider qui peut être MJ, c'est pas le MJ qui la change.
	public abstract class Rule<T> {
		private String name;
		private String description;

		public Rule(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public abstract T getValue();
	}

	public class Choice {
		private String value;
		private int id;

		public Choice(String value, int id) {
			this.setValue(value);
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private class RuleChoice extends Rule<Choice> {
		private List<String> args;
		private int value;

		RuleChoice(String name, String description, String... args) {
			super(name, description);
			this.args = Arrays.asList(args);
			this.value = 0;
		}

		public Choice getValue() {
			return new Choice(args.get(value), value);
		}

		public void setValue(int id) {
			this.value = id;
			setDirty();
		}
	}

	private class RuleBool extends Rule<Boolean> {
		private boolean value;

		RuleBool(String name, String description) {
			super(name, description);
			this.value = true;
		}

		public Boolean getValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
			setDirty();
		}
	}

	public final RuleChoice LEVELINGMODE = new RuleChoice("Leveling mode", "How do players gain class level?"
			+ "\n*Same as level: The class level is the same as the minecraft level. Can't multiclass"
			+ "\n*Class point on level up: When a player level up, they gain a class point to spend on a class. Can multiclass."
			+ "\n*Manual: Players can't gain class point, they must be given manually by the game master.",
			"Same as level", "Class point on level up", "Manual");

	public final RuleBool MULTICLASS = new RuleBool("Multiclass", "Can players have multiple class ?"
			+ "\nIf a player have multiple class, they must choose which class to spend a \"class point\" on to gain a level.");

	private void init(ServerLevel level) {
		level.getDataStorage().computeIfAbsent(this::load, this::create, "gmmodrules");
	}

	private RulesManager create() {
		setDirty();
		return this;
	}

	private RulesManager load(CompoundTag tag) {
		GmMod.logger().info("Loading config");
		this.LEVELINGMODE.setValue(tag.getInt(RULES.LEVELINGMODE.getName().toLowerCase()));
		this.MULTICLASS.setValue(tag.getBoolean(RULES.MULTICLASS.getName().toLowerCase()));
		GmMod.logger().info("Config loaded");
		return this;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		GmMod.logger().info("Saving config");
		tag.putInt(this.LEVELINGMODE.getName().toLowerCase(), this.LEVELINGMODE.getValue().id);
		tag.putBoolean(this.MULTICLASS.getName().toLowerCase(), this.MULTICLASS.getValue());
		GmMod.logger().info("Config saved");
		return tag;
	}

	@SubscribeEvent
	public static void loadWorld(WorldEvent.Load event) {
		if (event.getWorld().getServer() == null) {
			return;
		}
		if (event.getWorld().equals(event.getWorld().getServer().overworld())) {
			RULES = new RulesManager();
			RULES.init(event.getWorld().getServer().overworld());
		}
	}

}
