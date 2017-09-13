package cd4017be.indaut.render.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cd4017be.indaut.tileentity.SteamCompressor;
import cd4017be.lib.Gui.GuiMachine;
import cd4017be.lib.Gui.TileContainer;

/**
 *
 * @author CD4017BE
 */
public class GuiSteamCompressor extends GuiMachine {

	private final SteamCompressor tile;

	public GuiSteamCompressor(SteamCompressor tileEntity, EntityPlayer player) {
		super(new TileContainer(tileEntity, player));
		this.tile = tileEntity;
		this.MAIN_TEX = new ResourceLocation("automation", "textures/gui/steamCompressor.png");
	}

	@Override
	public void initGui() {
		this.xSize = 176;
		this.ySize = 168;
		super.initGui();
		guiComps.add(new ProgressBar(3, 48, 16, 8, 52, 176, 0, (byte)1).setTooltip("x*200+0;steamComp.press"));
		guiComps.add(new ProgressBar(4, 108, 37, 32, 10, 184, 0, (byte)0));
	}

	@Override
	protected Object getDisplVar(int id) {
		switch(id) {
		case 3: return tile.getPressure();
		case 4: return tile.getProgress();
		default: return null;
		}
	}

}
