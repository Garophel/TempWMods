package garophel.tempwmods.event;

import garophel.tempwmods.Modifier;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class LivingAttackHandler {
	
	private boolean handling = false;
	
	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent e) {
		if(!handling && !e.entityLiving.worldObj.isRemote) {
			handling = true;
			
			if(e.source.getEntity() instanceof EntityLivingBase) {
				EntityLivingBase el = (EntityLivingBase) e.source.getEntity();
				ItemStack item = el.getHeldItem();
				if(item != null && item.stackTagCompound != null && item.stackTagCompound.hasKey(Modifier.MODIFIER_TAG)) {
					NBTTagCompound modtag = item.stackTagCompound.getCompoundTag(Modifier.MODIFIER_TAG);
					modtag = Modifier.handleModifier(e, el, e.entityLiving, e.ammount, modtag);
					
					if(modtag == null) {
						item.stackTagCompound.removeTag(Modifier.MODIFIER_TAG);
					}
				}
			}
			
			handling = false;
		}
	}
}
