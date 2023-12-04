package com.fexl.viewlock.client.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CWorldCoordinates implements CCoordinates {

	private final WorldCoordinate x;
	private final WorldCoordinate y;
	private final WorldCoordinate z;

	public CWorldCoordinates(WorldCoordinate x, WorldCoordinate y, WorldCoordinate z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3 getPosition(FabricClientCommandSource source) {
		Vec3 vec3d = source.getPosition();
		return new Vec3(this.x.get(vec3d.x), this.y.get(vec3d.y), this.z.get(vec3d.z));
	}

	public Vec2 getRotation(FabricClientCommandSource source) {
		Vec2 vec2f = source.getRotation();
		return new Vec2((float) this.x.get(vec2f.x), (float) this.y.get(vec2f.y));
	}

	public boolean isXRelative() {
		return this.x.isRelative();
	}

	public boolean isYRelative() {
		return this.y.isRelative();
	}

	public boolean isZRelative() {
		return this.z.isRelative();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof CWorldCoordinates defaultPosArgument)) {
			return false;
		} else {
			if (this.x.equals(defaultPosArgument.x)) {
				return this.y.equals(defaultPosArgument.y) && this.z.equals(defaultPosArgument.z);
			}
			return false;
		}
	}

	public static CWorldCoordinates parseInt(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		WorldCoordinate worldCoordinate = WorldCoordinate.parseInt(reader);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			WorldCoordinate coordinateArgument2 = WorldCoordinate.parseInt(reader);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				WorldCoordinate coordinateArgument3 = WorldCoordinate.parseInt(reader);
				return new CWorldCoordinates(worldCoordinate, coordinateArgument2, coordinateArgument3);
			} else {
				reader.setCursor(cursor);
				throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
			}
		} else {
			reader.setCursor(cursor);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}

	public static CWorldCoordinates parseDouble(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		WorldCoordinate worldCoordinate = WorldCoordinate.parseDouble(reader, centerIntegers);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			WorldCoordinate coordinateArgument2 = WorldCoordinate.parseDouble(reader, false);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				WorldCoordinate coordinateArgument3 = WorldCoordinate.parseDouble(reader, centerIntegers);
				return new CWorldCoordinates(worldCoordinate, coordinateArgument2, coordinateArgument3);
			} else {
				reader.setCursor(cursor);
				throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
			}
		} else {
			reader.setCursor(cursor);
			throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(reader);
		}
	}

	public static CWorldCoordinates absolute(double x, double y, double z) {
		return new CWorldCoordinates(new WorldCoordinate(false, x), new WorldCoordinate(false, y), new WorldCoordinate(false, z));
	}

	public static CWorldCoordinates absolute(Vec2 vec) {
		return new CWorldCoordinates(new WorldCoordinate(false, (double)vec.x), new WorldCoordinate(false, (double)vec.y), new WorldCoordinate(true, 0.0D));
	}

	public static CWorldCoordinates current() {
		return new CWorldCoordinates(new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D));
	}

	@Override
	public int hashCode() {
		int hash = this.x.hashCode();
		hash = 31 * hash + this.y.hashCode();
		hash = 31 * hash + this.z.hashCode();
		return hash;
	}
}
