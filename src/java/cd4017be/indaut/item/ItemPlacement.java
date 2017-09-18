package cd4017be.indaut.item;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import cd4017be.indaut.render.gui.GuiPlacement;
import cd4017be.indaut.render.gui.InventoryPlacement;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.BlockGuiHandler.ClientItemPacketReceiver;
import cd4017be.lib.DefaultItem;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.ItemGuiData;
import cd4017be.lib.Gui.SlotHolo;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.templates.InventoryItem;
import cd4017be.lib.templates.InventoryItem.IItemInventory;
import cd4017be.lib.util.ItemFluidUtil;

public class ItemPlacement extends DefaultItem implements IGuiItem, ClientItemPacketReceiver, IItemInventory {

	public ItemPlacement(String id) {
		super(id);
	}

	private static final String[] dirs = {"B", "T", "N", "S", "W", "E"};

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean b) {
		InventoryPlacement inv = new InventoryPlacement(item);
		for (int i = 0; i < inv.inventory.length; i++) 
			list.add(dirs[inv.getDir(i).ordinal()] + ": " + inv.inventory[i].getDisplayName());
		super.addInformation(item, player, list, b);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		BlockGuiHandler.openItemGui(player, hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing s, float X, float Y, float Z) {
		ItemStack item = player.getHeldItem(hand);
		if (world.isRemote) return EnumActionResult.SUCCESS;
		InventoryPlacement inv = new InventoryPlacement(item);
		int p;
		ItemStack stack;
		pos = pos.offset(s);
		for (int i = 0; i < inv.inventory.length && inv.inventory[i] != null; i++) {
			for (p = 0; true; p++) {
				if (p == player.inventory.currentItem) continue;
				if (p >= player.inventory.mainInventory.size()) return EnumActionResult.SUCCESS;
				stack = player.inventory.mainInventory.get(p);
				if (stack != null && stack.getItem() == inv.inventory[i].getItem() && (!inv.useDamage(i) || stack.getItemDamage() == inv.inventory[i].getItemDamage())) {
					stack = player.inventory.decrStackSize(p, 1);
					break;
				}
			}
			stack = inv.doPlacement(world, player, pos, i, stack);
			if (stack != null && !player.inventory.addItemStackToInventory(stack)) {
				EntityItem e = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
				world.spawnEntity(e);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public Container getContainer(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new TileContainer(new GuiData(), player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new GuiPlacement(new TileContainer(new GuiData(), player));
	}

	@Override
	public void onPacketFromClient(PacketBuffer dis, EntityPlayer player, ItemStack item, int slot) throws IOException {
		NBTTagCompound nbt;
		if (item.hasTagCompound()) nbt = item.getTagCompound();
		else item.setTagCompound(nbt = new NBTTagCompound());
		int[] arr = nbt.getIntArray("mode");
		byte idx = dis.readByte();
		if (idx >= arr.length) return;
		arr[idx] = dis.readInt();
		nbt.setIntArray("mode", arr);
	}

	@Override
	public ItemStack[] loadInventory(ItemStack inv, EntityPlayer player) {
		ItemStack[] items = new ItemStack[8];
		if (inv.hasTagCompound()) ItemFluidUtil.loadItems(inv.getTagCompound().getTagList(ItemFluidUtil.Tag_ItemList, 10), items);
		return items;
	}

	@Override
	public void saveInventory(ItemStack inv, EntityPlayer player, ItemStack[] items) {
		NBTTagCompound nbt;
		if (inv.hasTagCompound()) nbt = inv.getTagCompound();
		else inv.setTagCompound(nbt = new NBTTagCompound());
		NBTTagList list = ItemFluidUtil.saveItems(items);
		nbt.setTag(ItemFluidUtil.Tag_ItemList, list);
		int[] mode = nbt.getIntArray("mode");
		int l = mode.length;
		if (l != list.tagCount()) {
			mode = Arrays.copyOf(mode, list.tagCount());
			if (mode.length > l) Arrays.fill(mode, l, mode.length, 0x0088);
			nbt.setIntArray("mode", mode);
		}
	}

	class GuiData extends ItemGuiData {

		public GuiData() {
			super(ItemPlacement.this);
		}

		@Override
		public void initContainer(DataContainer container) {
			TileContainer cont = (TileContainer)container;
			this.inv = new InventoryItem(cont.player);
			for (int i = 0; i < 8; i++)
				cont.addItemSlot(new SlotHolo(inv, i, 8 + i * 18, 16, false, false));
			cont.addPlayerInventory(8, 68, false, true);
		}

	}

}
