package com.fexl.viewlock.client.commands;

import com.fexl.viewlock.ViewModify;
import com.fexl.viewlock.mixin.PlayerTeleport;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public class DegreeLockCommand {
	
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("vl")
		.executes(null)
				.then(argument("relock", StringArgumentType.word()).executes(DegreeLockCommand::relock))
				.then(argument("pitch", FloatArgumentType.floatArg(-90, 90))
				.executes(DegreeLockCommand::run)
						.then(argument("yaw", FloatArgumentType.floatArg(-180, 180))
						.executes(DegreeLockCommand::run)))
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
		ViewModify.setPitchLocked(true);
		ViewModify.setYawLocked(true);
		ViewModify.setAxisAlignLocked(true);
		
		return 1;
	}
	
	public static int relock(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
		//System.out.println("Relock executed with command \"" + StringArgumentType.getString(context, "relock") + "\"");
		if(StringArgumentType.getString(context, "relock").equals("relock")) {
			System.out.println("Relock string gotten!");
			ViewModify.setPitchLocked(ViewModify.lastPitchLock);
			ViewModify.setYawLocked(ViewModify.lastYawLock);
			ViewModify.setAxisAlignLocked(ViewModify.lastAxisAlignLock);
			return 1;
		}
		return 0;
	}
}
