package garophel.tempwmods;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeModify implements IRecipe {
	
	private ItemStack target = null, modifier = null;
	private int modifierCount = 1;
	private Modifier mod = null;

	@Override
	public boolean matches(InventoryCrafting cinv, World world) {
		target = null;
		modifier = null;
		modifierCount = 1;
		
		ItemStack a = null, b = null;
		
		for(int i = 0; i < cinv.getSizeInventory(); i++) {
			ItemStack s = cinv.getStackInSlot(i);
			if(s != null) {
				if(ItemStack.areItemStacksEqual(a, s)) {
					ItemStack t = a;
					a = b;
					b = t;
					
					modifierCount++;
				} else if(ItemStack.areItemStacksEqual(b, s)) {
					modifierCount++;
				} else if(a == null) {
					a = s;
				} else if(b == null) {
					b = s;
				} else {
					return false;
				}
			}
		}
		
		if(a != null && b != null) {
			if(isOrderInverse(a, b)) {
				target = b;
				modifier = a;
			} else {
				target = a;
				modifier = b;
			}
			
			return recipeValid(target, modifier);
		}
		
		return false;
	}
	
	private boolean recipeValid(ItemStack target, ItemStack modifier) {
		mod = Modifier.getModForStack(modifier);
		if(mod != null) {
			return mod.handler.canModify(target, modifierCount);
		}
		
		return false;
	}
	
	private boolean isOrderInverse(ItemStack target, ItemStack modifier) {
		if(target.getItem() instanceof ItemSword || target.getItem() instanceof ItemTool) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean hasActiveModifier(ItemStack s) {
		if(s.stackTagCompound != null) {
			return s.stackTagCompound.hasKey(Modifier.MODIFIER_TAG);
		}
		
		return false;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting cinv) {
		if(target != null && modifier != null && !hasActiveModifier(target)) {
			ItemStack tgt = target.copy();
			
			//writeModData(modtag, Modifier.DAMAGE, 3, 711);
			mod.handler.modify(tgt, modifier, modifierCount, mod);
//			writeModData(modtag, Modifier.FLAME, 3, 4);
			
			return tgt;
		}
		
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
