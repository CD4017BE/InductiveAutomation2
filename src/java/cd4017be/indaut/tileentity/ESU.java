package cd4017be.indaut.tileentity;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.ArrayList;

import cd4017be.api.automation.PipeEnergy;
import cd4017be.api.energy.EnergyAPI;
import cd4017be.api.energy.EnergyAPI.IEnergyAccess;
import cd4017be.indaut.Config;
import cd4017be.indaut.Objects;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.DataContainer.IGuiData;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.Gui.TileContainer.ISlotClickHandler;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.Inventory;
import cd4017be.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author CD4017BE
 */
public class ESU extends AutomatedTile implements IGuiData, IEnergyAccess, ISlotClickHandler {

	public int type = 0;
	public int Uref;
	public float Estor, power;

	public ESU() {
		energy = new PipeEnergy(0, 0);
		inventory = new Inventory(2, 2, null).group(0, 0, 1, Utils.ACC).group(1, 1, 2, Utils.ACC);
	}

	protected void setWireType() {
		Block id = this.getBlockType();
		type = id == Objects.SCSU ? 0 : id == Objects.OCSU ? 1 : 2;
	}

	@Override
	public void update() {
		if (energy.Umax == 0) {
			this.setWireType();
			byte con = energy.sideCfg;
			energy = new PipeEnergy(Config.Umax[type], Config.Rcond[type]);
			energy.sideCfg = con;
		}
		super.update();
		if (world.isRemote) return;
		Estor -= EnergyAPI.get(inventory.items[0], 0).addEnergy(getStorage() - getCapacity());
		Estor -= EnergyAPI.get(inventory.items[1], 0).addEnergy(getStorage());
		float d = energy.getEnergy(Uref, 1);
		float d1 = addEnergy(d);
		if (d1 == d) energy.Ucap = Uref;
		else if (d1 != 0) energy.addEnergy(-d1);
		power = (float)d1;
	}

	public int getMaxStorage() {
		return Config.Ecap[type];
	}

	@Override
	public float addEnergy(float e) {
		if (energy.Umax == 0) return 0;
		if (Estor + e < 0) {
			e = -Estor;
			Estor = 0;
		} else if (Estor + e > getCapacity()) {
			e = getCapacity() - Estor;
			Estor = getCapacity();
		} else {
			Estor += e;
		}
		return e;
	}

	@Override
	public void onPlaced(EntityLivingBase entity, ItemStack item) {
		item = item.copy();
		item.setCount(1);
		Estor = (float)EnergyAPI.get(item, -1).getStorage();
	}

	@Override
	public ArrayList<ItemStack> dropItem(IBlockState state, int fortune) {
		ItemStack item = new ItemStack(this.getBlockType());
		Estor -= EnergyAPI.get(item, -1).addEnergy(Estor);
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		list.add(item);
		return list;
	}

	@Override
	protected void customPlayerCommand(byte cmd, PacketBuffer dis, EntityPlayerMP player) throws IOException {
		if (cmd == 0) {
			Uref = dis.readInt();
			if (Uref < 0) Uref = 0;
			if (Uref > Config.Umax[type]) Uref = Config.Umax[type];
		}
	}

	public float storage() {
		return Estor / getCapacity();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setByte("type", (byte)type);
		nbt.setFloat("storage", Estor);
		nbt.setInteger("voltage", Uref);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		type = nbt.getByte("type");
		energy = new PipeEnergy(Config.Umax[type], Config.Rcond[type]);
		super.readFromNBT(nbt);
		Estor = nbt.getFloat("storage");
		Uref = nbt.getInteger("voltage");
	}

	public float getDiff() {
		return power / this.getCapacity() * 400F;
	}

	@Override
	public void initContainer(DataContainer cont) {
		TileContainer container = (TileContainer)cont;
		container.clickHandler = this;
		container.addItemSlot(new SlotItemHandler(inventory, 0, 98, 16));
		container.addItemSlot(new SlotItemHandler(inventory, 1, 134, 16));
		
		container.addPlayerInventory(8, 86);
	}

	@Override
	public boolean transferStack(ItemStack item, int s, TileContainer container) {
		if (s < container.invPlayerS) container.mergeItemStack(item, container.invPlayerS, container.invPlayerE, false);
		else container.mergeItemStack(item, 0, 2, true);
		return true;
	}

	@Override
	public float getStorage() {
		return Estor;
	}

	@Override
	public float getCapacity() {
		return this.getMaxStorage() * 1000F;
	}

	@Override
	public int[] getSyncVariables() {
		return new int[]{Uref, Float.floatToIntBits(Estor), Float.floatToIntBits(power)};
	}

	@Override
	public void setSyncVariable(int i, int v) {
		switch(i) {
		case 0: Uref = v; break;
		case 1: Estor = Float.intBitsToFloat(v); break;
		case 2: power = Float.intBitsToFloat(v); break;
		}
	}

}
