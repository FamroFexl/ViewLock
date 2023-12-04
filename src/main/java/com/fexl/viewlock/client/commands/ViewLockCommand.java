package com.fexl.viewlock.client.commands;

import com.fexl.viewlock.ViewModify;
import com.fexl.viewlock.client.commands.arguments.CAngleArgument;
import com.fexl.viewlock.client.commands.arguments.CCoordinates;
import com.fexl.viewlock.client.commands.arguments.CVec3Argument;
import com.fexl.viewlock.util.VecAlign;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ViewLockCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("vl")
			.then(literal("block")
				.then(argument("pos", CVec3Argument.vec3(true))
					.executes(commandContext -> ViewLockCommand.lockToPos(commandContext, false)
					)
					.then(literal("track")
						.executes(commandContext -> ViewLockCommand.lockToPos(commandContext, true))
					)
				)
				.then(literal("view")
					.executes(commandContext -> ViewLockCommand.lockToView(commandContext, false))
					.then(literal("track")
						.executes(commandContext -> ViewLockCommand.lockToView(commandContext, true))
					)
				)
			)
			.then(argument("pitch", CAngleArgument.angle())
					.executes(commandContext -> ViewLockCommand.viewChange(commandContext, CAngleArgument.getAngle(commandContext, "pitch", true), 0))
					.then(argument("yaw", CAngleArgument.angle())
						.executes(commandContext -> ViewLockCommand.viewChange(commandContext, CAngleArgument.getAngle(commandContext, "pitch", true), CAngleArgument.getAngle(commandContext, "yaw", false)))
					)
			)
			.then(literal("unlock")
				.executes(ViewLockCommand::unlock)
			)
			.then(literal("relock")
				.executes(ViewLockCommand::relock)
			)
		);
	}
	
	//Change the player's pitch and yaw
	private static int viewChange(CommandContext<FabricClientCommandSource> commandContext, float pitch, float yaw) throws CommandSyntaxException {
		FloatArgumentType pitchLimits = FloatArgumentType.floatArg(-90, 90);
		pitchLimits.parse(new StringReader(String.valueOf(pitch)));
		
		FloatArgumentType yawLimits = FloatArgumentType.floatArg(-180, 180);
		yawLimits.parse(new StringReader(String.valueOf(yaw)));
		
		ViewModify.lastPitch = pitch;
		ViewModify.lastYaw = yaw;
		
		//Set the view locked
		ViewModify.setPitchLocked(true);
		ViewModify.setYawLocked(true);
		ViewModify.setAxisAlignLocked(true);
		
		return 1;
	}
	
	//Unlock or relock the player's view
	private static int unlock(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		ViewModify.lastPitchLock = ViewModify.getPitchLocked();
		ViewModify.lastYawLock = ViewModify.getYawLocked();
		ViewModify.lastAxisAlignLock = ViewModify.getAxisAlignLocked();
		
		ViewModify.setPitchLocked(false);
		ViewModify.setYawLocked(false);
		ViewModify.setAxisAlignLocked(false);
		return 1;
	}
	
	private static int relock(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		ViewModify.setPitchLocked(ViewModify.lastPitchLock);
		ViewModify.setYawLocked(ViewModify.lastYawLock);
		ViewModify.setAxisAlignLocked(ViewModify.lastAxisAlignLock);
		return 1;
	}
	
	private static void calcView(Vec3 targetBlock, boolean tracking) {
		//Calculate angle from player coords (plus height)
		Vec3 playerEyePos = Minecraft.getInstance().player.position().add(new Vec3(0, Minecraft.getInstance().player.getEyeHeight(), 0));
		
		double xAngle = VecAlign.xAngleBetween(playerEyePos, targetBlock);
		double yAngle = VecAlign.yAngleBetween(playerEyePos, targetBlock);
		
		//Used for determining if the view should update every tick
		ViewModify.tracking = tracking;
		
		//The block relative arguments diverge from
		ViewModify.lastPos = targetBlock;
		
		ViewModify.lastPitch = (float) xAngle;
		ViewModify.lastYaw = (float) yAngle;
		
		ViewModify.setPitchLocked(true);
		ViewModify.setYawLocked(true);
		ViewModify.setAxisAlignLocked(true);
	}
	
	private static int lockToPos(CommandContext<FabricClientCommandSource> context, boolean tracking) throws CommandSyntaxException {
		Vec3 targetBlock = CVec3Argument.getVec3(context, "pos");
		
		//Used for calculating relative arguments
		CCoordinates coords = CVec3Argument.getCoordinates(context, "pos");
		
		double x, y, z;
		
		if(coords.isXRelative()) {
			double relativeX = coords.getPosition(context.getSource()).x() - context.getSource().getPosition().x;
			x = ViewModify.lastPos.x + relativeX;
		}
		else
			x = targetBlock.x;
		
		if(coords.isYRelative()) {
			double relativeY = coords.getPosition(context.getSource()).y() - context.getSource().getPosition().y;
			y = ViewModify.lastPos.y + relativeY;
		}
		else
			y = targetBlock.y;
		
		if(coords.isZRelative()) {
			double relativeZ = coords.getPosition(context.getSource()).z() - context.getSource().getPosition().z;
			z = ViewModify.lastPos.z + relativeZ;
		}
		else
			z = targetBlock.z;
		
		//Center of block by default on the y-axis if it is not relative
		if(y % 1 == 0 && !coords.isYRelative()) {
			y += 0.5;
		}
		
		ViewLockCommand.calcView(new Vec3(x, y, z), tracking);
		
		return 1;
	}
	
	private static int lockToView(CommandContext<FabricClientCommandSource> context, boolean tracking) throws CommandSyntaxException {
		Entity camera = Minecraft.getInstance().getCameraEntity();
	
		//Get a block in the player's crosshair
		HitResult hit = (BlockHitResult) camera.pick(1000, 0, false);
		
		switch(hit.getType()) {
			case BLOCK:
				BlockHitResult block = (BlockHitResult) hit;
				
				//Absolute block position
				BlockPos blockPos = block.getBlockPos();
				
				//Turn the BlockPos into a Vec3
				Vec3 blockVec = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
				
				//Center of block
				blockVec = blockVec.add(0.5F, 0.5F, 0.5F);
				
				ViewLockCommand.calcView(blockVec, tracking);
				
				return 1;
			default:
				return 0;
		}
	}
}
