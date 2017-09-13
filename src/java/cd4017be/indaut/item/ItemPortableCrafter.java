package cd4017be.indaut.item;

import java.io.IOException;

import cd4017be.indaut.render.gui.GuiPortableCrafting;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.DefaultItem;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.ItemGuiData;
import cd4017be.lib.Gui.SlotHolo;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.templates.InventoryItem;
import cd4017be.lib.templates.InventoryItem.IItemInventory;
import cd4017be.lib.util.ItemFluidUtil;
import cd4017be.lib.util.Utils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemPortableCrafter extends DefaultItem implements IGuiItem, IItemInventory {

	public ItemPortableCrafter(String id) {
		super(id);
		this.setMaxStackSize(1);
	}

	@Override
	public Container getContainer(World world, EntityPlayer player, int x, int y, int z) {
		return new TileContainer(new GuiData(), player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(World world, EntityPlayer player, int x, int y, int z) {
		return new GuiPortableCrafting(new TileContainer(new GuiData(), player));
	}

	@Override
	public void onPlayerCommand(ItemStack item, EntityPlayer player, PacketBuffer dis) {
		if (item == null || item.getItem() != this) return;
		if (item.getTagCompound() == null) item.setTagCompound(new NBTTagCompound());
		byte cmd = dis.readByte();
		if (cmd == 0) {
			item.getTagCompound().setBoolean("active", !item.getTagCompound().getBoolean("active"));
		} else if (cmd == 1) {
			item.getTagCompound().setBoolean("auto", !item.getTagCompound().getBoolean("auto"));
			item.getTagCompound().setBoolean("active", false);
		} else if (cmd == 2) {
			byte n = item.getTagCompound().getByte("amount");
			n += dis.readByte();
			if (n < 0) n = 0;
			if (n > 64) n = 64;
			item.getTagCompound().setByte("amount", n);
		} else if (cmd == 3) {
			byte n = dis.readByte();
			ItemStack[] recipe = loadRecipe(item.getTagCompound().getTagList("grid", 10), player.world);
			if (recipe[0] != null) this.craft(player.inventory, recipe, n > 0 ? n : Integer.MAX_VALUE, false);
			item.getTagCompound().setBoolean("active", false);
		} else if (cmd == 4) try {// set crafting grid (used for JEI recipe transfer)
			NBTTagCompound nbt = dis.readCompoundTag();
			item.getTagCompound().setTag("grid", nbt.getTagList("grid", 10));
			byte n = nbt.getByte("amount");
			if (n < 0) n = 0;
			if (n > 64) n = 64;
			item.getTagCompound().setByte("amount", n);
			item.getTagCompound().setBoolean("active", false);
			ItemGuiData.updateInventory(player, player.inventory.currentItem);
		} catch (IOException e) {}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		BlockGuiHandler.openItemGui(player, world, 0, -1, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int s, boolean b) {
		if (entity instanceof EntityPlayer) {
			if (item.getTagCompound() == null) item.setTagCompound(new NBTTagCompound());
			long t = world.getTotalWorldTime();
			if ((t - (long)item.getTagCompound().getByte("t") & 0xff) >= 20) {
				EntityPlayer player = (EntityPlayer)entity;
				InventoryPlayer inv = player.inventory;
				int n = item.getTagCompound().getByte("amount");
				boolean auto = item.getTagCompound().getBoolean("auto");
				boolean on = item.getTagCompound().getBoolean("active");
				if (on) {
					ItemStack[] recipe = loadRecipe(item.getTagCompound().getTagList("grid", 10), world);
					if (recipe[0] != null) {
						n -= this.craft(inv, recipe, n, auto);
						if (!auto) item.getTagCompound().setByte("amount", (byte)n);
						if (n <= 0) item.getTagCompound().setBoolean("active", false);
					} else item.getTagCompound().setBoolean("active", false);
				}
				item.getTagCompound().setByte("t", (byte)t);
			}
		}
	}

	public static ItemStack[] loadRecipe(NBTTagList data, World world) {
		InventoryCrafting icr = new InventoryCrafting(ItemFluidUtil.CraftContDummy, 3, 3);
		ItemStack[] recipe = new ItemStack[data.tagCount()];
		int n = 0;
		NBTTagCompound tag;
		for (int i = 0; i < data.tagCount(); i++) {
			tag = data.getCompoundTagAt(i);
			int s = tag.getByte("slot") & 0xff;
			ItemStack stack = new ItemStack(tag);
			if (s < 9) icr.setInventorySlotContents(s, stack);
			for (int j = 0; j < n && stack != null; j++)
				if (Utils.itemsEqual(recipe[j], stack)) {
					recipe[j].grow(1);
					stack = null;
				}
			if (stack != null) recipe[n++] = stack.splitStack(1);
		}
		ItemStack[] ret = new ItemStack[n + 1];
		ret[0] = CraftingManager.getInstance().findMatchingRecipe(icr, world);
		System.arraycopy(recipe, 0, ret, 1, n);
		return ret;
	}

	private int craft(InventoryPlayer inv, ItemStack[] recipe, int n, boolean auto) {
		if (auto) {
			n *= recipe[0].getCount();
			for (int i = 0; i < inv.mainInventory.size() && n > 0; i++) {
				if (Utils.itemsEqual(inv.mainInventory.get(i), recipe[0])) n -= inv.mainInventory.get(i).getCount();
			}
			n /= recipe[0].getCount();
		}
		if (n <= 0) return 0;
		int m, p;
		int[] slots = new int[inv.mainInventory.size()];
		for (int i = 0; i < slots.length; i++) slots[i] = i;
		for (int i = 1; i < recipe.length; i++) {
			m = 0; p = 0;
			while((p = Utils.findStack(recipe[i], inv, slots, p)) >= 0) {
				m += inv.getStackInSlot(slots[p++]).getCount();
			}
			m /= recipe[i].getCount();
			if (m < n) n = m;
		}
		if (n <= 0) return 0;
		for (int i = 1; i < recipe.length; i++) {
			m = recipe[i].getCount() * n; p = 0;
			while((p = Utils.findStack(recipe[i], inv, slots, p)) >= 0 && m > 0) {
				m -= inv.decrStackSize(slots[p++], m).getCount();
			}
			if (recipe[i].getItem().hasContainerItem(recipe[i])) {
				ItemStack iout = recipe[i].getItem().getContainerItem(recipe[i]);
				if (iout.isItemStackDamageable() && iout.getItemDamage() > iout.getMaxDamage()) iout = null;
				if (iout != null) {
					iout.setCount(recipe[i].getCount() * n);
					if (!inv.addItemStackToInventory(iout)) {
						inv.player.dropItem(iout, true);
					}
				}
			}
		}
		ItemStack item = recipe[0];
		item.setCount(item.getCount() * n);
		if (!inv.addItemStackToInventory(recipe[0])) {
			inv.player.dropItem(recipe[0], true);
		}
		if (auto) return 0;
		else return n;
	}

	@Override
	public ItemStack[] loadInventory(ItemStack inv, EntityPlayer player) {
		ItemStack[] items = new ItemStack[10];
		if (inv.hasTagCompound()) {
			ItemFluidUtil.loadInventory(inv.getTagCompound().getTagList("grid", 10), items);
			items[9] = CraftingManager.getInstance().findMatchingRecipe(ItemFluidUtil.craftingInventory(items, 3), player.world);
		}
		return items;
	}

	@Override
	public void saveInventory(ItemStack inv, EntityPlayer player, ItemStack[] items) {
		if (!inv.hasTagCompound()) inv.setTagCompound(new NBTTagCompound());
		items[9] = null;
		inv.getTagCompound().setTag("grid", ItemFluidUtil.saveInventory(items));
		items[9] = CraftingManager.getInstance().findMatchingRecipe(ItemFluidUtil.craftingInventory(items, 3), player.world);
	}

	class GuiData extends ItemGuiData {

		public GuiData() {super(ItemPortableCrafter.this);}

		@Override
		public void initContainer(DataContainer container) {
			TileContainer cont = (TileContainer)container;
			this.inv = new InventoryItem(cont.player);
			for (int j = 0; j < 3; j++)
				for (int i = 0; i < 3; i++) 
					cont.addItemSlot(new SlotHolo(inv, i + 3 * j, 17 + i * 18, 16 + j * 18, false, false));
			cont.addItemSlot(new SlotHolo(inv, 9, 89, 34, true, true));
			cont.addPlayerInventory(8, 86, false, true);
		}

	}

}
