package com.fexl.viewlock.event;

import com.fexl.viewlock.ViewModify;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScreenRender {

	@SubscribeEvent
	public static void screenRender(RenderTickEvent event) {
		//Get the client player
		Player player = Minecraft.getInstance().player;
		
		//Make sure the player is in a world
		if(player == null)
			return;
		
		//Modify the player's view
		ViewModify.changeView(player);
	}
}
