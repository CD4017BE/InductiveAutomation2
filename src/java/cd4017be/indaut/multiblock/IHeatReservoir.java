package cd4017be.indaut.multiblock;

import cd4017be.lib.util.ICachableInstance;

public interface IHeatReservoir extends ICachableInstance{
	/**
	 * @return [K] temperature
	 */
	public float T();
	/**
	 * @return [J/K] heat capacity
	 */
	public float C();
	/**
	 * @return [K/W] heat transfer resistance
	 */
	public float R();
	/**
	 * add the given amount of heat to this reservoir (negative value to remove).
	 * @param dQ [J] heat energy
	 */
	public void addHeat(float dQ);

	/**
	 * implementation that prevents any heat transfer.
	 */
	public static final IHeatReservoir NULL = new IHeatReservoir() {
		@Override
		public boolean invalid() {return false;}
		@Override
		public float T() {return 0;}
		@Override
		public float C() {return 0;}
		@Override
		public float R() {return Float.POSITIVE_INFINITY;}
		@Override
		public void addHeat(float dQ) {}
	};

	/**
	 * implements an infinite heat reservoir with constant temperature
	 * @author CD4017BE
	 */
	public static class ConstantHeat implements IHeatReservoir {
		private final float T, R;
		public ConstantHeat(float T, float R) {this.T = T; this.R = R;}
		@Override
		public boolean invalid() {return false;}
		@Override
		public float T() {return T;}
		@Override
		public float C() {return Float.POSITIVE_INFINITY;}
		@Override
		public float R() {return R;}
		@Override
		public void addHeat(float dQ) {}
	}

}
