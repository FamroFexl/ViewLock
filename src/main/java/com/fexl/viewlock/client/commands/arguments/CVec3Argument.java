package com.fexl.viewlock.client.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class CVec3Argument implements ArgumentType<CCoordinates> {

	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("argument.pos3d.incomplete"));
	public static final SimpleCommandExceptionType MIXED_COORDINATE_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("argument.pos.mixed"));
	private final boolean centerIntegers;

	public CVec3Argument(boolean centerIntegers) {
		this.centerIntegers = centerIntegers;
	}

	public static CVec3Argument vec3() {
		return new CVec3Argument(true);
	}

	public static CVec3Argument vec3(boolean centerIntegers) {
		return new CVec3Argument(centerIntegers);
	}

	public static Vec3 getVec3(CommandContext<FabricClientCommandSource> context, String name) {
		return context.getArgument(name, CCoordinates.class).getPosition(context.getSource());
	}

	public static CCoordinates getCoordinates(CommandContext<FabricClientCommandSource> context, String name) {
		return context.getArgument(name, CCoordinates.class);
	}

	@Override
	public CCoordinates parse(final StringReader stringReader) throws CommandSyntaxException {
		return stringReader.canRead() && stringReader.peek() == '^' ? CLocalCoordinates.parse(stringReader) : CWorldCoordinates.parseDouble(stringReader, this.centerIntegers);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		if (!(context.getSource() instanceof SharedSuggestionProvider)) {
			return Suggestions.empty();
		} else {
			String string = builder.getRemaining();
			if (!string.isEmpty() && string.charAt(0) == '^') {
				final Set<SharedSuggestionProvider.TextCoordinates> singleton = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
				return SharedSuggestionProvider.suggestCoordinates(string, singleton, builder, Commands.createValidator(this::parse));
			} else {
				final Collection<SharedSuggestionProvider.TextCoordinates> positionSuggestions = ((SharedSuggestionProvider) context.getSource()).getRelevantCoordinates();
				return SharedSuggestionProvider.suggestCoordinates(string, positionSuggestions, builder, Commands.createValidator(this::parse));
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
