package fr.emmuliette.gmmod.commands.stat;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.exceptions.MissingStatException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

/* Registered in ModEventListener */
public class StatCommand {
	private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(
			new TranslatableComponent("commands.experience.set.points.invalid"));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("stats").then(Commands.literal("list").executes((command) -> {
			return listStats(command.getSource());
		})).then(Commands.literal("set")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.argument("stat", new StatArgument()).then(
								Commands.argument("amount", IntegerArgumentType.integer()).suggests((command, text) -> {
									return SharedSuggestionProvider.suggest(StatArgument.getStatList(), text);
								}).executes((command) -> {
									return setStat(command.getSource(), EntityArgument.getPlayers(command, "targets"),
											StatArgument.getStat(command, "stat"),
											IntegerArgumentType.getInteger(command, "amount"));
								})))))
				.then(Commands.literal("get").executes((command) -> {
					return getStat(command.getSource());
				}).then(Commands.argument("target", EntityArgument.player()).executes((command) -> {
					return getStat(command.getSource(), EntityArgument.getPlayer(command, "target"));
				}).then(Commands.argument("stat", new StatArgument()).suggests((command, text) -> {
					return SharedSuggestionProvider.suggest(StatArgument.getStatList(), text);
				}).executes((command) -> {
					return getStat(command.getSource(), EntityArgument.getPlayer(command, "target"),
							StatArgument.getStat(command, "stat"));
				})))));
	}

	private static int listStats(CommandSourceStack context) {
		context.sendSuccess(new TextComponent(Stat.getRegistry().toString()), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setStat(CommandSourceStack context, Collection<? extends ServerPlayer> targets,
			Class<? extends Stat> stat, int amount) throws CommandSyntaxException {
		int i = 0;
		GmMod.logger().info("Setting stat " + stat + " to " + amount);

		for (ServerPlayer serverplayer : targets) {
			CharacterSheet sheet = serverplayer.getCapability(CharacterSheet.SHEET_CAPABILITY).orElse(null);
			if (sheet == null) {
				GmMod.logger().warn("No sheet found for target " + serverplayer);
				continue;
			}
			try {
				sheet.getStat(stat).setValue(amount);
				++i;
			} catch (MissingStatException e) {
				// TODO send error to caller
				e.printStackTrace();
			}
		}

		if (i == 0) {
			GmMod.logger().warn("i == 0");
			throw ERROR_SET_POINTS_INVALID.create();
		} else {
			if (targets.size() == 1) {
				context.sendSuccess(new TextComponent("Set " + stat + " to " + amount),
						// new TranslatableComponent("commands.experience.set." + "POINTS " +
						// ".success.single", amount,
						// targets.iterator().next().getDisplayName()),
						true);
			} else {
				context.sendSuccess(new TextComponent("Set " + stat + " to " + amount),
						// new TranslatableComponent("commands.experience.set." + " POINTS " +
						// ".success.multiple", amount, targets.size()),
						true);
			}

			return targets.size();
		}
	}

	private static int getStat(CommandSourceStack context) {
		try {
			return getStat(context, context.getPlayerOrException(), null);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static int getStat(CommandSourceStack context, ServerPlayer target) {
		return getStat(context, target, null);
	}

	private static int getStat(CommandSourceStack context, ServerPlayer target, Class<? extends Stat> stat) {
		CharacterSheet sheet = target.getCapability(CharacterSheet.SHEET_CAPABILITY).orElse(null);
		if (sheet == null) {
			context.sendFailure(new TranslatableComponent("commands.stats.get.failure"));
			return Command.SINGLE_SUCCESS;
		}
		StringBuilder result = new StringBuilder();
		if (stat == null) {
			for (Stat s : sheet.getStats()) {
				if (s.getValue() != 0)
					result.append(s.getClass().getSimpleName() + " " + s.getValue() + "\n");
			}
		} else {
			try {
				result.append(stat.getSimpleName() + " " + sheet.getStat(stat).getValue());
			} catch (MissingStatException e) {
				// TODO send error to caller
				e.printStackTrace();
			}
		}
		context.sendSuccess(new TextComponent(result.toString()), false);
		return Command.SINGLE_SUCCESS;
	}
}
