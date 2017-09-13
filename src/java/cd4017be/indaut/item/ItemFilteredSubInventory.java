package cd4017be.indaut.item;

import java.util.List;

import cd4017be.api.automation.InventoryItemHandler;
import cd4017be.api.automation.InventoryItemHandler.IItemStorage;
import cd4017be.indaut.Objects;
import cd4017be.lib.DefaultItem;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.Gui.ItemGuiData;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.util.ItemFluidUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class ItemFilteredSubInventory extends DefaultItem implements IItemStorage, IGuiItem {

	public ItemFilteredSubInventory(String id) {
		super(id);
		this.setMaxStackSize(1);
	}

	@Override
	public String getInventoryTag() {
		return "items";
	}

	@Override
	public ItemStack[] loadInventory(ItemStack inv, EntityPlayer player) {
		int l = getSizeInventory(inv);
		ItemStack[] items = new ItemStack[l + 2];
		if (inv.hasTagCompound()) {
			NBTTagCompound nbt = inv.getTagCompound();
			ItemFluidUtil.loadInventory(nbt.getTagList(getInventoryTag(), 10), items);
			if (nbt.hasKey("fin")) {
				items[l] = new ItemStack(Objects.itemUpgrade);
				items[l].setTagCompound(nbt.getCompoundTag("fin"));
			} else items[l] = null;
			if (nbt.hasKey("fout")) {
				items[l + 1] = new ItemStack(Objects.itemUpgrade);
				items[l + 1].setTagCompound(nbt.getCompoundTag("fout"));
			} else items[l + 1] = null;
		}
		return items;
	}

	@Override
	public void saveInventory(ItemStack inv, EntityPlayer player, ItemStack[] items) {
		int l = getSizeInventory(inv);
		NBTTagCompound nbt;
		if (inv.hasTagCompound()) nbt = inv.getTagCompound();
		else inv.setTagCompound(nbt = new NBTTagCompound());
		ItemStack fin = items[l], fout = items[l + 1]; items[l] = null; items[l + 1] = null;
		nbt.setTag(getInventoryTag(), ItemFluidUtil.saveInventory(items));
		if (fin != null && fin.getItem() == Objects.itemUpgrade) {
			if (!fin.hasTagCompound()) fin.setTagCompound(new NBTTagCompound());
			nbt.setTag("fin", fin.getTagCompound());
			items[l] = fin;
		} else nbt.removeTag("fin");
		if (fout != null && fout.getItem() == Objects.itemUpgrade) {
			if (!fout.hasTagCompound()) fout.setTagCompound(new NBTTagCompound());
			nbt.setTag("fout", fout.getTagCompound());
			items[l + 1] = fout;
		} else nbt.removeTag("fout");
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean b) {
		InventoryItemHandler.addInformation(item, list);
		super.addInformation(item, player, list, b);
	}

	protected int tickTime() {
		return 20;
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int s, boolean b) {
		if (world.isRemote) return;
		if (entity instanceof EntityPlayer && item.hasTagCompound()) {
			long t = world.getTotalWorldTime();
			if ((t - (long)item.getTagCompound().getByte("t") & 0xff) >= tickTime()) {
				EntityPlayer player = (EntityPlayer)entity;
				InventoryPlayer inv = player.inventory;
				PipeUpgradeItem in = item.getTagCompound().hasKey("fin") ? PipeUpgradeItem.load(item.getTagCompound().getCompoundTag("fin")) : null;
				PipeUpgradeItem out = item.getTagCompound().hasKey("fout") ? PipeUpgradeItem.load(item.getTagCompound().getCompoundTag("fout")) : null;
				this.updateItem(item, player, inv, s, in, out);
				item.getTagCompound().setByte("t", (byte)t);
			}
		}
	}

	protected void updateItem(ItemStack item, EntityPlayer player, InventoryPlayer inv, int s, PipeUpgradeItem in, PipeUpgradeItem out) {
		boolean update = false;
		if (in != null) update |= this.autoInput(item, inv, in);
		if (out != null) update |= this.autoOutput(item, inv, out);
		if (update) ItemGuiData.updateInventory(player, s);
	}

	protected boolean autoInput(ItemStack item, InventoryPlayer inv, PipeUpgradeItem in) {
		IItemHandler acc = new InvWrapper(inv);
		ItemStack stack, stack1;
		in.mode |= 64;
		if ((in.mode & 128) == 0) return false;
		boolean update = false;
		for (int i = 0; i < inv.mainInventory.size(); i++)
			if ((stack = inv.mainInventory.get(i)) != null && itemAllowed(stack.getItem()) && (stack1 = in.getExtract(stack, acc)) != null) {
				stack.shrink(stack1.getCount());
				if ((stack1 = InventoryItemHandler.insertItemStack(item, stack1)) != null) stack.grow(stack1.getCount());
				else if (stack.getCount() <= 0) inv.mainInventory.set(i, null);
				update = true;
			}
		return update;
	}

	private boolean itemAllowed(Item item) {
		return !(item instanceof ItemFilteredSubInventory || item instanceof ItemRemoteInv);
	}

	protected boolean autoOutput(ItemStack item, InventoryPlayer inv, PipeUpgradeItem out) {
		IItemHandler acc = new InvWrapper(inv);
		int n;
		out.mode |= 64;
		if ((out.mode & 128) == 0) return false;
		for (ItemStack search : InventoryItemHandler.getItemList(item)) {
			n = out.insertAmount(search, acc);
			if (n <= 0) continue;
			if (n < search.getCount()) search.setCount(n);
			n -= TileContainer.putInPlayerInv(search, inv);
			search = search.copy();
			search.setCount(n);
			InventoryItemHandler.extractItemStack(item, search);
		}
		return true;
	}

	@Override
	public void onPlayerCommand(ItemStack item, EntityPlayer player, PacketBuffer dis) {
		byte cmd = dis.readByte();
		if (cmd >= 0 && cmd < 2) {
			String name = cmd == 0 ? "fin" : "fout";
			if (item.hasTagCompound() && item.getTagCompound().hasKey(name, 10)) {
				NBTTagCompound tag = item.getTagCompound().getCompoundTag(name);
				byte m = tag.getByte("mode");
				m |= 64;
				m ^= 128;
				tag.setByte("mode", m);
				ItemGuiData.updateInventory(player, player.inventory.currentItem);
			}
		} else this.customPlayerCommand(item, player, cmd, dis);
	}

	protected void customPlayerCommand(ItemStack item, EntityPlayer player, byte cmd, PacketBuffer dis) {}

	public static boolean isFilterOn(ItemStack item, boolean in) {
		if (item != null && item.hasTagCompound() && item.getTagCompound().hasKey(in ? "fin" : "fout", 10))
			return (item.getTagCompound().getCompoundTag(in ? "fin" : "fout").getByte("mode") & 192) == 192;
		return false;
	}

}
