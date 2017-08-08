package cd4017be.indaut.tileentity;

import java.util.List;

import cd4017be.indaut.Objects;
import cd4017be.indaut.registry.FlyWheelMaterials;
import cd4017be.indaut.registry.FlyWheelMaterials.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FlyWheel extends CircularMultiblock {

	@Override
	protected Block multiblockPart() {
		return Objects.flyWheelPart;
	}

	@Override
	protected void create(MultiblockPart part) {
		Entry e;
		List<ItemStack> list = part.storedBlock.getBlock().getDrops(world, pos, part.storedBlock, 0);
		if (!list.isEmpty() && (e = FlyWheelMaterials.getFor(list.get(0))) != null) {
			part.m = e.mass;
		}
	}

	@Override
	protected boolean valid(IBlockState state, BlockPos pos) {
		if (state.getMaterial().isSolid()) {
			List<ItemStack> list = state.getBlock().getDrops(world, pos, state, 0);
			if (list.size() == 1) {
				return FlyWheelMaterials.getFor(list.get(0)) != null;
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModel() {
		return String.format("flywheel(%.1f", Math.round(R * 2F) / 2F - 0.5F);
	}

}
