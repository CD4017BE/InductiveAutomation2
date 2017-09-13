package cd4017be.indaut.tileentity;

import cd4017be.indaut.Config;

public class QuantumTank extends Tank {

	@Override
	protected int capacity()
	{
		return Config.tankCap[5];
	}
	
}
