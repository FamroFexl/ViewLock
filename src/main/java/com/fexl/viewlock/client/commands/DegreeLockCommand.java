package com.fexl.viewlock.client.commands;

import com.fexl.viewlock.ViewModify;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class DegreeLockCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("vl")
		.executes(null)
			.then(argument("pitch", FloatArgumentType.floatArg(-90, 90))
			.executes(DegreeLockCommand::run)
					.then(argument("yaw", FloatArgumentType.floatArg(-180, 180))
					.executes(DegreeLockCommand::run)))
			.then(argument("lock", StringArgumentType.word()).executes(DegreeLockCommand::lock))
		);
	}
	
	public static int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		//Set pitch to command input
		ViewModify.lastPitch = FloatArgumentType.getFloat(context, "pitch");
		
		//Check for single command argument
		if(context.getNodes().size() < 3) {
			//Set yaw to default
			ViewModify.lastYaw = 0F;
		}
		else {
			//Set yaw to command input
			ViewModify.lastYaw = FloatArgumentType.getFloat(context, "yaw");
		}
		
		//Set the view locked
		ViewModify.pitchLock = true;
		ViewModify.yawLock = true;
		ViewModify.axisAlignLock = true;
		
		return 1;
	}
	
	//Unlock or relock the player's view
	public static int lock(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		if(StringArgumentType.getString(context, "lock").equals("relock")) {
			ViewModify.setPitchLocked(ViewModify.lastPitchLock);
			ViewModify.setYawLocked(ViewModify.lastYawLock);
			ViewModify.setAxisAlignLocked(ViewModify.lastAxisAlignLock);
			return 1;
		}
		
		if(StringArgumentType.getString(context, "lock").equals("unlock")) {
			ViewModify.lastPitchLock = ViewModify.getPitchLocked();
			ViewModify.lastYawLock = ViewModify.getYawLocked();
			ViewModify.lastAxisAlignLock = ViewModify.getAxisAlignLocked();
			
			ViewModify.setPitchLocked(false);
			ViewModify.setYawLocked(false);
			ViewModify.setAxisAlignLocked(false);
			return 1;
		}
		return 0;
	}
}
