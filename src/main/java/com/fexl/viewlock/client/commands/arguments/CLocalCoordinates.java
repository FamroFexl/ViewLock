package com.fexl.viewlock.client.commands.arguments;

import java.util.Objects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CLocalCoordinates implements CCoordinates {
	public static final char PREFIX_LOCAL_COORDINATE = '^';
	private final double left;
	private final double up;
	private final double forwards;

	public CLocalCoordinates(double x, double y, double z) {
		this.left = x;
		this.up = y;
		this.forwards = z;
	}

	public Vec3 getPosition(FabricClientCommandSource source) {
		Vec2 rotation = source.getRotation();
		Vec3 pos = CEntityAnchorArgument.Anchor.FEET.apply(source);
		final float PiDividedBy180 = 0.017453292F;
		float f = Mth.cos((rotation.y + 90.0F) * PiDividedBy180);
		float g = Mth.sin((rotation.y + 90.0F) * PiDividedBy180);
		float h = Mth.cos(-rotation.x * PiDividedBy180);
		float i = Mth.sin(-rotation.x * PiDividedBy180);
		float j = Mth.cos((-rotation.x + 90.0F) * PiDividedBy180);
		float k = Mth.sin((-rotation.x + 90.0F) * PiDividedBy180);
		Vec3 vec3d2 = new Vec3((f * h), i, (g * h));
		Vec3 vec3d3 = new Vec3((f * j), k, (g * j));
		Vec3 vec3d4 = vec3d2.cross(vec3d3).multiply(-1.0D, -1.0D, -1.0D);
		double d = vec3d2.x * this.forwards + vec3d3.x * this.up + vec3d4.x * this.left;
		double e = vec3d2.y * this.forwards + vec3d3.y * this.up + vec3d4.y * this.left;
		double l = vec3d2.z * this.forwards + vec3d3.z * this.up + vec3d4.z * this.left;
		return new Vec3(pos.x + d, pos.y + e, pos.z + l);
	}

	public Vec2 getRotation(FabricClientCommandSource source) {
		return Vec2.ZERO;
	}

	public boolean isXRelative() {
		return true;
	}

	public boolean isYRelative() {
		return true;
	}

	public boolean isZRelative() {
		return true;
	}

	public static CLocalCoordinates parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		double x = readDouble(reader, cursor);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			double y = readDouble(reader, cursor);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				double z = readDouble(reader, cursor);
				return new CLocalCoordinates(x, y, z);
			} else {
				reader.setCursor(cursor);
				throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
			}
		} else {
			reader.setCursor(cursor);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}

	private static double readDouble(StringReader reader, int startingCursorPos) throws CommandSyntaxException {
		if (!reader.canRead()) {
			throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(reader);
		} else if (reader.peek() != '^') {
			reader.setCursor(startingCursorPos);
			throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(reader);
		}
		reader.skip();
		return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof CLocalCoordinates lookingPosArgument)) {
			return false;
		} else {
			return this.left == lookingPosArgument.left && this.up == lookingPosArgument.up && this.forwards == lookingPosArgument.forwards;
		}
	}

	public int hashCode() {
		return Objects.hash(this.left, this.up, this.forwards);
	}
}
