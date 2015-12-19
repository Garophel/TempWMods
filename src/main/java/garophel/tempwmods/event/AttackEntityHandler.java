package garophel.tempwmods.event;

import garophel.tempwmods.Modifier;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class AttackEntityHandler {
	
	public void onAttackEntity(AttackEntityEvent e) {
		ItemStack item = null;
		if((item = e.entityPlayer.getCurrentEquippedItem()) != null && !e.target.worldObj.isRemote) {
			if(item.getItem() instanceof ItemSword && e.target instanceof EntityLivingBase) {
				if(item.stackTagCompound != null && item.stackTagCompound.hasKey(Modifier.MODIFIER_TAG)) {
					NBTTagCompound modtag = item.stackTagCompound.getCompoundTag(Modifier.MODIFIER_TAG);
//					modtag = Modifier.handleModifier(e, modtag);
					
					if(modtag == null) {
						item.stackTagCompound.removeTag(Modifier.MODIFIER_TAG);
					}
				}
				
//				EntityLivingBase tgt = (EntityLivingBase) e.target;
//				tgt.setFire(2 * amp);
//				tgt.attackEntityFrom(DamageSource.causePlayerDamage(e.entityPlayer), 2f);
//				System.out.println("New health: " + tgt.getHealth());
			}
		}
	}
}
