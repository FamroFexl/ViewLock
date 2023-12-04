package com.fexl.viewlock.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;

@Environment(EnvType.CLIENT)
public class ClientFrameRenderEvents {
	public static final Event<FrameRender> FRAME_RENDER = EventFactory.createArrayBacked(FrameRender.class, callbacks -> () -> {
		for(FrameRender event : callbacks) {
			InteractionResult result = event.interact();
			if(result != InteractionResult.PASS) {
				return result;
			}
		}
		return InteractionResult.PASS;
	});
	@FunctionalInterface
	
	public interface FrameRender {
		InteractionResult interact();
	}
	
	
}
