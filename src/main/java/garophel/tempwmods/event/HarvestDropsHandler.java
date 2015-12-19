package garophel.tempwmods.event;

import garophel.tempwmods.Modifier;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class HarvestDropsHandler {
	
	private boolean blockRecurse = false;
	
	@SubscribeEvent
	public void onHarvestDrops(BlockEvent.HarvestDropsEvent e) {
		if(!blockRecurse && e.harvester != null && !e.world.isRemote) {
			blockRecurse = true;
//			System.out.println("fire");
			
			ItemStack item = e.harvester.getHeldItem();
			if(item != null && item.stackTagCompound != null && item.stackTagCompound.hasKey(Modifier.MODIFIER_TAG)) {
				NBTTagCompound modtag = item.stackTagCompound.getCompoundTag(Modifier.MODIFIER_TAG);
				modtag = Modifier.handleModifier(e, e.harvester, modtag);
				
				if(modtag == null) {
					item.stackTagCompound.removeTag(Modifier.MODIFIER_TAG);
				}
			}
			
//			Block up = e.world.getBlock(e.x, e.y + 1, e.z);
//			int meta = e.world.getBlockMetadata(e.x, e.y + 1, e.z);
//			
//			if(up != null && up != Blocks.air) {
//				boolean canHarvest = false;
//				
//				if(!e.harvester.capabilities.isCreativeMode) {
//					canHarvest = up.canHarvestBlock(e.harvester, meta);
//				}
//				
//				if(removeBlock(e.world, e.harvester, e.x, e.y + 1, e.z, up, meta, canHarvest)) {
//					up.harvestBlock(e.world, e.harvester, e.x, e.y + 1, e.z, meta);
//				}
//			}
			
			//System.out.println("removed: " + tryRemoveBlock(e.world, e.harvester, e.x, e.y - 1, e.z, false));
			
			blockRecurse = false;
		}
	}
	
}
