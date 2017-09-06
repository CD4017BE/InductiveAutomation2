package cd4017be.indaut.tileentity;

import cd4017be.lib.block.AdvancedBlock.INeighborAwareTile;
import cd4017be.lib.block.BaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;

public class MultiblockPart extends BaseTileEntity implements INeighborAwareTile {

	public CircularMultiblock link;
	public double r, m, v;
	public IBlockState storedBlock = Blocks.AIR.getDefaultState();
	public boolean isLinked;

	public void link(CircularMultiblock target) {
		if (link == target) return;
		link = target;
		isLinked = true;
		r = Math.sqrt(pos.distanceSq(target.getPos()));
		link.mergeMomentum(m * r * r, v / r);
		markUpdate();
	}

	public void unlink() {
		if (link == null) return;
		link.remove(this);
		link = null;
		isLinked = false;
		if (!tileEntityInvalid) markUpdate();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		unlink();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		unlink();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("lk", isLinked);
		return new SPacketUpdateTileEntity(pos, -1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		isLinked = pkt.getNbtCompound().getBoolean("lk");
		markUpdate();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		m = nbt.getDouble("m");
		v = nbt.getDouble("v");
		storedBlock = Block.getStateById(nbt.getInteger("block"));
		isLinked = nbt.getBoolean("lk");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (link != null) v = link.structure.v * r;
		nbt.setDouble("m", m);
		nbt.setDouble("v", v);
		nbt.setInteger("block", Block.getStateId(storedBlock));
		nbt.setBoolean("lk", link != null);
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
