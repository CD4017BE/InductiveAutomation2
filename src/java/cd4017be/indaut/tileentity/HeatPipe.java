package cd4017be.indaut.tileentity;

import cd4017be.indaut.Objects;
import cd4017be.indaut.multiblock.HeatPipeComp;
import cd4017be.indaut.multiblock.HeatPipeStructure;
import cd4017be.lib.block.AdvancedBlock.IInteractiveTile;
import cd4017be.lib.block.MultipartBlock.IModularTile;
import cd4017be.lib.templates.MultiblockTile;
import cd4017be.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class HeatPipe extends MultiblockTile<HeatPipeComp, HeatPipeStructure> implements IInteractiveTile, IModularTile {

	public static float C = 1000, R = 0.05F, Rp = 100;
	
	public HeatPipe() {
		comp = new HeatPipeComp(this, C, R, Rp);
	}

	@Override
	public void update() {
		if (!world.isRemote) super.update();
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack item, EnumFacing dir, float X, float Y, float Z) {
		if (world.isRemote) return true;
		dir = Utils.hitSide(X, Y, Z);
		byte s = (byte)dir.ordinal();
		if (player.isSneaking() && item.isEmpty()) {
			boolean t = !comp.canConnect(s);
			comp.setConnect(s, t);
			comp.updateCon = true;
			this.markUpdate();
			TileEntity te = Utils.neighborTile(this, dir);
			if (te instanceof HeatPipe) {
				HeatPipeComp pipe = ((HeatPipe)te).comp;
				pipe.setConnect((byte)(s^1), t);
				((HeatPipe)te).markUpdate();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onClicked(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing s) {
		if (cap == Objects.HEAT_CAP || cap == Objects.HEAT_PIPE_CAP) return comp.canConnect((byte)s.ordinal());
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing s) {
		if (cap == Objects.HEAT_CAP || cap == Objects.HEAT_PIPE_CAP && comp.canConnect((byte)s.ordinal())) return (T) comp;
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		comp.writeToNBT(nbt, "heat");
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		comp = HeatPipeComp.readFromNBT(this, nbt, "heat", C, R, Rp);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		short c = pkt.getNbtCompound().getShort("con");
		comp.con = (byte)c;
		comp.isCon = (byte)(c >> 8);
		this.markUpdate();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setShort("con", (short)(comp.con & 0x3f | comp.isCon << 8 & 0x3f00));
		return new SPacketUpdateTileEntity(getPos(), -1, nbt);
	}

	@Override
	public void neighborBlockChange(Block b, BlockPos src) {
		comp.updateCon = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getModuleState(int m) {
		return (T)Byte.valueOf(comp.canConnect((byte)m) && comp.getNeighbor((byte)m) != null ? (byte)0 : comp.isCon(EnumFacing.VALUES[m]) ? (byte)1 : (byte)-1);
	}

	@Override
	public boolean isModulePresent(int m) {
		return comp.isCon(EnumFacing.VALUES[m]) || comp.canConnect((byte)m) && comp.getNeighbor((byte)m) != null;
	}

}
