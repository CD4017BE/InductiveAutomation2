package cd4017be.indaut;

import cd4017be.indaut.render.ShaftRenderer;
import cd4017be.indaut.tileentity.FlyWheel;
import cd4017be.indaut.tileentity.Shaft;
import cd4017be.indaut.tileentity.ShaftHandle;
import cd4017be.lib.render.SpecialModelLoader;
import cd4017be.lib.render.model.BlockMimicModel;
import cd4017be.lib.render.model.MultipartModel;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

	public ShaftRenderer shaftRenderer;

	@Override
	public void registerRenderers() {
		super.registerRenderers();
		SpecialModelLoader.registerBlockModel(Objects.flyWheelPart, BlockMimicModel.instance);
		SpecialModelLoader.registerBlockModel(Objects.heatPipe, new MultipartModel(Objects.heatPipe));
		
		shaftRenderer = new ShaftRenderer();
		SpecialModelLoader.instance.tesrs.add(shaftRenderer);
		
		ClientRegistry.bindTileEntitySpecialRenderer(Shaft.class, shaftRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(ShaftHandle.class, shaftRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(FlyWheel.class, shaftRenderer);
	}

}
