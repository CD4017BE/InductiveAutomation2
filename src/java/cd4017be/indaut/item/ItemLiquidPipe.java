package cd4017be.indaut.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import cd4017be.lib.BlockItemRegistry;
import cd4017be.lib.DefaultItemBlock;

	
/**
 *
 * @author CD4017BE
 */
public class ItemLiquidPipe extends DefaultItemBlock {

	public ItemLiquidPipe(Block id) {
		super(id);
		this.setHasSubtypes(true);
		BlockItemRegistry.registerItemStack(new ItemStack(this, 1, 0), "liquidPipeT");
		BlockItemRegistry.registerItemStack(new ItemStack(this, 1, 2), "liquidPipeE");
		BlockItemRegistry.registerItemStack(new ItemStack(this, 1, 1), "liquidPipeI");
	}

	@Override
	public int getMetadata(int dmg) {
		return dmg;
	}

}
