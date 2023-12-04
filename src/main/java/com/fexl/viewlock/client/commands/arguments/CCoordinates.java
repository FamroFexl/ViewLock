package com.fexl.viewlock.client.commands.arguments;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface CCoordinates {

	public Vec3 getPosition(FabricClientCommandSource source);

	public Vec2 getRotation(FabricClientCommandSource source);

	default BlockPos getBlockPos(FabricClientCommandSource source) {
		return new BlockPos(this.getPosition(source));
	}

	public boolean isXRelative();

	public boolean isYRelative();

	public boolean isZRelative();
}
