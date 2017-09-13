package cd4017be.indaut.render.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cd4017be.indaut.tileentity.FluidPacker;
import cd4017be.lib.Gui.GuiMachine;
import cd4017be.lib.Gui.TileContainer;

/**
 *
 * @author CD4017BE
 */
public class GuiFluidPacker extends GuiMachine {

	public GuiFluidPacker(FluidPacker tileEntity, EntityPlayer player) {
		super(new TileContainer(tileEntity, player));
		this.MAIN_TEX = new ResourceLocation("automation", "textures/gui/fluidPacker.png");
		this.xSize = 176;
		this.ySize = 168;
	}

}
