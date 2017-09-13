package cd4017be.indaut.multiblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import cd4017be.indaut.Objects;
import cd4017be.indaut.block.BlockLiquidPipe;
import cd4017be.indaut.multiblock.WarpPipePhysics.IFluidDest;
import cd4017be.lib.ModTileEntity;

public class FluidDestination extends FluidComp implements IFluidDest {

	public FluidDestination(BasicWarpPipe pipe, byte side) {
		super(pipe, side);
	}

	@Override
	public boolean onClicked(EntityPlayer player, EnumHand hand, ItemStack item, long uid) {
		if (player == null) ((ModTileEntity)pipe.tile).dropStack(new ItemStack(Objects.liquidPipe, 1, BlockLiquidPipe.ID_Injection));
		if (super.onClicked(player, hand, item, uid) || player == null) return true;
		if (item == null && player.isSneaking()) {
			((ModTileEntity)pipe.tile).dropStack(new ItemStack(Objects.liquidPipe, 1, BlockLiquidPipe.ID_Injection));
			pipe.con[side] = 0;
			pipe.network.remConnector(pipe, side);
			pipe.updateCon = true;
			pipe.hasFilters &= ~(1 << side);
			((ModTileEntity)pipe.tile).markUpdate();
			return true;
		}
		return false;
	}

}
