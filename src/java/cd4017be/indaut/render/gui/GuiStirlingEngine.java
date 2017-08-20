package cd4017be.indaut.render.gui;

import cd4017be.indaut.tileentity.StirlingEngine;
import cd4017be.lib.TooltipInfo;
import cd4017be.lib.render.InWorldUIRenderer.Gui;
import net.minecraft.util.math.RayTraceResult;

public class GuiStirlingEngine implements Gui<StirlingEngine> {

	@Override
	public void renderComponents(StirlingEngine tile, RayTraceResult aim, float t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTooltip(StirlingEngine tile, int comp) {
		return TooltipInfo.format("stirlingEngine.mult", tile.mult);
	}

}
