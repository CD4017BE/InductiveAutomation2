package cd4017be.electricity.components;

import cd4017be.electricity.INotify;
import cd4017be.electricity.IResistorMergable;
import static cd4017be.electricity.MathUtil.*;
import static cd4017be.electricity.IResistorMergable.*;

public class Diode extends BiPole implements INotify, IResistorMergable {

	public static final double PassPotential = 0.7;
	protected int swId, cst;
	protected boolean transmit;

	public Diode() {
		this.transmit = false;
	}

	@Override
	public void setEquations(double[][] mat, int states) {
		double[] row = mat[id];
		row[A.Id_U] = -1.0;
		row[B.Id_U] = 1.0;
		row[id] = (transmit ? PassResistance : BlockResistance) + Rc(A);
		row[states + cst] = -PassPotential;
	}

	@Override
	public void update(double[] states, double dt) {
		transmit = states[id] > 0;
		circuit.setSwitch(swId, transmit);
	}

	@Override
	public int init() {
		this.swId = circuit.nextSwitch();
		this.cst = circuit.getConstant();
		circuit.preNotifier.add(this);
		circuit.setSwitch(swId, transmit);
		return super.init();
	}

	@Override
	public void updateResistor() {}

}
