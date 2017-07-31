package cd4017be.electricity.components;

import cd4017be.electricity.IResistorMergable;
import cd4017be.electricity.Parameter;
import static cd4017be.electricity.IResistorMergable.*;

/**
 * Implements an electric component that provides a fixed voltage between negative pin A and positive pin B
 * @author CD4017BE
 */
public class VoltageSource extends BiPole implements Parameter, IResistorMergable {

	protected int cid;
	protected double U;

	/**
	 * Creates a new voltage source
	 * @param U [V] the voltage it provides
	 */
	public VoltageSource(double U) {
		this.U = U;
	}

	@Override
	public void setEquations(double[][] mat, int states) {
		double[] row = mat[id];
		row[cid + states] = 1.0;
		row[B.Id_U] = 1.0;
		row[A.Id_U] = -1.0;
		row[id] = Rc(A);
	}

	@Override
	public void setValue(double[] vec) {
		vec[cid] = U;
	}

	@Override
	public int init() {
		this.cid = circuit.addPar(this);
		return super.init();
	}

	@Override
	public String toString() {
		return super.toString() + String.format(" U=%.3gV", U);
	}

	@Override
	public void updateResistor() {}

}
