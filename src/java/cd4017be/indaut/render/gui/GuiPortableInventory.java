package cd4017be.indaut.render.gui;

import cd4017be.indaut.item.ItemFilteredSubInventory;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.Gui.GuiMachine;
import cd4017be.lib.Gui.TileContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class GuiPortableInventory extends GuiMachine {

	private final InventoryPlayer inv;

	public GuiPortableInventory(TileContainer container) {
		super(container);
		this.inv = container.player.inventory;
		this.MAIN_TEX = new ResourceLocation("automation", "textures/gui/portableInventory.png");
	}

	@Override
	public void initGui() {
		this.xSize = 176;
		this.ySize = 168;
		super.initGui();
		guiComps.add(new Button(0, 7, 34, 18, 8, 0).texture(176, 0).setTooltip("inputFilter"));
		guiComps.add(new Button(1, 7, 42, 18, 8, 0).texture(176, 16).setTooltip("outputFilter"));
	}

	@Override
	protected Object getDisplVar(int id) {
		ItemStack item = inv.mainInventory.get(inv.currentItem);
		if (id < 2) return ItemFilteredSubInventory.isFilterOn(item, id == 0) ? 1 : 0;
		else return null;
	}

	@Override
	protected void setDisplVar(int id, Object obj, boolean send) {
		PacketBuffer dos = BlockGuiHandler.getPacketTargetData(((TileContainer)inventorySlots).data.pos());
		dos.writeByte(id);
		if (send) BlockGuiHandler.sendPacketToServer(dos);
	}

}
