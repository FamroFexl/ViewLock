package com.fexl.viewlock;

import net.minecraft.client.player.LocalPlayer;

public class ViewModify {
	//Storage for pitch and yaw lock which remain constant between frames
	public static boolean pitchLock = false;
	public static boolean yawLock = false;
	
	//Checks if the axisAlignKey has been activated before
	public static boolean axisAlignLock = false;
	
	public static boolean lastPitchLock = false;
	public static boolean lastYawLock = false;
	public static boolean lastAxisAlignLock = false;
		
	//Used for keeping the player view static if no keys were pressed
	public static float lastPitch = 0F;
	public static float lastYaw = 0F;
	
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
			playerPitch = ViewModify.closestAxisX(playerPitch);
			playerYaw = ViewModify.closestAxisY(playerYaw);
					
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
				
		//Don't modify the pitch/yaw from the previous frame if they are locked
		if(pitchLock)
			playerPitch = lastPitch;
			player.setXRot(playerPitch);
		if(yawLock)
			playerYaw = lastYaw;
			player.setYRot(playerYaw);
	}
	
	//Figure out if player is closer to top, bottom, or center of the x axis
	public static float closestAxisX(float pitch) {
		//Closer to top
		if (pitch >= 45)
			pitch = 90;
		//Closer to bottom
		else if (pitch <= -45)
			pitch = -90;
		//Closer to middle
		else
			pitch = 0;
		
		//Return the closest x axis whether perpendicular, x+, or x-
		return pitch;
	}
	
	//Figure out which y axis direction the player's view is closest to
	public static float closestAxisY(float yaw) {
		
		//Make the yaw greater than -180 or less than 180
		float modifiedYaw = (yaw % 360);
		//Positive yaw
		if(modifiedYaw > 180 && yaw > 0)
			modifiedYaw -= 360; 
		//Negative yaw
		else if (modifiedYaw < -180 && yaw < 0)
			modifiedYaw += 360;
		
		//Closer to south
		if (modifiedYaw >= -45 && modifiedYaw < 45)
			yaw = 0;
		//Closer to west
		else if (modifiedYaw >= 45 && modifiedYaw < 135)
			yaw = 90;
		//Closer to north
		else if (modifiedYaw >= 135 || modifiedYaw < -135)
			yaw = 180;
		//Closer to east
		else if (modifiedYaw >= -135 && modifiedYaw < -45)
			yaw = -90;
		
		
		//Return the closest y axis
		return yaw;
	}
}
