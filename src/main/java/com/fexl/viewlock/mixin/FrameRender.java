package com.fexl.viewlock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fexl.viewlock.ViewLock;
import com.fexl.viewlock.ViewModify;
import com.fexl.viewlock.event.ClientFrameRenderEvents;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

@Mixin(Minecraft.class)
public class FrameRender {

	//Activated every frame per second
	@Inject(method = "Lnet/minecraft/client/Minecraft;runTick(Z)V", at = @At("HEAD"))
	public void screenRender(CallbackInfo info) {
		InteractionResult result = ClientFrameRenderEvents.FRAME_RENDER.invoker().interact();
	}
}
