package cd4017be.indaut;

import cd4017be.indaut.block.BlockShaft;
import cd4017be.lib.DefaultItemBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.AxisAlignedBB;

public class Objects {

	public static CreativeTabs tabIndAut;

	public static BlockShaft shaft;

	static void createBlocks() {
		new DefaultItemBlock((shaft = new BlockShaft("shaft", Material.IRON, 0, null)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
	}

	static void createItems() {
	}

}
