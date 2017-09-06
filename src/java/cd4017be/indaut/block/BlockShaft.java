package cd4017be.indaut.block;

import cd4017be.lib.block.AdvancedBlock;
import cd4017be.lib.util.Orientation;
import cd4017be.lib.util.Utils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockShaft extends AdvancedBlock {

	public static IProperty<Axis> XYZorient = PropertyEnum.create("dir", Axis.class);
	public static IProperty<Boolean> negConnect = PropertyBool.create("con_"), posConnect = PropertyBool.create("con");

	public BlockShaft(String id, Material m, SoundType sound, Class<? extends TileEntity> tile) {
		super(id, m, sound, 3, tile);
		setDefaultState(getBlockState().getBaseState().withProperty(XYZorient, Axis.Z));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, XYZorient, negConnect, posConnect);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(XYZorient, Axis.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(XYZorient).ordinal();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		Axis o = state.getValue(XYZorient);
		IBlockState neighb = world.getBlockState(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, o)));
		state = state.withProperty(negConnect, neighb.getBlock() instanceof BlockShaft && neighb.getValue(XYZorient) == o);
		neighb = world.getBlockState(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, o)));
		return state.withProperty(posConnect, neighb.getBlock() instanceof BlockShaft && neighb.getValue(XYZorient) == o);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return getActualState(state, world, pos);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		if (!placer.isSneaking()) facing = Utils.getLookDirPlacement(placer).getOpposite();
		return getDefaultState().withProperty(XYZorient, facing.getAxis());
	}

	@Override
	public AdvancedBlock setBlockBounds(AxisAlignedBB box) {
		boundingBox = new AxisAlignedBB[] {Orientation.W.rotate(box), Orientation.Bn.rotate(box), box};
		return this;
	}

}
