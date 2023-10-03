package com.fexl.viewlock;

import org.lwjgl.glfw.GLFW;

import com.fexl.viewlock.client.commands.DegreeLockCommand;
import com.fexl.viewlock.event.ScreenRender;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.client.KeyMapping;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ViewLock.MODID)
public class ViewLock
{
    public static final String MODID = "viewlock";
    public static final String VERSION = "1.1.0";
    public static final String NAME = "View Lock";
    
	public static KeyMapping[] keyBindings;
    
	public ViewLock() 
	{
		//Create key bindings 
		ViewLock.keyBindings = new KeyMapping[3];
		keyBindings[0] = new KeyMapping("key.viewlock.axisalign.name", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, "key.viewlock.category");
		keyBindings[1] = new KeyMapping("key.viewlock.pitch.name", GLFW.GLFW_KEY_U, "key.viewlock.category");
		keyBindings[2] = new KeyMapping("key.viewlock.yaw.name", GLFW.GLFW_KEY_I, "key.viewlock.category");
		
		//Register events
    	MinecraftForge.EVENT_BUS.register(ScreenRender.class);
	}
	
	@Mod.EventBusSubscriber(modid = ViewLock.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents {
    	@SubscribeEvent
    	public static void commandRegister(RegisterClientCommandsEvent event) {
    		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
    		
    		//Register /vl command
    		DegreeLockCommand.register(dispatcher);
    	}
    }
}
