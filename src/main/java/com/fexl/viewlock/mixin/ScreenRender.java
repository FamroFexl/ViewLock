package com.fexl.viewlock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fexl.viewlock.ViewModify;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Mixin(Minecraft.class)
public class ScreenRender {

	//Activated every frame per second
	@Inject(method = "Lnet/minecraft/client/Minecraft;runTick(Z)V", at = @At("HEAD"))
	public void screenRender(CallbackInfo info) {
		//Get the client player (fix resource leak)
		LocalPlayer player = Minecraft.getInstance().player;

		//Don't activate when not in a world
		if(player == null)
			return;
		
		ViewModify.changeView(player);
	}
}
