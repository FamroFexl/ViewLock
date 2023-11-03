package com.fexl.viewlock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.fexl.viewlock.ViewModify;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;

@Mixin(ClientPacketListener.class)
public class PlayerTeleport {
	
	private float lastTime = 0;
	
	@Inject(method = "handleMovePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerPositionPacket;)V", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void playerTeleport(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket, CallbackInfo info) {
		LocalPlayer player = Minecraft.getInstance().player;
		
		//Don't activate when not in a world or on a public server
		if(player == null || Minecraft.getInstance().isLocalServer() || Minecraft.getInstance().getCurrentServer().isLan()) {
			return;
		}
				
		//More than 1 tick between teleport requests so "Player moved too quickly!" warnings don't accumulate
		if(Minecraft.getInstance().level.getGameTime() < lastTime+2) {
			lastTime = Minecraft.getInstance().level.getGameTime();
			return;
		}
		lastTime = Minecraft.getInstance().level.getGameTime();
				
				
		//The player's view isn't locked, so unlocking is unecessary
		if(!ViewModify.getPitchLocked() && !ViewModify.getYawLocked() && !ViewModify.getAxisAlignLocked()) {
			return;
		}
		
		ViewModify.lastPitchLock = ViewModify.getPitchLocked();
		ViewModify.lastYawLock = ViewModify.getYawLocked();
		ViewModify.lastAxisAlignLock = ViewModify.getAxisAlignLocked();
		
		ViewModify.setPitchLocked(false);
		ViewModify.setYawLocked(false);
		ViewModify.setAxisAlignLocked(false);
		
		
		String hoverText = "A teleportation command automatically disabled your locked view!";
		
		//Inform the user their view has been unlocked
		player.sendMessage(new TextComponent("")
				.append(new TextComponent("View unlocked! Click ")
						.withStyle(style -> style
								.withColor(ChatFormatting.RED)
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(hoverText)))
						)
				)
				.append(new TextComponent("here")
						.withStyle(style -> style
								.withColor(ChatFormatting.BLUE)
								.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vl relock"))
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Reset view")))
						)
				)
				.append(new TextComponent(" to restore lock.")
						.withStyle(style -> style
								.withColor(ChatFormatting.RED)
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(hoverText)))
						)
				)
		,player.getUUID());
	}
}
