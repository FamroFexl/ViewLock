package com.fexl.viewlock.client.commands;

import com.fexl.viewlock.ViewModify;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class DegreeLockCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("vl")
				.then(Commands.argument("pitch", FloatArgumentType.floatArg(-90, 90))
				.executes(commandContext -> DegreeLockCommand.run(commandContext))
						.then(Commands.argument("yaw", FloatArgumentType.floatArg(-180, 180))
						.executes(commandContext -> DegreeLockCommand.run(commandContext)))));
	}
	
	public static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
}
