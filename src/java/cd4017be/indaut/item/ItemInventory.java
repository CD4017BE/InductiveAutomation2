package cd4017be.indaut.item;

import cd4017be.api.automation.InventoryItemHandler.IItemStorage;
import cd4017be.indaut.Objects;
import cd4017be.indaut.render.gui.GuiPortableInventory;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.BlockGuiHandler.ClientItemPacketReceiver;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.ItemGuiData;
import cd4017be.lib.Gui.SlotItemType;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.templates.InventoryItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemInventory extends ItemFilteredSubInventory implements IItemStorage, IGuiItem, ClientItemPacketReceiver {

	public ItemInventory(String id) {
		super(id);
	}

	@Override
	public int getSizeInventory(ItemStack item) {
		return 24;
	}

	@Override
	public Container getContainer(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new TileContainer(new GuiData(), player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new GuiPortableInventory(new TileContainer(new GuiData(), player));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		BlockGuiHandler.openItemGui(player, hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	class GuiData extends ItemGuiData {

		public GuiData() {
			super(ItemInventory.this);
		}

		@Override
		public void initContainer(DataContainer container) {
			TileContainer cont = (TileContainer)container;
			this.inv = new InventoryItem(cont.player);
			cont.addItemSlot(new SlotItemType(inv, 24, 8, 16, new ItemStack(Objects.itemUpgrade)));
			cont.addItemSlot(new SlotItemType(inv, 25, 8, 52, new ItemStack(Objects.itemUpgrade)));
			for (int j = 0; j < 3; j++)
				for (int i = 0; i < 8; i++)
					cont.addItemSlot(new SlotItemHandler(inv, j * 8 + i, 26 + i * 18, 16 + j * 18));
			cont.addPlayerInventory(8, 86, false, true);
		}

	}

}
