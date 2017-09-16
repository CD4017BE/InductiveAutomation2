package cd4017be.indaut.render;

import cd4017be.indaut.tileentity.FluidPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class FluidPipeRenderer extends TileEntitySpecialRenderer<FluidPipe> {

	@Override
	public void renderTileEntityAt(FluidPipe te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.tank != null) TileEntityTankRenderer.renderFluid(te.tank, te, x, y + 0.3755, z, 0.249, 0.249);
	}

}
