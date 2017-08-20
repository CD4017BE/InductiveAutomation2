package cd4017be.indaut.multiblock;

import cd4017be.lib.block.BaseTileEntity;

public class SimpleHeatReservoir implements IHeatReservoir {

	private final float C, R;
	public float T;
	private BaseTileEntity tile;

	public SimpleHeatReservoir(BaseTileEntity tile, float C, float R) {
		this.C = C;
		this.R = R;
		this.tile = tile;
	}

	@Override
	public boolean invalid() {
		return tile.invalid();
	}

	@Override
	public float T() {
		return T;
	}

	@Override
	public float C() {
		return C;
	}

	@Override
	public float R() {
		return R;
	}

	@Override
	public void addHeat(float dQ) {
		T += dQ / C;
	}

}
