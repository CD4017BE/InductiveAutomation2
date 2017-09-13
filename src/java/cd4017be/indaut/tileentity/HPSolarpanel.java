package cd4017be.indaut.tileentity;

import cd4017be.indaut.Config;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumSkyBlock;

/**
 *
 * @author CD4017BE
 */
public class HPSolarpanel extends Solarpanel {
	
	@Override
	protected float getRcond() {return Config.Rcond[1];}

	@Override
	protected int getUmax() {return Config.Ugenerator[4];}

	@Override
	public void update() 
	{
		if (this.world.isRemote) return;
		int sl = this.world.getLightFor(EnumSkyBlock.SKY, pos.up());
		int bl = 45;
		sl -= this.world.getSkylightSubtracted();
		if (sl < 0) sl = 0;
		if (world.provider.getDimensionType().equals(DimensionType.THE_END)) sl = 5;
		float power = (float)(sl * sl * sl + bl) / 3.375F; //Skylight = 15 -> power = 1
		energy.addEnergy(power * Config.Pgenerator[4]);
		energy.update(this);
	}
	
}
