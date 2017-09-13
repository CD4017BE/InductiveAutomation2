package cd4017be.indaut.render;

import cd4017be.indaut.tileentity.LiquidPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class FluidPipeRenderer extends TileEntitySpecialRenderer<LiquidPipe> {

	@Override
	public void renderTileEntityAt(LiquidPipe te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.tank != null) TileEntityTankRenderer.renderFluid(te.tank, te, x, y + 0.3755, z, 0.249, 0.249);
	}

}
