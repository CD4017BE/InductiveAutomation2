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
}
