package com.fexl.viewlock;

import org.lwjgl.input.Keyboard;

import com.fexl.viewlock.client.commands.DegreeLockCommand;
import com.fexl.viewlock.event.ScreenRender;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ViewLock.MODID, name = ViewLock.NAME, version = ViewLock.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "1.8-1.8.9")
public class ViewLock
{
    public static final String MODID = "viewlock";
    public static final String VERSION = "1.1.0";
    public static final String NAME = "View Lock";
    
	public static KeyBinding[] keyBindings;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    		
    		//Create key bindings
    		ViewLock.keyBindings = new KeyBinding[3];
    		keyBindings[0] = new KeyBinding("key.viewlock.axisalign.name", Keyboard.KEY_Y, "key.viewlock.category");
    		keyBindings[1] = new KeyBinding("key.viewlock.pitch.name", Keyboard.KEY_U, "key.viewlock.category");
    		keyBindings[2] = new KeyBinding("key.viewlock.yaw.name", Keyboard.KEY_I, "key.viewlock.category");
    		
    		//Register key bindings
    		for(int i = 0; i < keyBindings.length; i++) {
    			ClientRegistry.registerKeyBinding(keyBindings[i]);
    		}
    		
    		//Register /vl command
    		ClientCommandHandler.instance.registerCommand(new DegreeLockCommand());
    		
    		//Register events
    		MinecraftForge.EVENT_BUS.register(new ScreenRender());
    }
}
