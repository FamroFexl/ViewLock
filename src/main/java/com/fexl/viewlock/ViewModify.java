package com.fexl.viewlock;

import net.minecraft.client.player.LocalPlayer;

public class ViewModify {
	
	//Only activated if viewlock is on
	public static void viewChange(LocalPlayer player, float pitch, float yaw, boolean pitchLock, boolean yawLock) {
		
		//Set the player pitch only if it is locked
		if (pitchLock)
			player.setXRot(pitch);
		
		//Set the player yaw only if it is locked
		if (yawLock)
			player.setYRot(yaw);
		
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
