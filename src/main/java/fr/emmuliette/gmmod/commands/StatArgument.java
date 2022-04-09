package fr.emmuliette.gmmod.commands;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

public class StatArgument implements ArgumentType<String> {
	public static final DynamicCommandExceptionType ERROR_UNKNOWN_STAT = new DynamicCommandExceptionType((arg) -> {
		return new TranslatableComponent("stat.notFound", arg);
	});

	public static Class<? extends Stat> getStat(CommandContext<CommandSourceStack> command, String arg)
			throws CommandSyntaxException {
		return Stat.getStat(verifyStat(command.getArgument(arg, String.class)));
	}

	private static String verifyStat(String key) throws CommandSyntaxException {
		if (getStatList().contains(key))
			return key;
		throw ERROR_UNKNOWN_STAT.create(key);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		return verifyStat(reader.readString());
	}

	@Override
	public Collection<String> getExamples() {
		return getStatList();
	}

	static Collection<String> getStatList() {
		return Stat.getRegistry();
	}

	@SuppressWarnings("hiding")
	@Override
	public <CommandSourceStack> CompletableFuture<Suggestions> listSuggestions(
			CommandContext<CommandSourceStack> command, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(getStatList(), builder);
	}

}
