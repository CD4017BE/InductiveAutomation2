package cd4017be.indaut.block;

import cd4017be.indaut.tileentity.MultiblockPart;
import cd4017be.lib.block.AdvancedBlock;
import cd4017be.lib.property.PropertyBlockMimic;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockMultiblockPart extends AdvancedBlock {

	public static final PropertyBool linked = PropertyBool.create("link");

	public BlockMultiblockPart(String id, Material m, Class<? extends TileEntity> tile) {
		super(id, m, 2, tile);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{linked}, new IUnlistedProperty[] {PropertyBlockMimic.instance});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof MultiblockPart) {
			MultiblockPart part = (MultiblockPart)te;
			if (part.link != null)
				return ((IExtendedBlockState)state).withProperty(PropertyBlockMimic.instance, part.storedBlock);
		}
		return state;
	}

}
