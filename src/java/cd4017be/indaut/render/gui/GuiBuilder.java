package cd4017be.indaut.render.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import cd4017be.indaut.tileentity.Builder;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.util.TooltipUtil;
import cd4017be.lib.Gui.GuiMachine;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.templates.AutomatedTile;

/**
 *
 * @author CD4017BE
 */
public class GuiBuilder extends GuiMachine {
	private String[] steps = {"Inactive", "Frame Y", "Frame Z", "Frame X", "Bottom", "Top", "North", "South", "West", "East", "Filling"};
	private Builder tile;

	public GuiBuilder(Builder tileEntity, EntityPlayer player) {
		super(new TileContainer(tileEntity, player));
		this.tile = tileEntity;
		this.MAIN_TEX = new ResourceLocation("automation", "textures/gui/builder.png");
	}

	@Override
	public void initGui() {
		this.xSize = 176;
		this.ySize = 240;
		super.initGui();
		String[] s = TooltipUtil.getConfigFormat("builder.state").split("\\n");
		if (s.length >= steps.length) steps = s;
		for (int i = 0; i < 8; i++)
			guiComps.add(new NumberSel(2 + i, 25 + i * 18, 69, 18, 18, "%d", 0, 256, 8).setTooltip("builder.size"));
		guiComps.add(new Text(10, 115, 20, 54, 8, "\\%s").center());
		guiComps.add(new Button(11, 7, 69, 18, 18, 0).texture(176, 0).setTooltip("builder.dir"));
		guiComps.add(new Button(12, 115, 15, 54, 18, -1).setTooltip("builder.run"));
		guiComps.add(new GuiComp(13, 7, 15, 54, 18).setTooltip("builder.frame"));
		guiComps.add(new GuiComp(13, 61, 15, 54, 18).setTooltip("builder.wall"));
		guiComps.add(new GuiComp(13, 7, 51, 18, 18).setTooltip("builder.stack"));
	}

	@Override
	protected Object getDisplVar(int id) {
		if (id < 10) return tile.thick[id - 2];
		else if (id == 10) return steps[tile.thick[8]];
		else if (id == 11) return tile.thick[9];
		else return null;
	}

	@Override
	protected void setDisplVar(int id, Object obj, boolean send) {
		PacketBuffer dos = tile.getPacketTargetData();
		if (id < 10) {
			dos.writeByte(AutomatedTile.CmdOffset + id - 2);
			dos.writeShort(tile.thick[id - 2] = (Integer)obj);
		} else if (id == 11) {
			dos.writeByte(AutomatedTile.CmdOffset + 9);
			dos.writeShort(tile.thick[9] = (tile.thick[9] + 1) % 3);
		} else if (id == 12) {
			dos.writeByte(AutomatedTile.CmdOffset + 8);
		}
		if (send) BlockGuiHandler.sendPacketToServer(dos);
	}

}
