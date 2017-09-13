package cd4017be.indaut.multiblock;

import cd4017be.api.IAbstractTile;
import cd4017be.indaut.Objects;
import cd4017be.indaut.registry.Substances;
import cd4017be.indaut.registry.Substances.Environment;
import cd4017be.indaut.tileentity.HeatPipe;
import cd4017be.lib.block.BaseTileEntity;
import cd4017be.lib.templates.MultiblockComp;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class HeatPipeComp extends MultiblockComp<HeatPipeComp, HeatPipeStructure> implements IHeatReservoir {

	public final float C, R, Rp;
	public float refTemp;
	public float heatCond;
	public byte isCon = 0;

	public HeatPipeComp(IAbstractTile tile, float C, float R, float Rp) {
		super(tile);
		this.C = C;
		this.R = R;
		this.Rp = Rp;
	}

	public void setUID(long uid) {
		super.setUID(uid);
		if (network == null) {
			HeatPipe pipe = (HeatPipe)tile;
			HeatPipeStructure network = new HeatPipeStructure(this);
			Environment env = Substances.getEnvFor(pipe.getWorld());
			network.T = env.getTemp(pipe.getWorld(), pipe.getPos());
		}
	}

	public boolean isCon(EnumFacing side) {
		return (isCon & 1 << side.ordinal()) != 0;
	}

	public void setCon(EnumFacing side, boolean c) {
		if (c) isCon |= 1 << side.ordinal();
		else isCon &= ~(1 << side.ordinal());
	}

	@Override
	public void setConnect(byte side, boolean c) {
		super.setConnect(side, c);
		setCon(EnumFacing.VALUES[side], c);
	}

	public static HeatPipeComp readFromNBT(BaseTileEntity tile, NBTTagCompound nbt, String k, float C, float R, float Rp) {
		HeatPipeComp pipe = new HeatPipeComp(tile, C, R, Rp);
		HeatPipeStructure struc = new HeatPipeStructure(pipe);
		struc.T = nbt.getFloat(k + "T");
		short c = nbt.getShort(k + "con");
		pipe.con = (byte)c;
		pipe.isCon = (byte)(c >> 8);
		return pipe;
	}

	public void writeToNBT(NBTTagCompound nbt, String k) {
		if (network != null) {
			nbt.setFloat(k + "T", network.T);
		}
		nbt.setShort(k + "con", (short)(con & 0x3f | isCon << 8 & 0x3f00));
	}

	@Override
	public Capability<HeatPipeComp> getCap() {
		return Objects.HEAT_PIPE_CAP;
	}

	@Override
	public float T() {
		return network.T;
	}

	@Override
	public float C() {
		return network.C;
	}

	@Override
	public float R() {
		return R;
	}

	@Override
	public void addHeat(float dQ) {
		network.T += dQ / network.C;
	}

}
