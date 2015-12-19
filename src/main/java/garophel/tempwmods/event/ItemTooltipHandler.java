package garophel.tempwmods.event;

import garophel.tempwmods.Modifier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemTooltipHandler {
	
	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent e) {
		if(e.itemStack.stackTagCompound != null && e.itemStack.stackTagCompound.hasKey(Modifier.MODIFIER_TAG)) {
			NBTTagCompound modtag = e.itemStack.stackTagCompound.getCompoundTag(Modifier.MODIFIER_TAG);
			Modifier mod = Modifier.getFromId(modtag.getInteger(Modifier.TAG_ID));
			if(mod != null) {
				mod.handler.tooltip(mod, e, modtag);
			}
		}
	}
}
