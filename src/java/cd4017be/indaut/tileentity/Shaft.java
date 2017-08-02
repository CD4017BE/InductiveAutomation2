package cd4017be.indaut.tileentity;

import java.util.ArrayList;
import cd4017be.indaut.block.BlockShaft;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.multiblock.ShaftStructure;
import cd4017be.lib.block.AdvancedBlock.INeighborAwareTile;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.block.BaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class Shaft extends BaseTileEntity implements INeighborAwareTile, ITickable {

	public ShaftStructure structure;

	public Shaft() {
	}

	public Shaft(IBlockState state) {
		super(state);
		structure = new ShaftStructure(this, state.getValue(BlockShaft.XYZorient));
	}

	@Override
	public void neighborBlockChange(Block b, BlockPos src) {
		
	}

	@Override
	public void neighborTileChange(BlockPos src) {
		// TODO Auto-generated method stub
	}

	public double getMass() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void getInteractions(ArrayList<IKineticInteraction> list) {
		// TODO Auto-generated method stub
		
	}

	public void sendVelocityUpdate(int size) {
		PacketBuffer data = BlockGuiHandler.getPacketTargetData(pos);
		data.writeFloat((float)structure.v);
		BlockGuiHandler.sendPacketToAllNear(this, (double)size * 1.5 + 16.0, data);
	}

	@Override
	public void update() {
		structure.update(this, world.isRemote);
	}

}
