package cd4017be.indaut.multiblock;

import cd4017be.api.IAbstractTile;
import cd4017be.indaut.Objects;
import cd4017be.lib.block.BaseTileEntity;
import cd4017be.lib.templates.MultiblockComp;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

public class HeatPipeComp extends MultiblockComp<HeatPipeComp, HeatPipeStructure> implements IHeatReservoir {

	public final float C, R;
	public float refTemp;
	public float heatCond;

	public HeatPipeComp(IAbstractTile tile, float C, float R) {
		super(tile);
		this.C = C;
		this.R = R;
	}

	public void setUID(long uid) {
		super.setUID(uid);
		if (network == null) new HeatPipeStructure(this);
	}

	public static HeatPipeComp readFromNBT(BaseTileEntity tile, NBTTagCompound nbt, String k, float C, float R) {
		HeatPipeComp pipe = new HeatPipeComp(tile, C, R);
		HeatPipeStructure struc = new HeatPipeStructure(pipe);
		struc.T = nbt.getFloat(k + "T");
		return pipe;
	}

	public void writeToNBT(NBTTagCompound nbt, String k) {
		if (network != null) {
			nbt.setFloat(k + "T", network.T);
		}
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
