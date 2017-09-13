package cd4017be.indaut.tileentity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.SlotItemHandler;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.DataContainer.IGuiData;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.Gui.TileContainer.ISlotClickHandler;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.Inventory;
import cd4017be.lib.util.Obj2;
import cd4017be.lib.util.Utils;

public class ItemBuffer extends AutomatedTile implements IGuiData, ISlotClickHandler {

	public int mode, amA, amB;

	public ItemBuffer() {
		inventory = new Inventory(21, 4, null).group(0, 0, 18, Utils.IN).group(1, 18, 19, Utils.OUT).group(2, 19, 20, Utils.OUT).group(3, 20, 21, Utils.OUT);
	}

	@Override
	public void update() 
	{
		super.update();
		if (world.isRemote) return;
		boolean bigStack = (mode & 0x100) != 0;
		int ovflSlots = mode & 0xff;
		int min = amA + amB;
		if (min > 0 && inventory.items[18] == null && inventory.items[19] == null) {
			Obj2<ItemStack, Integer> out = this.getItem(min, bigStack);
			if (out.objA != null) {
				int n = out.objA.getCount() / min;
				inventory.items[18] = amA == 0 ? null : out.objA.splitStack(n * amA);
				inventory.items[19] = amB == 0 ? null : out.objA.splitStack(n * amB);
				if (out.objA.getCount() > 0) inventory.items[out.objB] = out.objA;
			}
		}
		if (ovflSlots > 0 && inventory.items[20] == null) {
			for (int i = 0; i < 18 && ovflSlots > 0; i++)
				if (inventory.items[i] != null) ovflSlots--;
			if (ovflSlots <= 0) inventory.items[20] = this.getItem(1, bigStack).objA;
		}
	}
	
	private Obj2<ItemStack, Integer> getItem(int minAm, boolean biggest)
	{
		ItemStack item = null;
		int s = -1;
		for (int i = 0; i < 18; i++) {
			if (inventory.items[i] != null && inventory.items[i].getMaxStackSize() >= minAm) {
				if (item == null) {
					item = inventory.items[i];
					inventory.items[i] = null;
					s = i;
				} else if (item.getCount() >= item.getMaxStackSize()) return new Obj2<ItemStack, Integer>(item, s);
				else if (Utils.itemsEqual(item, inventory.items[i])) {
					int n = Math.min(item.getMaxStackSize() - item.getCount(), inventory.items[i].getCount());
					inventory.extractItem(i, n, false);
					item.grow(n);
				} else if (biggest && inventory.items[i].getCount() > item.getCount()) {
						inventory.items[s] = item;
						item = inventory.items[i];
						inventory.items[i] = null;
						s = i;
				}
			}
		}
		if (item != null && item.getCount() >= minAm) return new Obj2<ItemStack, Integer>(item, s);
		if (item != null) inventory.items[s] = item;
		return new Obj2<ItemStack, Integer>();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		mode = nbt.getInteger("mode");
		amA = nbt.getInteger("n1");
		amB = nbt.getInteger("n2");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setInteger("mode", mode);
		nbt.setInteger("n1", amA);
		nbt.setInteger("n2", amB);
		return super.writeToNBT(nbt);
	}

	@Override
	protected void customPlayerCommand(byte cmd, PacketBuffer dis, EntityPlayerMP player) {
		if (cmd == 0) mode = dis.readInt();
		else if (cmd == 1) amA = dis.readInt();
		else if (cmd == 2) amB = dis.readInt();
	}

	@Override
	public void initContainer(DataContainer cont) {
		TileContainer container = (TileContainer)cont;
		container.clickHandler = this;
		for (int j = 0; j < 2; j++)
			for (int i = 0; i < 9; i++)
				container.addItemSlot(new SlotItemHandler(inventory, i + 9 * j, 8 + 18 * i, 16 + 18 * j));
		container.addItemSlot(new SlotItemHandler(inventory, 18, 107, 52));
		container.addItemSlot(new SlotItemHandler(inventory, 19, 152, 52));
		container.addItemSlot(new SlotItemHandler(inventory, 20, 62, 52));

		container.addPlayerInventory(8, 86);
	}

	@Override
	public boolean transferStack(ItemStack item, int s, TileContainer container) {
		if (s < container.invPlayerS) container.mergeItemStack(item, container.invPlayerS, container.invPlayerE, false);
		else container.mergeItemStack(item, 0, 18, false);
		return true;
	}

	@Override
	public int[] getSyncVariables() {
		return new int[]{mode, amA, amB};
	}

	@Override
	public void setSyncVariable(int i, int v) {
		switch(i) {
		case 0: mode = v; break;
		case 1: amA = v; break;
		case 2: amB = v; break;
		}
	}

}
