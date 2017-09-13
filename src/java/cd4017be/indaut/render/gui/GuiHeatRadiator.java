package cd4017be.indaut.render.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cd4017be.indaut.tileentity.HeatRadiator;
import cd4017be.lib.Gui.GuiMachine;
import cd4017be.lib.Gui.TileContainer;

public class GuiHeatRadiator extends GuiMachine {

	public GuiHeatRadiator(HeatRadiator tileEntity, EntityPlayer player) {
		super(new TileContainer(tileEntity, player));
		this.MAIN_TEX = new ResourceLocation("automation", "textures/gui/radiator.png");
		this.xSize = 176;
		this.ySize = 168;
	}

}
