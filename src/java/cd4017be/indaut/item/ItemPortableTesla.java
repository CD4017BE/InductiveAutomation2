package cd4017be.indaut.item;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;

import cd4017be.api.automation.TeslaNetwork;
import cd4017be.api.energy.EnergyAPI;
import cd4017be.indaut.Config;
import cd4017be.indaut.render.gui.GuiPortableTesla;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.BlockGuiHandler.ClientItemPacketReceiver;
import cd4017be.lib.DefaultItem;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.ItemGuiData;
import cd4017be.lib.Gui.TileContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPortableTesla extends DefaultItem implements IGuiItem, ClientItemPacketReceiver {

	public ItemPortableTesla(String id) {
		super(id);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack item) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public Container getContainer(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new TileContainer(new GuiData(), player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new GuiPortableTesla(new TileContainer(new GuiData(), player));
	}

	@Override
	public void onPacketFromClient(PacketBuffer dis, EntityPlayer player, ItemStack item, int slot) throws IOException {
		if (item.getTagCompound() == null) item.setTagCompound(new NBTTagCompound());
		byte cmd = dis.readByte();
		if (cmd == 0) item.getTagCompound().setShort("mode", dis.readShort());
		else if (cmd == 1) item.getTagCompound().setShort("freq", dis.readShort());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		BlockGuiHandler.openItemGui(player, hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int s, boolean b) {
		if (!(entity instanceof EntityPlayer) || world.isRemote || item.getTagCompound() == null) return;
		EntityPlayer player = (EntityPlayer)entity;
		TeslaNetwork.instance.transmittEnergy(new TeslaTransmitterItem(player, s, item, Config.Umax[3]));
		float Emax = Config.Umax[3] * Config.Umax[3];
		short mode = item.getTagCompound().getShort("mode");
		if ((mode & 0x7) != 0) {
			InventoryPlayer inv = player.inventory;
			double E0 = item.getTagCompound().getDouble("voltage");
			float E = (float)(E0 *= E0);
			
			if ((mode & 0x0300) == 0x0100)
				for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
					E -= EnergyAPI.get(inv.mainInventory.get(i), 0).addEnergy(E);
			else if ((mode & 0x0300) == 0x0200)
				for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
					E -= EnergyAPI.get(inv.mainInventory.get(i), 0).addEnergy(E - Emax);
			
			if ((mode & 0x0c00) == 0x0400)
				for (int i = InventoryPlayer.getHotbarSize(); i < inv.mainInventory.size(); i++)
					E -= EnergyAPI.get(inv.mainInventory.get(i), 0).addEnergy(E);
			else if ((mode & 0x0c00) == 0x0800)
				for (int i = InventoryPlayer.getHotbarSize(); i < inv.mainInventory.size(); i++)
					E -= EnergyAPI.get(inv.mainInventory.get(i), 0).addEnergy(E - Emax);
			
			if ((mode & 0x3000) == 0x1000)
				for (int i = 0; i < inv.armorInventory.size(); i++)
					E -= EnergyAPI.get(inv.armorInventory.get(i), 0).addEnergy(E);
			else if ((mode & 0x3000) == 0x2000)
				for (int i = 0; i < inv.armorInventory.size(); i++)
					E -= EnergyAPI.get(inv.armorInventory.get(i), 0).addEnergy(E - Emax);
			
			if (E != E0) item.getTagCompound().setDouble("voltage", Math.sqrt(E));
		}
	}

	class GuiData extends ItemGuiData {

		public GuiData() {
			super(ItemPortableTesla.this);
		}

		@Override
		public void initContainer(DataContainer container) {
			TileContainer cont = (TileContainer)container;
			cont.addPlayerInventory(26, 50, true, true);
		}

	}
}
