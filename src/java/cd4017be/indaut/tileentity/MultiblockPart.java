package cd4017be.indaut.tileentity;

import cd4017be.lib.block.AdvancedBlock.INeighborAwareTile;
import cd4017be.lib.block.BaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class MultiblockPart extends BaseTileEntity implements INeighborAwareTile {

	public CircularMultiblock link;
	public double r, m, v;
	public IBlockState storedBlock;

	public void link(CircularMultiblock target) {
		if (link == target) return;
		link = target;
		r = Math.sqrt(pos.distanceSq(target.getPos()));
		link.mergeMomentum(m * r * r, v / r);
	}

	public void unlink() {
		if (link == null) return;
		link.remove(this);
		link = null;
	}

	@Override
	public void invalidate() {
		unlink();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		unlink();
		super.onChunkUnload();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		m = nbt.getDouble("m");
		v = nbt.getDouble("v");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (link != null) v = link.structure.v * r;
		nbt.setDouble("m", m);
		nbt.setDouble("v", v);
		return super.writeToNBT(nbt);
	}

	@Override
	public void neighborBlockChange(Block b, BlockPos src) {
		if (link != null) link.neighborBlockChange(b, src);
	}

	@Override
	public void neighborTileChange(BlockPos src) {
		neighborBlockChange(null, src);
	}

}
