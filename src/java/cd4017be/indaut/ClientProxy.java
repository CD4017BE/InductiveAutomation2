package cd4017be.indaut;

import cd4017be.indaut.render.ShaftRenderer;
import cd4017be.indaut.render.gui.GuiSolidHeater;
import cd4017be.indaut.render.gui.GuiStirlingEngine;
import cd4017be.indaut.tileentity.FlyWheel;
import cd4017be.indaut.tileentity.Shaft;
import cd4017be.indaut.tileentity.ShaftHandle;
import cd4017be.indaut.tileentity.SolidHeater;
import cd4017be.indaut.tileentity.StirlingEngine;
import cd4017be.lib.BlockItemRegistry;
import cd4017be.lib.render.InWorldUIRenderer;
import cd4017be.lib.render.SpecialModelLoader;
import cd4017be.lib.render.model.BlockMimicModel;
import cd4017be.lib.render.model.MultipartModel;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import static cd4017be.indaut.Objects.*;

public class ClientProxy extends CommonProxy {

	public ShaftRenderer shaftRenderer;

	@Override
	public void registerRenderers() {
		super.registerRenderers();
		BlockItemRegistry.registerRender(heatInsulation);
		BlockItemRegistry.registerRender(heatSink);
		BlockItemRegistry.registerRender(flyWheel);
		BlockItemRegistry.registerRender(heatPipe);
		BlockItemRegistry.registerRender(shaft);
		BlockItemRegistry.registerRender(shaftHandle);
		BlockItemRegistry.registerRender(stirlingEngine);
		BlockItemRegistry.registerRender(solidHeater);
		
		SpecialModelLoader.registerBlockModel(flyWheelPart, BlockMimicModel.instance);
		SpecialModelLoader.registerBlockModel(heatPipe, new MultipartModel(heatPipe));
		
		shaftRenderer = new ShaftRenderer();
		SpecialModelLoader.instance.tesrs.add(shaftRenderer);
		
		ClientRegistry.bindTileEntitySpecialRenderer(Shaft.class, shaftRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(ShaftHandle.class, shaftRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(FlyWheel.class, shaftRenderer);
		InWorldUIRenderer.register(StirlingEngine.class, new GuiStirlingEngine());
		InWorldUIRenderer.register(SolidHeater.class, new GuiSolidHeater());
		
	}

}
