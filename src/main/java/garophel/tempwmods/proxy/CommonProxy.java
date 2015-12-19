package garophel.tempwmods.proxy;

import garophel.tempwmods.RecipeModify;
import garophel.tempwmods.event.HarvestDropsHandler;
import garophel.tempwmods.event.LivingAttackHandler;
import garophel.tempwmods.event.LivingHurtHandler;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	public LivingHurtHandler livingHurtHandler = null;
	
	public void init() {
//		MinecraftForge.EVENT_BUS.register(new AttackEntityHandler());
		MinecraftForge.EVENT_BUS.register(livingHurtHandler = new LivingHurtHandler());
		MinecraftForge.EVENT_BUS.register(new LivingAttackHandler());
		MinecraftForge.EVENT_BUS.register(new HarvestDropsHandler());
		
		GameRegistry.addRecipe(new RecipeModify());
	}
	
}
