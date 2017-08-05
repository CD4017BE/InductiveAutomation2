package cd4017be.indaut;

import cd4017be.indaut.render.ShaftRenderer;
import cd4017be.indaut.tileentity.Shaft;
import cd4017be.lib.render.SpecialModelLoader;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		super.registerRenderers();
		SpecialModelLoader.registerTESR(Shaft.class, new ShaftRenderer());
	}

}
