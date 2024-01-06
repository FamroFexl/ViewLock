package com.fexl.viewlock.client.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider.TextCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class CBlockPosArgument implements ArgumentType<CCoordinates> {

	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(Component.translatable("argument.pos.unloaded"));
	public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD = new SimpleCommandExceptionType(Component.translatable("argument.pos.outofworld"));
	public static final SimpleCommandExceptionType ERROR_OUT_OF_BOUNDS = new SimpleCommandExceptionType(Component.translatable("argument.pos.outofbounds"));

	public static CBlockPosArgument blockPos() {
		return new CBlockPosArgument();
	}

	public static BlockPos getLoadedBlockPos(final CommandContext<FabricClientCommandSource> context, final String name) throws CommandSyntaxException {
		ClientLevel clientWorld = context.getSource().getWorld();
		return getLoadedBlockPos(context, clientWorld, name);
	}

	public static BlockPos getLoadedBlockPos(final CommandContext<FabricClientCommandSource> context, final ClientLevel world, final String name) throws CommandSyntaxException {
		BlockPos blockPos = getBlockPos(context, name);
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (!world.getChunkSource().hasChunk(chunkPos.x, chunkPos.z)) {
			throw ERROR_NOT_LOADED.create();
		} else if (!world.isInWorldBounds(blockPos)) {
			throw ERROR_OUT_OF_WORLD.create();
		} else {
			return blockPos;
		}
	}

	public static BlockPos getBlockPos(final CommandContext<FabricClientCommandSource> context, final String name) {
		return context.getArgument(name, CCoordinates.class).getBlockPos(context.getSource());
	}

	public static BlockPos getSpawnablePos(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
		BlockPos blockPos = getBlockPos(context, name);
		if (!Level.isInSpawnableBounds(blockPos)) {
			throw ERROR_OUT_OF_BOUNDS.create();
		}
		return blockPos;
	}

	@Override
	public CCoordinates parse(final StringReader stringReader) throws CommandSyntaxException {
		return stringReader.canRead() && stringReader.peek() == '^' ? CLocalCoordinates.parse(stringReader) : CWorldCoordinates.parseInt(stringReader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		if (!(context.getSource() instanceof SharedSuggestionProvider)) {
			return Suggestions.empty();
		}
		String string = builder.getRemaining();
		Collection<SharedSuggestionProvider.TextCoordinates> collection;
		if (!string.isEmpty() && string.charAt(0) == '^') {
			collection = Collections.singleton(TextCoordinates.DEFAULT_LOCAL);
		} else {
			collection = ((SharedSuggestionProvider) context.getSource()).getRelevantCoordinates();
		}

		return SharedSuggestionProvider.suggestCoordinates(string, collection, builder, Commands.createValidator(this::parse));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
