package garophel.tempwmods;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import garophel.tempwmods.modifiers.ModAOE;
import garophel.tempwmods.modifiers.ModFlame;
import garophel.tempwmods.modifiers.ModPowerPick;
import garophel.tempwmods.modifiers.ModQuartz;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;


public enum Modifier {
	DAMAGE(0, "Quartz", new ModQuartz()),
	FLAME(1, "Flaming", new ModFlame()),
	AOE(2, "AoE", new ModAOE()),
	POWERPICK(3, "Breaker", new ModPowerPick());
	// MOST DEBUFFS
	// AOE - many uses, each hit mob drains one use, somewhat expensive, 3x3x3 around the hit entity
	// LIGHTNING
	// transformation modifiers
	
	public final int type;
	public final String name;
	public final ModHandler handler;
	
	private Modifier(int type, String name, ModHandler handler) {
		this.type = type;
		this.name = name;
		this.handler = handler;
	}
	
	private int handle(LivingAttackEvent e, EntityLivingBase source, EntityLivingBase target, float damage, ModDataWrapper data) {
		return handler.handle(e, source, target, damage, data);
	}
	
	public static final String MODIFIER_TAG = "tempwmods_mod";
	public static final String TAG_ID = "id";
	public static final String TAG_AMPLIFIER = "amp";
	public static final String TAG_USES = "uses";
	public static final String TAG_TIME = "time";
	
	private static final Modifier[] modifiers = Modifier.class.getEnumConstants();
	
	public static NBTTagCompound handleModifier(HarvestDropsEvent e, EntityPlayer plr, NBTTagCompound nbt) {
		if(nbt == null) {
			return null;
		}
		
		int id = nbt.getInteger(TAG_ID);
		int uses = nbt.getInteger(TAG_USES);
		
		ModDataWrapper wrap = new ModDataWrapper(nbt);
		
		Modifier mod = getFromId(id);
		if(mod != null && wrap.valid) {
			if(uses < -200) {
				int time = nbt.getInteger(TAG_TIME) - 1;
				if(time <= 0) {
					return null;
				} else {
					nbt.setInteger(TAG_TIME, time);
					return nbt;
				}
				
			} else {
				uses -= mod.handler.handleBlock(e, wrap);
				if(uses <= 0) {
					return null;
				} else {
					nbt.setInteger(TAG_USES, uses);
					return nbt;
				}
			}
		}
		
		return nbt;
	}
	
	public static NBTTagCompound handleModifier(LivingAttackEvent e, EntityLivingBase source, EntityLivingBase target, float damage, NBTTagCompound nbt) {
		if(nbt == null) {
			return null;
		}
		
		int id = nbt.getInteger(TAG_ID);
		int amp = nbt.getInteger(TAG_AMPLIFIER);
		int uses = nbt.getInteger(TAG_USES);
		
		Modifier mod = getFromId(id);
		if(mod != null) {
			if(uses < -200) {
				int time = nbt.getInteger(TAG_TIME) - 1;
				if(time <= 0) {
					return null;
				} else {
					nbt.setInteger(TAG_TIME, time);
					return nbt;
				}
				
			} else {
				uses -= mod.handle(e, source, target, damage, new ModDataWrapper(nbt));
				if(uses <= 0) {
					return null;
				} else {
					nbt.setInteger(TAG_USES, uses);
					return nbt;
				}
			}
		}
		
		return nbt;
	}
	
	public static Modifier getFromId(int id) {
		if(id >= 0 && id < modifiers.length) {
			return modifiers[id];
		}
		
		return null;
	}

	public static Modifier getModForStack(ItemStack modifier) {
		for(Modifier m : modifiers) {
			if(m.handler.itemMatches(modifier)) {
				return m;
			}
		}
		
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static void defaultTooltip(Modifier m, ItemTooltipEvent e, NBTTagCompound modtag, EnumChatFormatting color) {
		e.toolTip.add((color != null ? color : EnumChatFormatting.GRAY) + m.name + ": " + modtag.getInteger(Modifier.TAG_USES) + " Uses left" + EnumChatFormatting.RESET);
	}
	
	public static NBTTagCompound addModtag(ItemStack target) {
		NBTTagCompound modtag = null;
		if(target.stackTagCompound == null) {
			modtag = new NBTTagCompound();
			
			target.stackTagCompound = new NBTTagCompound();
			target.stackTagCompound.setTag(Modifier.MODIFIER_TAG, modtag);
		} else {
			if(!target.stackTagCompound.hasKey(Modifier.MODIFIER_TAG)) {
				modtag = new NBTTagCompound();
				target.stackTagCompound.setTag(Modifier.MODIFIER_TAG, modtag);
			} else {
				modtag = target.stackTagCompound.getCompoundTag(Modifier.MODIFIER_TAG);
			}
		}
		
		return modtag;
	}
	
	public static void writeModData_uses(NBTTagCompound nbt, Modifier mod, int amplifier, int uses) {
		nbt.setInteger(Modifier.TAG_ID, mod.type);
		nbt.setInteger(Modifier.TAG_AMPLIFIER, amplifier);
		nbt.setInteger(Modifier.TAG_USES, uses);
	}
	
	/** Maybe, in future. Much work */
	private static void writeModData_time(NBTTagCompound nbt, Modifier mod, int amplifier, int time) {
		nbt.setInteger(Modifier.TAG_ID, mod.type);
		nbt.setInteger(Modifier.TAG_AMPLIFIER, amplifier);
		nbt.setInteger(Modifier.TAG_USES, -222);
		nbt.setInteger(Modifier.TAG_TIME, time);
	}
	
}
