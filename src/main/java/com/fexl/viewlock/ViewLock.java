package com.fexl.viewlock;



import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class ViewLock implements ClientModInitializer {

	//Three main keys
	public static KeyMapping axisAlignKey;
	public static KeyMapping pitchKey;
	public static KeyMapping yawKey;
	
	@Override
	public void onInitializeClient() {
		axisAlignKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.viewlock.axisalign.name", 
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Y, 
				"key.viewlock.category"
				));
		
		pitchKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.viewlock.pitch.name",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_U,
				"key.viewlock.category"
				));
		
		yawKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.viewlock.yaw.name",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_I,
				"key.viewlock.category"
				));
	}

}
