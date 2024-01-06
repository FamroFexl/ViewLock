package com.fexl.viewlock.client.commands.arguments;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface CCoordinates {

	public Vec3 getPosition(FabricClientCommandSource source);

	public Vec2 getRotation(FabricClientCommandSource source);

	default BlockPos getBlockPos(FabricClientCommandSource source) {
		return new BlockPos((int)this.getPosition(source).x, (int)this.getPosition(source).y, (int)this.getPosition(source).z);
	}

	public boolean isXRelative();

	public boolean isYRelative();

	public boolean isZRelative();
}
