package garophel.tempwmods.modifiers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import garophel.tempwmods.ModDataWrapper;
import garophel.tempwmods.ModHandler;
import garophel.tempwmods.Modifier;

public class ModPowerPick extends ModHandler {
	
	@Override
	public void modify(ItemStack target, ItemStack modifier, int modifierCount, Modifier mod) {
		NBTTagCompound nbt = Modifier.addModtag(target);
		Modifier.writeModData_uses(nbt, mod, modifierCount == 8 ? 2 : 1, target.getMaxDamage() - target.getItemDamage() + 1);
	}
	
	@Override
	public int handleBlock(HarvestDropsEvent e, ModDataWrapper data) {
		ItemStack pick = e.harvester.getCurrentEquippedItem();
		int uses = Math.min(data.getUses(), pick.getMaxDamage() - pick.getItemDamage());
		int broken = 0;
		
		int r = data.getAmplifier() + 1;
		for(int bx = -r; bx <= r; bx++) {
			for(int by = -r; by <= r; by++) {
				for(int bz = -r; bz <= r; bz++) {
					if(!(bx == 0 && by == 0 && bz == 0) && (bx * bx + by * by + bz * bz) <= (r * r) - 2f) {
//					if(!(bx == 0 /*&& by == 0*/ && bz == 0) && (bx * bx + /*by * by +*/ bz * bz) <= (r * r) - 2f) {
//						tryRemoveBlock(e.world, e.harvester, e.x + bx, e.y /*+ by*/, e.z + bz, false);
						if(tryRemoveBlock(e.world, e.harvester, e.x + bx, e.y + by, e.z + bz, false)) {
							broken++;
							uses--;
							
							if(uses <= 0) {
								break;
							}
						}
					}
				}
			}
		}
		
		pick.damageItem(broken, e.harvester);
		
		return broken;
	}

	@Override
	public boolean itemMatches(ItemStack modifier) {
		return modifier.getItem() == Items.brick;
	}

	@Override
	public boolean canModify(ItemStack target, int modifierCount) {
		return (modifierCount == 3 || modifierCount == 8) && target.getItem() instanceof ItemPickaxe;
	}

}
