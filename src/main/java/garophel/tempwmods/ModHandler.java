package garophel.tempwmods;

import garophel.tempwmods.event.LivingHurtHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;

public abstract class ModHandler {
	
	/** @return number of uses used up */
	public int handle(LivingAttackEvent e, EntityLivingBase source, EntityLivingBase target, float damage, ModDataWrapper data) {
		return Integer.MAX_VALUE;
	}
	
	public int handleBlock(BlockEvent.HarvestDropsEvent e, ModDataWrapper data) {
		return Integer.MAX_VALUE;
	}
	
	public abstract boolean itemMatches(ItemStack modifier);
	
	public abstract boolean canModify(ItemStack target, int modifierCount);
	
	public void modify(ItemStack target, ItemStack modifier, int modifierCount, Modifier mod) {
		NBTTagCompound nbt = Modifier.addModtag(target);
		Modifier.writeModData_uses(nbt, mod, 1, 16);
	}
	
	protected void trapDamage(EntityLivingBase target, float newDamage) {
		LivingHurtHandler h = TempWMods.proxy.livingHurtHandler;
		h.target = target;
		h.targetTickCount = target.ticksExisted;
		h.newDamage = newDamage;
	}
	
	@SideOnly(Side.CLIENT)
	public void tooltip(Modifier m, ItemTooltipEvent e, NBTTagCompound modtag) {
		e.toolTip.add(getTooltipStyle(m) + m.name + ": " + modtag.getInteger(Modifier.TAG_USES) + " Uses left" + EnumChatFormatting.RESET);
	}
	
	@SideOnly(Side.CLIENT)
	public String getTooltipStyle(Modifier m) {
		return EnumChatFormatting.WHITE.toString();
	}
	
	//// Helper methods to simplify modifier handler code ////
	
	// TODO: handle all the things properly
	protected void handleSuccessHit(EntityLivingBase source, EntityLivingBase target, float damage) {
		int knockbackLevel = EnchantmentHelper.getKnockbackModifier(source, (EntityLivingBase)target);
		float enchDamage = EnchantmentHelper.getEnchantmentModifierLiving(source, (EntityLivingBase)target);
		int fireAspectLevel = EnchantmentHelper.getFireAspectModifier(source);
		
		if(source instanceof EntityPlayer) {
			EntityPlayer plr = (EntityPlayer) source;
			
			boolean shouldCrit = plr.fallDistance > 0.0F && !plr.onGround && !plr.isOnLadder() && !plr.isInWater() && !plr.isPotionActive(Potion.blindness) && plr.ridingEntity == null && target instanceof EntityLivingBase;
			
			if(knockbackLevel > 0) {
				target.addVelocity((double)(-MathHelper.sin(plr.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackLevel * 0.5F), 0.1D, (double)(MathHelper.cos(plr.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackLevel * 0.5F));
				plr.motionX *= 0.6D;
				plr.motionZ *= 0.6D;
				plr.setSprinting(false);
			}

			if(shouldCrit) {
				plr.onCriticalHit(target);
			}

			if(enchDamage > 0.0F) {
				plr.onEnchantmentCritical(target);
			}

			if(damage >= 18.0F) {
				plr.triggerAchievement(AchievementList.overkill);
			}

			plr.setLastAttacker(target);

			EnchantmentHelper.func_151384_a((EntityLivingBase)target, plr);

			EnchantmentHelper.func_151385_b(plr, target);
			ItemStack itemstack = plr.getCurrentEquippedItem();

			if(itemstack != null) {
				itemstack.hitEntity(target, plr);

				if(itemstack.stackSize <= 0) {
					plr.destroyCurrentEquippedItem();
				}
			}

			plr.addStat(StatList.damageDealtStat, Math.round(damage * 10.0F));

			if(fireAspectLevel > 0) {
//					System.out.println("target-fire: " + target + ", level: " + fireAspectLevel);
				target.setFire(fireAspectLevel * 4);
			}

			plr.addExhaustion(0.3F);
		} else if(source instanceof EntityMob) {
			if(knockbackLevel > 0) {
				target.addVelocity((double)(-MathHelper.sin(source.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackLevel * 0.5F), 0.1D, (double)(MathHelper.cos(source.rotationYaw * (float)Math.PI / 180.0F) * (float)knockbackLevel * 0.5F));
				source.motionX *= 0.6D;
				source.motionZ *= 0.6D;
			}
			
			if(fireAspectLevel > 0) {
				target.setFire(fireAspectLevel * 4);
			}
			
			EnchantmentHelper.func_151384_a(target, source);
			EnchantmentHelper.func_151385_b(source, target);
		}
	}
	
	protected boolean tryRemoveBlock(World world, EntityPlayer plr, int x, int y, int z, boolean voidDrops) {
		Block up = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		
		boolean removed = false;
		
		if(up != null && up != Blocks.air) {
			boolean canHarvest = false;
			
			if(!plr.capabilities.isCreativeMode) {
				canHarvest = up.canHarvestBlock(plr, meta);
			}
			
			if(canHarvest && (removed = removeBlock(world, plr, x, y, z, up, meta, canHarvest)) && !voidDrops) {
				up.harvestBlock(world, plr, x, y, z, meta);
			}
		}
		
		return removed;
	}
	
	protected boolean removeBlock(World world, EntityPlayer plr, int x, int y, int z, Block block, int meta, boolean canHarvest) {
		boolean removed = block.removedByPlayer(world, plr, x, y, z, canHarvest);
		
		if(removed) {
			block.onBlockDestroyedByPlayer(world, x, y, z, meta);
		}
		
		return removed;
	}
	
}
