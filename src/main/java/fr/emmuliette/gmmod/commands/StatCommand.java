package fr.emmuliette.gmmod.commands;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.exceptions.MissingStatException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class StatCommand {
	private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(
			new TranslatableComponent("commands.experience.set.points.invalid"));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("stats").then(Commands.literal("list").executes((command) -> {
			return listStats(command.getSource());
		})).then(Commands.literal("set")
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.argument("stat", new StatArgument())
								.then(Commands.argument("amount", DoubleArgumentType.doubleArg())
										/*
										 * .then(Commands.createValidator(new ParseFunction() {
										 * 
										 * @Override public void parse(StringReader p_82161_) throws
										 * CommandSyntaxException {
										 * 
										 * } }))
										 */

										.executes((command) -> {
											return setStat(command.getSource(),
													EntityArgument.getPlayers(command, "targets"),
													StatArgument.getStat(command, "stat"),
													DoubleArgumentType.getDouble(command, "amount"));
										})))))
				.then(Commands.literal("get")
						.then(Commands.argument("target", EntityArgument.player()).executes((command) -> {
							return getStat(command.getSource(), EntityArgument.getPlayer(command, "target"));
						}).then(Commands.argument("stat", new StatArgument()).executes((command) -> {
							return getStat(command.getSource(), EntityArgument.getPlayer(command, "target"),
									StatArgument.getStat(command, "stat"));
						})))));
	}

	private static int listStats(CommandSourceStack context) {
		context.sendSuccess(new TextComponent(Stat.getRegistry().toString()), false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setStat(CommandSourceStack context, Collection<? extends ServerPlayer> targets,
			Class<? extends Stat> stat, double amount) throws CommandSyntaxException {
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
