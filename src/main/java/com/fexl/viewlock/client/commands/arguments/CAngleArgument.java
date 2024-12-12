package com.fexl.viewlock.client.commands.arguments;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class CAngleArgument implements ArgumentType<CAngleArgument.SingleAngle> {

	private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
	public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.angle.incomplete"));
	public static final SimpleCommandExceptionType ERROR_INVALID_ANGLE = new SimpleCommandExceptionType(Component.translatable("argument.angle.invalid"));

	public static CAngleArgument angle() {
		return new CAngleArgument();
	}

	public static float getAngle(final CommandContext<FabricClientCommandSource> context, final String name, boolean pitch) {
		return context.getArgument(name, SingleAngle.class).getAngle(context.getSource(), pitch);
	}

	@Override
	public SingleAngle parse(final StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw ERROR_NOT_COMPLETE.createWithContext(stringReader);
		}
		boolean relative = WorldCoordinate.isRelative(stringReader);
		float angle = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readFloat() : 0.0F;
		if (!Float.isNaN(angle) && !Float.isInfinite(angle)) {
			return new SingleAngle(angle, relative);
		}
		throw ERROR_INVALID_ANGLE.createWithContext(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static final class SingleAngle {
		private final float angle;
		private final boolean isRelative;

		SingleAngle(float angle, boolean relative) {
			this.angle = angle;
			this.isRelative = relative;
		}

		public float getAngle(FabricClientCommandSource source, boolean pitch) {
			if(pitch) {
				return Mth.wrapDegrees(this.isRelative ? this.angle + source.getRotation().x : this.angle);
			}
			return Mth.wrapDegrees(this.isRelative ? this.angle + source.getRotation().y : this.angle);
		}
	}
}
