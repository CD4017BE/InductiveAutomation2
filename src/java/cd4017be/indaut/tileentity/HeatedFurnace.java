package cd4017be.indaut.tileentity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;
import cd4017be.indaut.shaft.HeatReservoir;
import cd4017be.indaut.shaft.IHeatReservoir;
import cd4017be.indaut.shaft.IHeatReservoir.IHeatStorage;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.DataContainer.IGuiData;
import cd4017be.lib.Gui.SlotItemType;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.Inventory;
import cd4017be.lib.util.Utils;

public class HeatedFurnace extends AutomatedTile implements IHeatStorage, IGuiData {

	public static final float NeededTemp = 1200F, TRwork = 20, Energy = 250000F;
	public HeatReservoir heat;
	private boolean done = true;
	public int num;
	public float progress, temp;

	public HeatedFurnace() {
		inventory = new Inventory(3, 2, null).group(0, 0, 1, Utils.IN).group(1, 1, 2, Utils.OUT);
		heat = new HeatReservoir(10000F);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) return;
		heat.update(this);
		float e = (heat.T - NeededTemp) * heat.C;
		if (inventory.items[2] == null && heat.T > NeededTemp && inventory.items[0] != null && FurnaceRecipes.instance().getSmeltingResult(inventory.items[0]) != null) {
			inventory.items[2] = inventory.extractItem(0, (int)Math.ceil(e / Energy), false);
			done = false;
		}
		if (inventory.items[2] != null) {
			if (!done){
				int sz = inventory.items[2].getCount();
				float req = (float)sz * Energy;
				if (progress < req && e > 0) {
					float dQ = e / TRwork;
					progress += dQ;
					heat.addHeat(-dQ);
				}
				if (progress >= req) {
					ItemStack item = FurnaceRecipes.instance().getSmeltingResult(inventory.items[2]);
					if (item != null) {
						progress -= req;
						inventory.items[2] = item.copy();
						inventory.items[2].getCount() *= sz;
					}
					done = true;
				}
			}
			if (done) {
				int n = inventory.items[2].getMaxStackSize();
				if (inventory.items[1] == null) 
					inventory.items[1] = inventory.extractItem(2, n, false);
				else if (inventory.items[1].isItemEqual(inventory.items[2]) && (n -= inventory.items[1].getCount()) > 0) 
					inventory.items[1].grow(inventory.extractItem(2, n, false).getCount());
			}
		} else if (progress > 0) {
			heat.addHeat(progress);
			progress = 0;
		}
		temp = heat.T;
		num = inventory.items[2] == null ? 0 : inventory.items[2].getCount();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		heat.load(nbt, "heat");
		progress = nbt.getFloat("progress");
		done = nbt.getBoolean("done");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		heat.save(nbt, "heat");
		nbt.setFloat("progress", progress);
		nbt.setBoolean("done", done);
		return super.writeToNBT(nbt);
	}
	
	@Override
	public void onNeighborBlockChange(Block b, BlockPos src) {
		heat.check = true;
	}

	@Override
	public void onNeighborTileChange(BlockPos pos) {
		heat.check = true;
	}

	@Override
	public void initContainer(DataContainer container) {
		TileContainer cont = (TileContainer)container;
		cont.addItemSlot(new SlotItemHandler(inventory, 0, 17, 16));
		cont.addItemSlot(new SlotItemType(inventory, 1, 71, 16));
		cont.addPlayerInventory(8, 68);
	}

	@Override
	public boolean transferStack(ItemStack item, int s, TileContainer container) {
		if (s < container.invPlayerS) container.mergeItemStack(item, container.invPlayerS, container.invPlayerE, false);
		else container.mergeItemStack(item, 0, 1, false);
		return true;
	}

	@Override
	public IHeatReservoir getHeat(byte side) {
		return heat;
	}

	@Override
	public float getHeatRes(byte side) {
		return (side^1) == this.getOrientation() ? HeatReservoir.def_con : HeatReservoir.def_discon;
	}

	public float getProgress() {
		return progress / Energy / (float)num;
	}

	@Override
	public int[] getSyncVariables() {
		return new int[]{num, Float.floatToIntBits(progress), Float.floatToIntBits(temp)};
	}

	@Override
	public void setSyncVariable(int i, int v) {
		switch(i) {
		case 0: num = v; break;
		case 1: progress = Float.intBitsToFloat(v); break;
		case 2: temp = Float.intBitsToFloat(v); break;
		}
	}

}
