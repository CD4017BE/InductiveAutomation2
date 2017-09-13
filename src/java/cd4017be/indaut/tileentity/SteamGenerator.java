package cd4017be.indaut.tileentity;

import cd4017be.indaut.Config;

/**
 *
 * @author CD4017BE
 */
public class SteamGenerator extends SteamEngine {
	@Override
	protected int getUmax() {return Config.Ugenerator[1];}
	@Override
	public float getPower() {return Config.Pgenerator[1];}
}
