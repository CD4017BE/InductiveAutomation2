package cd4017be.indaut.tileentity;

import cd4017be.indaut.Config;

public class TeslaTransmitterLV extends TeslaTransmitter
{

	@Override
	protected int getMaxVoltage() 
	{
		return Config.Umax[3];
	}
	
	public boolean checkAlive()
	{
		return !this.isInvalid() && world.isBlockLoaded(getPos()) && world.getTileEntity(getPos()) == this;
	}
	
}
