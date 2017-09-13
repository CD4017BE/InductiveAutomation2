package cd4017be.indaut.item;

import cd4017be.indaut.block.BlockOre.Ore;
import cd4017be.lib.BlockItemRegistry;
import cd4017be.lib.DefaultItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CD4017BE
 */
public class ItemOre extends DefaultItemBlock {

	public ItemOre(Block id) {
		super(id);
		this.setHasSubtypes(true);
		BlockItemRegistry.registerItemStack(new ItemStack(this, 1, Ore.Silver.ordinal()), "oreSilver");
		BlockItemRegistry.registerItemStack(new ItemStack(this, 1, Ore.Copper.ordinal()), "oreCopper");
		BlockItemRegistry.registerItemStack(new ItemStack(this, 1, Ore.Aluminium.ordinal()), "oreAluminium");
	}

	@Override
	public int getMetadata(int dmg) {
		return dmg;
	}

}
