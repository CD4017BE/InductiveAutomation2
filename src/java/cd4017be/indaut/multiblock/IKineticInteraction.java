package cd4017be.indaut.multiblock;

import cd4017be.lib.util.ICachableInstance;

public interface IKineticInteraction extends ICachableInstance {
	/**
	 * called upon connection with a shaft
	 * @param shaft reference to the shaft connected with
	 */
	public void setShaft(IShaft shaft);
	/**
	 * calculate an estimation for the torque this part applies on the shaft.
	 * This value doesn't need to be exact, but it shouldn't be greater than the actual force added during work() to prevent bad glitches.
	 * @param ds [m] the distance the shaft will move during this calculation tick
	 * @return [kg*m/s²] the force on the shaft (positive values accelerate, negative slow down)
	 */
	public double estimateTorque(double ds);
	/**
	 * update the mechanical physics of this connected part.<br>
	 * {@code estimateTorque()} is always called before this but usually with a slightly different value for {@code ds}.
	 * @param ds [m] the distance the shaft will move during this calculation tick
	 * @param v [20m/s] the speed that would result from force
	 * @return [J] the amount of kinetic energy added to the shaft
	 */
	public double work(double ds, double v);
}
