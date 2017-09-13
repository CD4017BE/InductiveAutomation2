package cd4017be.indaut.tileentity;

import cd4017be.api.recipes.AutomationRecipes;
import cd4017be.api.recipes.AutomationRecipes.CmpRecipe;
import cd4017be.indaut.Config;
import cd4017be.indaut.Objects;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.DataContainer.IGuiData;
import cd4017be.lib.Gui.SlotItemType;
import cd4017be.lib.Gui.SlotTank;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.Gui.TileContainer.TankSlot;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.Inventory;
import cd4017be.lib.templates.Inventory.IAccessHandler;
import cd4017be.lib.templates.TankContainer;
import cd4017be.lib.util.ItemFluidUtil;
import cd4017be.lib.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author CD4017BE
 */
public class SteamCompressor extends AutomatedTile implements IGuiData, IAccessHandler {

	public static int Euse = 160;
	private boolean craftInvChange = true;
	public int Estor, process;

	public SteamCompressor() {
		inventory = new Inventory(7, 5, this).group(0, 0, 1, Utils.IN).group(1, 1, 2, Utils.IN).group(2, 2, 3, Utils.IN).group(3, 3, 4, Utils.IN).group(4, 4, 5, Utils.OUT);
		tanks = new TankContainer(1, 1).tank(0, Config.tankCap[1], Utils.IN, 6, -1, Objects.L_steam);
	}

	@Override
	public void update() {
		super.update();
		if(world.isRemote) return;
		//steam
		int dp = 16 - Estor / 40;
		if (tanks.getAmount(0) < 5 * dp) dp = tanks.getAmount(0) / 5;
		if (dp > 0) {
			tanks.drain(0, dp * 5, true);
			Estor += dp;
		}
		//Item process
		if (Estor >= Euse && inventory.items[5] == null && craftInvChange) {
			CmpRecipe recipe = AutomationRecipes.getRecipeFor(inventory.items, 0, 4);
			if (recipe != null) {
				int n;
				for (int i = 0; i < recipe.input.length; i++)
					if ((n = AutomationRecipes.getStacksize(recipe.input[i])) > 0) inventory.extractItem(i, n, false);
				inventory.items[5] = recipe.output.copy();
			} else craftInvChange = false;
		}
		if (inventory.items[5] != null) {
			if (process < Euse) {
				dp = 1 + Estor * 7 / 320;
				if (dp > Estor) dp = Estor;
				Estor -= dp;
				process += dp;
			}
			if (process >= Euse) {
				inventory.items[5] = ItemFluidUtil.putInSlots(inventory, inventory.items[5], 4);
				if (inventory.items[5] == null) process -= Euse;
			}
		}
	}

	public float getPressure() {
		return (float)Estor / 640F;
	}

	public float getProgress() {
		return (float)process / (float)Euse;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("progress", process);
		nbt.setInteger("pressure", Estor);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		process = nbt.getInteger("progress");
		Estor = nbt.getInteger("pressure");
		craftInvChange = true;
	}

	@Override
	public void initContainer(DataContainer cont) {
		TileContainer container = (TileContainer)cont;
		for (int j = 0; j < 2; j++)
			for (int i = 0; i < 2; i++)
				container.addItemSlot(new SlotItemHandler(inventory, i + 2 * j, 71 + 18 * i, 25 + 18 * j));
		container.addItemSlot(new SlotItemType(inventory, 4, 143, 34));
		container.addItemSlot(new SlotTank(inventory, 6, 17, 34));
		
		container.addPlayerInventory(8, 86);
		
		container.addTankSlot(new TankSlot(tanks, 0, 8, 16, (byte)0x23));
	}

	@Override
	public boolean transferStack(ItemStack item, int s, TileContainer container) {
		if (s < container.invPlayerS) container.mergeItemStack(item, container.invPlayerS, container.invPlayerE, false);
		else container.mergeItemStack(item, 0, 4, false);
		return true;
	}

	@Override
	public int[] getSyncVariables() {
		return new int[]{Estor, process, };
	}

	@Override
	public void setSyncVariable(int i, int v) {
		switch(i) {
		case 0: Estor = v; break;
		case 1: process = v; break;
		}
	}

	@Override
	public void setSlot(int g, int s, ItemStack item) {
		inventory.items[s] = item;
		if (s < 4) craftInvChange = true;
	}

}
