package cd4017be.indaut.render.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import cd4017be.api.automation.PipeEnergy;
import cd4017be.indaut.Config;
import cd4017be.indaut.tileentity.DecompCooler;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.Gui.GuiMachine;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.templates.AutomatedTile;

/**
 *
 * @author CD4017BE
 */
public class GuiDecompCooler extends GuiMachine {

	private final DecompCooler tile;

	public GuiDecompCooler(DecompCooler tileEntity, EntityPlayer player) {
		super(new TileContainer(tileEntity, player));
		this.tile = tileEntity;
		this.MAIN_TEX = new ResourceLocation("automation", "textures/gui/decompCooler.png");
	}

	@Override
	public void initGui() {
		this.xSize = 176;
		this.ySize = 168;
		super.initGui();
		guiComps.add(new NumberSel(7, 8, 16, 16, 52, "%d", Config.Rmin, 1000, 5).setTooltip("resistor"));
		guiComps.add(new ProgressBar(8, 26, 16, 8, 52, 176, 0, (byte)1));
		guiComps.add(new Tooltip(9, 26, 16, 8, 52, "energyFlow"));
		guiComps.add(new ProgressBar(10, 61, 37, 18, 10, 184, 0, (byte)0));
		guiComps.add(new ProgressBar(11, 133, 37, 18, 10, 184, 10, (byte)0));
	}

	@Override
	protected Object getDisplVar(int id) {
		switch(id) {
		case 7: return tile.Rw;
		case 8: return tile.getPower();
		case 9: return PipeEnergy.getEnergyInfo(tile.Uc, 0, tile.Rw);
		case 10:
		case 11: return tile.getProgress();
		default: return null;
		}
	}

	@Override
	protected void setDisplVar(int id, Object obj, boolean send) {
		PacketBuffer dos = tile.getPacketTargetData();
		switch(id) {
		case 7: dos.writeByte(AutomatedTile.CmdOffset).writeInt(tile.Rw = (Integer)obj);
		}
		if (send) BlockGuiHandler.sendPacketToServer(dos);
	}
}
