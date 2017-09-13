package cd4017be.indaut.jeiPlugin;

import cd4017be.indaut.tileentity.AntimatterBomb;
import net.minecraft.init.Blocks;

/**
 *
 * @author CD4017BE
 */
public class BedrockRecipeHandler
{
	public static final float neededAm;
	static {
		float i = (2 * AntimatterBomb.Density + 1) / AntimatterBomb.Density;
		neededAm = Blocks.BEDROCK.getExplosionResistance(null) / AntimatterBomb.PowerFactor / AntimatterBomb.explMult * i * i * 9.375F;
	}
	
}
