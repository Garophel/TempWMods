package garophel.tempwmods.modifiers;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import garophel.tempwmods.ModDataWrapper;
import garophel.tempwmods.ModHandler;
import garophel.tempwmods.Modifier;

public class ModAOE extends ModHandler {

	@Override
	@SuppressWarnings("unchecked")
	public int handle(LivingAttackEvent e, EntityLivingBase source, EntityLivingBase target, float damage, ModDataWrapper data) {
		double area = 2d;
		List<EntityLivingBase> entities = target.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(target.posX - area, target.posY - area, target.posZ - area, target.posX + area, target.posY + area, target.posZ + area));
		for(Iterator<EntityLivingBase> iter = entities.iterator(); iter.hasNext();) {
			EntityLivingBase ent = iter.next();
			if(ent == source || ent == target) {
				iter.remove();
			}
		}
		
		damage = damage / (1f + ((float) entities.size()) * 0.4f);
		if(damage < 2f) {
			damage = 2f;
		}
		
		int hit = 0;
		for(EntityLivingBase ent : entities) {
//			System.out.println("uses: " + uses + ", hit: " + hit);
//			System.out.println("target: " + target + ", source: " + source + ", ent: " + ent + ", isSource: " + (ent == source));
			
			if(hit >= data.getUses()) {
				break;
			} else {
				if(ent != source && ent != target) {
					if(ent.attackEntityFrom(source instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer) source) : DamageSource.causeMobDamage(source), damage)) {
//						System.out.println("handle success!");
						handleSuccessHit(source, ent, damage);
//						ent.setFire(4);
						hit++;
					}
				}
			}
		}
		
//		e.setCanceled(true);
		trapDamage(target, damage);
		return hit;
	}

	@Override
	public boolean itemMatches(ItemStack modifier) {
		return modifier.getItem() == Items.nether_star;
	}

	@Override
	public boolean canModify(ItemStack target, int modifierCount) {
		return modifierCount == 1 && (target.getItem() instanceof ItemSword || target.getItem() instanceof ItemAxe);
	}

	@Override
	public void modify(ItemStack target, ItemStack modifier, int modifierCount, Modifier mod) {
		NBTTagCompound nbt = Modifier.addModtag(target);
		Modifier.writeModData_uses(nbt, mod, 1, target.getMaxDamage() - target.getItemDamage() + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTooltipStyle(Modifier m) {
		return EnumChatFormatting.WHITE.toString() + EnumChatFormatting.UNDERLINE.toString();
	}
	
}
