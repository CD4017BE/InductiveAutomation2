package cd4017be.indaut.tileentity;

import java.util.ArrayList;
import cd4017be.indaut.block.BlockShaft;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.multiblock.ShaftStructure;
import cd4017be.lib.block.AdvancedBlock.INeighborAwareTile;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.BlockGuiHandler.ServerPacketReceiver;
import cd4017be.lib.block.BaseTileEntity;
import cd4017be.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Shaft extends BaseTileEntity implements INeighborAwareTile, ITickable, ServerPacketReceiver {

	public static double BASE_MASS = 1000;
	
	protected boolean checkNeighbors = true;
	public ShaftStructure structure;

	public Shaft() {}
	public Shaft(IBlockState state) {
		super(state);
		structure = new ShaftStructure(this, state.getValue(BlockShaft.XYZorient));
	}

	@Override
	public void update() {
		if (structure == null) new ShaftStructure(this, getBlockState().getValue(BlockShaft.XYZorient));
		if (checkNeighbors) {
			TileEntity te = Utils.neighborTile(this, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, structure.axis));
			if (te instanceof Shaft) structure.addShaft((Shaft)te);
			te = Utils.neighborTile(this, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, structure.axis));
			if (te instanceof Shaft) structure.addShaft((Shaft)te);
			checkNeighbors = false;
		}
		structure.update(this, world.isRemote);
	}

	public double getMass() {
		return BASE_MASS;
	}

	public void getInteractions(ArrayList<IKineticInteraction> list) {}

	@Override
	public void neighborBlockChange(Block b, BlockPos src) {}

	@Override
	public void neighborTileChange(BlockPos src) {
		checkNeighbors = true;
	}

	public void sendVelocityUpdate(int size) {
		PacketBuffer data = BlockGuiHandler.getPacketTargetData(pos);
		data.writeFloat((float)structure.v);
		BlockGuiHandler.sendPacketToAllNear(this, (double)size * 1.5 + 16.0, data);
	}

	@Override
	public void onPacketFromServer(PacketBuffer data) {
		structure.v = data.readFloat();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (structure != null) structure.removeShaft(this);
		structure = new ShaftStructure(this, nbt);
		checkNeighbors = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		structure.writeToNBT(nbt);
		return super.writeToNBT(nbt);
	}

	@SideOnly(Side.CLIENT)
	public String getModel() {
		return "shaft";
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (structure != null) structure.removeShaft(this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (structure != null) structure.removeShaft(this);
	}

}
