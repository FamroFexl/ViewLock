package com.fexl.viewlock;

import com.fexl.viewlock.util.VecAlign;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public abstract class ViewModify {
	//Storage for pitch and yaw lock which remain constant between frames
	private static boolean pitchLock = false;
	private static boolean yawLock = false;
	
	//Checks if the axisAlignKey has been activated before
	private static boolean axisAlignLock = false;
	
	public static boolean lastPitchLock = false;
	public static boolean lastYawLock = false;
	public static boolean lastAxisAlignLock = false;
		
	//Used for keeping the player view static if no keys were pressed
	public static float lastPitch = 0F;
	public static float lastYaw = 0F;
	
	public static Vec3 lastPos = new Vec3(0,0,0);
	public static boolean tracking = false;
	
	public static boolean getPitchLocked() {
		return pitchLock;
	}
	
	public static void setPitchLocked(boolean pitchLock) {
		ViewModify.pitchLock = pitchLock;
	}
	
	public static boolean getYawLocked() {
		return yawLock;
	}
	
	public static void setYawLocked(boolean yawLock) {
		ViewModify.yawLock = yawLock;
	}
	
	public static boolean getAxisAlignLocked() {
		return axisAlignLock;
	}
	
	public static void setAxisAlignLocked(boolean axisAlignLock) {
		ViewModify.axisAlignLock = axisAlignLock;
	}
	
	public static void changeView(LocalPlayer player) {
		//Get current player pitch and yaw
		float playerPitch = player.getXRot();
		float playerYaw = player.getYRot();
				
		//Set to closest pitch and yaw axes
		while (ViewLock.axisAlignKey.consumeClick()) {
			//If the axisAlignKey was pressed previously
			if(axisAlignLock == true) {
				//Unlock the player view
				pitchLock = false;
				yawLock = false;
				axisAlignLock = false;
						
				break;
			}
			//Get the closest axes to the player
			playerPitch = VecAlign.closestAxisX(playerPitch);
			playerYaw = VecAlign.closestAxisY(playerYaw);
					
			//Set the pitch and yaw to the last value when locked
			lastPitch = playerPitch;
			lastYaw = playerYaw;
					
			//Make sure it flips to the closest axes and stays
			pitchLock = true;
			yawLock = true;
					
			axisAlignLock = true;
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
		
		if(tracking) {
			Vec3 playerEyePos = player.position().add(new Vec3(0, player.getEyeHeight(), 0));
			lastPitch = (float) VecAlign.xAngleBetween(playerEyePos, ViewModify.lastPos);
			lastYaw = (float) VecAlign.yAngleBetween(playerEyePos, ViewModify.lastPos);
		}
				
		//Don't modify the pitch/yaw from the previous frame if they are locked
		if(pitchLock)
			playerPitch = lastPitch;
			player.setXRot(playerPitch);
		if(yawLock)
			playerYaw = lastYaw;
			player.setYRot(playerYaw);
	}
}
