package com.fexl.viewlock.client.commands;

import com.fexl.viewlock.ViewModify;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class DegreeLockCommand extends CommandBase {
	
	@Override
	public String getCommandName() {
		return "vl";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "command.vl.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		//No arguments
		if(args.length < 1) {
			throw new WrongUsageException("commands.vl.usage", new Object[0]);
		}
		
		//Has pitch argument
		else if(args.length == 1) {
			Float arg1 = Float.parseFloat(args[0]);
			
			if(arg1 < -90 || arg1 > 90) {
				throw new CommandException("commands.vl.pitchTooLarge", new Object[] {Integer.valueOf(-90), Integer.valueOf(90)});
			}
			else {
				DegreeLockCommand.run(arg1, 0f);
				return;
			}
		}
		
		//Has pitch and yaw argument
		else if(args.length == 2) {
			Float arg1 = Float.parseFloat(args[0]);
			Float arg2 = Float.parseFloat(args[1]);
			
			if(arg1 < -90 || arg1 > 90) {
				throw new CommandException("commands.vl.pitchTooLarge", new Object[] {Integer.valueOf(-90), Integer.valueOf(90)});
			}
			if(arg2 < -180 || arg2 > 180) {
				throw new CommandException("commands.vl.yawTooLarge", new Object[] {Integer.valueOf(-180), Integer.valueOf(180)});
			}
			else {
				DegreeLockCommand.run(arg1, arg2);
				return;
			}
		}
		
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	
	private static void run(Float pitch, Float yaw) {
		ViewModify.lastPitch = pitch; 
		ViewModify.lastYaw = yaw;
		
		ViewModify.pitchLock = true;
		ViewModify.yawLock = true;
		ViewModify.axisAlignLock = true;
	}
	
	
}
