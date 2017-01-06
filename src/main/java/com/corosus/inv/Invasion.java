package com.corosus.inv;

import modconfig.ConfigMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import com.corosus.inv.config.ConfigAdvancedOptions;
import com.corosus.inv.config.ConfigAdvancedSpawning;
import com.corosus.inv.config.ConfigInvasion;

@Mod(modid = "hw_inv", name="HW_Invasions", version="v0.1", acceptableRemoteVersions="*", dependencies="required-after:coroutil")
public class Invasion {
	
	@Mod.Instance( value = "hw_inv" )
	public static Invasion instance;
	public static String modID = "hw_inv";
    
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		ConfigMod.addConfigFile(event, new ConfigInvasion());
		ConfigMod.addConfigFile(event, new ConfigAdvancedSpawning());
		ConfigMod.addConfigFile(event, new ConfigAdvancedOptions());
    }
    
	@Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(new EventHandlerForge());
		FMLCommonHandler.instance().bus().register(new EventHandlerForge());
    }
    
    @Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
    	event.registerServerCommand(new CommandInvasion());
    }
    
    @Mod.EventHandler
    public void serverStart(FMLServerStartedEvent event) {
    	
    }
    
    @Mod.EventHandler
    public void serverStop(FMLServerStoppedEvent event) {
    	
    }

}
