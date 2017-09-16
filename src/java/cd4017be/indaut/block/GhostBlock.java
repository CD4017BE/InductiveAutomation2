package cd4017be.indaut.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import cd4017be.lib.block.BaseBlock;

public class GhostBlock extends BaseBlock {

	public static Block ID;

	public GhostBlock(String id) {
		super(id, Material.ROCK);
		ID = this;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

}
