package fr.emmuliette.gmmod.commands;

import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import net.minecraft.commands.CommandSourceStack;
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
		System.out.println("CHECKING HERE: " + key + " return " + Stat.getStat(key));
		if (Stat.getStat(key) != null)
			return key;
		throw ERROR_UNKNOWN_STAT.create(key);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		return verifyStat(reader.readString());
	}

	@Override
	public Collection<String> getExamples() {
		// return Arrays.asList(new String[] {"aaa"});
		return Stat.getRegistry();
	}

}
