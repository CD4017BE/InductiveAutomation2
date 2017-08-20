package cd4017be.indaut.render.gui;

import cd4017be.indaut.tileentity.SolidHeater;
import cd4017be.lib.TooltipInfo;
import cd4017be.lib.render.InWorldUIRenderer.Gui;
import net.minecraft.util.math.RayTraceResult;

public class GuiSolidHeater implements Gui<SolidHeater> {

	@Override
	public void renderComponents(SolidHeater tile, RayTraceResult aim, float t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTooltip(SolidHeater tile, int comp) {
		switch(comp) {
		case 0: return TooltipInfo.format("solidHeater.fuel", tile.fuel, SolidHeater.maxFuel);
		case 1: return TooltipInfo.format("solidHeater.burn", tile.burnRate);
		default: return null;
		}
	}

}
