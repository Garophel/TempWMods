package garophel.tempwmods;

import garophel.tempwmods.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=Ref.MODID, name = Ref.MOD_NAME, version = Ref.VERSION)
public class TempWMods {
	
	@SidedProxy(clientSide=Ref.CLIENT_PROXY, serverSide=Ref.COMMON_PROXY)
	public static CommonProxy proxy;
	
	@Mod.Instance(value=Ref.MODID)
	public static TempWMods instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init();
	}
}
