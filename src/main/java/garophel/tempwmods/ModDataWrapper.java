package garophel.tempwmods;

import net.minecraft.nbt.NBTTagCompound;

public class ModDataWrapper {
	
	public final NBTTagCompound nbt;
	public final boolean valid;
	
	public ModDataWrapper(NBTTagCompound nbt) {
		this.nbt = nbt;
		if(nbt != null && nbt.hasKey(Modifier.TAG_USES) && nbt.hasKey(Modifier.TAG_AMPLIFIER)) {
			valid = true;
		} else {
			valid = false;
		}
	}
	
	public boolean hasTag(String key) {
		return nbt.hasKey(key);
	}
	
	public int getUses() {
		return nbt.getInteger(Modifier.TAG_USES);
	}
	
	public int getAmplifier() {
		return nbt.getInteger(Modifier.TAG_AMPLIFIER);
	}
}
