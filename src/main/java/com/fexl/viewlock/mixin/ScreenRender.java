package com.fexl.viewlock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fexl.viewlock.ViewLock;
import com.fexl.viewlock.ViewModify;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Mixin(Minecraft.class)
public class ScreenRender {
	//Storage for pitch and yaw lock which remain constant between frames
	private boolean pitchLock = false;
	private boolean yawLock = false;
	
	//Used for keeping the player view static if no keys were pressed
	private float lastPitch = 0F;
	private float lastYaw = 0F;
	
	//Activated every frame per second
	@Inject(method = "Lnet/minecraft/client/Minecraft;runTick(Z)V", at = @At("HEAD"))
	public void screenRender(CallbackInfo info) {
		//Get the client player (fix resource leak)
		LocalPlayer player = Minecraft.getInstance().player;

		//Don't activate when not in a world
		if(player == null)
			return;
		
		//Get current player pitch and yaw
		float playerPitch = player.getXRot();
		float playerYaw = player.getYRot();
		
		//Set to closest pitch and yaw axes
		while (ViewLock.axisAlignKey.consumeClick()) {
			//Get the closest axes to the player
			playerPitch = ViewModify.closestAxisX(playerPitch);
			playerYaw = ViewModify.closestAxisY(playerYaw);
			
			//Set the pitch and yaw to the last value when locked
			lastPitch = playerPitch;
			lastYaw = playerYaw;
			
			//Make sure it flips to the closest axes and stays
			pitchLock = true;
			yawLock = true;
		}
		
		//Toggle the pitch lock
		while (ViewLock.pitchKey.consumeClick()) {
			if(pitchLock) {
				pitchLock = false;
			} else {
				pitchLock = true;
				lastPitch = playerPitch;
			}
		}
		
		//Toggle the yaw lock
		while (ViewLock.yawKey.consumeClick()) {
			if (yawLock) {
				yawLock = false;
			} else {
				yawLock = true;
				lastYaw = playerYaw;
			}
		}
		
		//Don't modify the pitch/yaw from the previous frame if they are locked
		if(pitchLock)
			playerPitch = lastPitch;
		if(yawLock)
			playerYaw = lastYaw;
		
		
		//Change the player pitch and yaw
		ViewModify.viewChange(player, playerPitch, playerYaw, pitchLock, yawLock);
	}
}
