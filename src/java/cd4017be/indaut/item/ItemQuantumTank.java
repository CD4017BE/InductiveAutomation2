package cd4017be.indaut.item;

import cd4017be.indaut.Config;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemQuantumTank extends ItemTank {

	public ItemQuantumTank(Block id) {
		super(id);
	}

	@Override
	public int getCapacity(ItemStack item) {
		return Config.tankCap[5];
	}

	@Override
	public EnumRarity getRarity(ItemStack item) {
		return EnumRarity.UNCOMMON;
	}

}
