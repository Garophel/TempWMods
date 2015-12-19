package garophel.tempwmods.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LivingHurtHandler {
	
	public EntityLivingBase target = null;
	public int targetTickCount = -1;
	public float newDamage = -1;
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent e) {
		if(!e.entity.worldObj.isRemote) {
			if(e.entityLiving == target && e.entityLiving.ticksExisted == targetTickCount) {
				if(newDamage == -1) {
					e.setCanceled(true);
				} else {
					e.ammount = newDamage;
				}
			}
			
			target = null;
			
			System.out.println("dmg amount: " + e.ammount);
		}
	}
}
