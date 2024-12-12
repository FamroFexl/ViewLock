package com.fexl.viewlock.client.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.Maps;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.Util;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CEntityAnchorArgument implements ArgumentType<CEntityAnchorArgument.Anchor> {

	private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
	private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(name -> Component.translatable("argument.anchor.invalid", name));

	public static CEntityAnchorArgument anchor() {
		return new CEntityAnchorArgument();
	}

	public static Anchor getAnchor(final CommandContext<FabricClientCommandSource> context, final String name) {
		return context.getArgument(name, Anchor.class);
	}

	@Override
	public Anchor parse(final StringReader stringReader) throws CommandSyntaxException {
		int cursor = stringReader.getCursor();
		String string = stringReader.readUnquotedString();
		Anchor anchor = Anchor.getByName(string);
		if (anchor == null) {
			stringReader.setCursor(cursor);
			throw ERROR_INVALID.createWithContext(stringReader, string);
		}
		return anchor;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(Anchor.BY_NAME.keySet(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public enum Anchor {
		FEET("feet", (pos, entity) -> pos),
		EYES("eyes", (pos, entity) -> new Vec3(pos.x, pos.y + (double) entity.getEyeHeight(), pos.z));

		static final Map<String, Anchor> BY_NAME = Util.make(Maps.newTreeMap(), map -> {
			Anchor[] var1 = values();

			for (Anchor anchor : var1) {
				map.put(anchor.name, anchor);
			}
		});
		private final String name;
		private final BiFunction<Vec3, Entity, Vec3> offset;

		Anchor(String name, BiFunction<Vec3, Entity, Vec3> offset) {
			this.name = name;
			this.offset = offset;
		}

		@Nullable
		public static CEntityAnchorArgument.Anchor getByName(String id) {
			return Anchor.BY_NAME.get(id);
		}

		public Vec3 apply(Entity entity) {
			return this.offset.apply(entity.position(), entity);
		}

		public Vec3 apply(FabricClientCommandSource source) {
			Entity entity = source.getEntity();
			return entity == null ? source.getPosition() : this.offset.apply(source.getPosition(), entity);
		}
	}
}