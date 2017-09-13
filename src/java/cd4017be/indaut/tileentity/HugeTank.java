package cd4017be.indaut.tileentity;

import cd4017be.indaut.Config;

/**
 *
 * @author CD4017BE
 */
public class HugeTank extends Tank {

	@Override
	protected int capacity() {
		return Config.tankCap[3];
	}

}
