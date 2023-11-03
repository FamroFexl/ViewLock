package com.fexl.viewlock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.fexl.viewlock.ViewModify;
import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;

@Mixin(ClientPacketListener.class)
public class PlayerTeleport {
	
	private float lastTime = 0;
	
	@Inject(method = "handleMovePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerPositionPacket;)V", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void playerTeleport(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket, CallbackInfo info) {
		LocalPlayer player = Minecraft.getInstance().player;
		//clientboundPlayerPositionPacket.
		
		//Don't activate when not in a world
		if(player == null) {
			return;
		}
		
		//More than 1 tick between teleport requests so "Player moved too quickly!" warnings don't accumulate
		if(Minecraft.getInstance().level.getGameTime() < lastTime+2) {
			lastTime = Minecraft.getInstance().level.getGameTime();
			return;
		}
		lastTime = Minecraft.getInstance().level.getGameTime();
		
		ViewModify.lastPitchLock = ViewModify.getPitchLocked();
		ViewModify.lastYawLock = ViewModify.getYawLocked();
		ViewModify.lastAxisAlignLock = ViewModify.getAxisAlignLocked();
		
		ViewModify.setPitchLocked(false);
		ViewModify.setYawLocked(false);
		ViewModify.setAxisAlignLocked(false);
		
		
		String hoverText = "A teleportation command automatically disabled your locked view!";
		
		//Inform the user their view has been unlocked
		player.displayClientMessage(MutableComponent.create(ComponentContents.EMPTY)
				.append(MutableComponent.create(new LiteralContents("View unlocked! Click "))
						.withStyle(style -> style
								.withColor(ChatFormatting.RED)
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, MutableComponent.create(ComponentContents.EMPTY).append(hoverText)))
						)
				)
				.append(MutableComponent.create(new LiteralContents("here"))
						.withStyle(style -> style
								.withColor(ChatFormatting.BLUE)
								.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vl relock"))
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, MutableComponent.create(ComponentContents.EMPTY).append("Reset view")))
						)
				)
				.append(MutableComponent.create(new LiteralContents(" to restore lock."))
						.withStyle(style -> style
								.withColor(ChatFormatting.RED)
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, MutableComponent.create(ComponentContents.EMPTY).append(hoverText)))
						)
				)
		, false);
	}
}