package com.fexl.viewlock.util;

import net.minecraft.world.phys.Vec3;

public class VecAlign {
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
		//float modifiedYaw = (yaw % 360);
		float modifiedYaw = yaw;
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
	
	//Calculate the pitch between two positions
	public static double xAngleBetween(Vec3 from, Vec3 to) {
		double angle = Math.toDegrees(
				/* Right-angle triangle TOA*/
				Math.atan(
						/* Opposite (height) */
						Math.abs(from.y-to.y)/
						/* Adjacent (length) (Distance formula) */
						Math.sqrt(Math.pow(from.z-to.z, 2) + Math.pow(from.x-to.x, 2))));
		
		if(from.y > to.y) {
			angle = angle;
		}
		else if(from.y < to.y) {
			angle = -angle;
		}
		
		return angle;
	}
	
	//Calculate the yaw between two positions
	public static double yAngleBetween(Vec3 from, Vec3 to) {
		return Math.toDegrees(Math.atan2(from.z-to.z, from.x-to.x)) + 90;
	}
}
