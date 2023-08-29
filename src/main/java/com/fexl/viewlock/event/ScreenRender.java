package com.fexl.viewlock.event;

import com.fexl.viewlock.ViewModify;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ScreenRender {

	@SubscribeEvent
	public void screenRender(RenderTickEvent event) {
		//Get the client player
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		//Make sure the player is in a world
		if(player == null)
			return;
		
		//Modify the player's view
		ViewModify.changeView(player);
	}
}
