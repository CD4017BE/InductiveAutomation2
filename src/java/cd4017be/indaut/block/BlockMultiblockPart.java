package cd4017be.indaut.block;

import cd4017be.indaut.tileentity.MultiblockPart;
import cd4017be.lib.block.AdvancedBlock;
import cd4017be.lib.property.PropertyBlockMimic;
import net.minecraft.block.SoundType;
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

	public BlockMultiblockPart(String id, Material m, SoundType sound, Class<? extends TileEntity> tile) {
		super(id, m, sound, 2, tile);
		setDefaultState(getBlockState().getBaseState().withProperty(linked, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{linked}, new IUnlistedProperty[] {PropertyBlockMimic.instance});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof MultiblockPart)
			return state.withProperty(linked, ((MultiblockPart)te).isLinked);
		return state;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof MultiblockPart) {
			MultiblockPart part = (MultiblockPart)te;
			if (part.isLinked)
				return ((IExtendedBlockState)state).withProperty(PropertyBlockMimic.instance, part.storedBlock);
		}
		return state;
	}

}
