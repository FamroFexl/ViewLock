package com.fexl.viewlock;

import org.lwjgl.glfw.GLFW;

import com.fexl.viewlock.client.commands.DegreeLockCommand;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.CommandBuildContext;

public class ViewLock implements ClientModInitializer {
	
	//Main keys
	public static KeyMapping axisAlignKey;
	public static KeyMapping pitchKey;
	public static KeyMapping yawKey;
	
	@Override
	public void onInitializeClient() {
		//Register client-side commands
		ClientCommandRegistrationCallback.EVENT.register(ViewLock::registerClientCommands);
		
		//Defines the axis-align key (default "Y")
		axisAlignKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.viewlock.axisalign.name", 
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Y, 
				"key.viewlock.category"
				));
		
		//Defines the pitch lock key (default "U")
		pitchKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.viewlock.pitch.name",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_U,
				"key.viewlock.category"
				));
		
		//Defines the yaw lock key (default "I")
		yawKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.viewlock.yaw.name",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_I,
				"key.viewlock.category"
				));
	}
	
	//Function for client-side command registering
	public static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		DegreeLockCommand.register(dispatcher);
	}

}
