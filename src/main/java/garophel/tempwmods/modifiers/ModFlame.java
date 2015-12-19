package garophel.tempwmods.modifiers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import garophel.tempwmods.ModDataWrapper;
import garophel.tempwmods.ModHandler;
import garophel.tempwmods.Modifier;

public class ModFlame extends ModHandler {

	@Override
	public int handle(LivingAttackEvent e, EntityLivingBase source, EntityLivingBase target, float damage, ModDataWrapper data) {
		target.setFire(2 * data.getAmplifier());
		return 1;
	}

	@Override
	public boolean itemMatches(ItemStack modifier) {
		return modifier.getItem() == Items.magma_cream;
	}

	@Override
	public boolean canModify(ItemStack target, int modifierCount) {
		return modifierCount == 1 && (target.getItem() instanceof ItemSword || target.getItem() instanceof ItemAxe);
	}

	@Override
	public void modify(ItemStack target, ItemStack modifier, int modifierCount, Modifier mod) {
		NBTTagCompound nbt = Modifier.addModtag(target);
		Modifier.writeModData_uses(nbt, mod, 1, 16);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void tooltip(Modifier m, ItemTooltipEvent e, NBTTagCompound modtag) {
		Modifier.defaultTooltip(m, e, modtag, EnumChatFormatting.YELLOW);
	}

}
