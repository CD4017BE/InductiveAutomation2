package cd4017be.indaut.item;

import cd4017be.api.energy.EnergyAPI;
import cd4017be.api.energy.EnergyAPI.IEnergyAccess;
import cd4017be.api.energy.EnergyAutomation.EnergyItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 *
 * @author CD4017BE
 */
public class ItemInvEnergy extends ItemEnergyCell {
	
	public ItemInvEnergy(String id, int store) {
		super(id, store);
		this.setMaxStackSize(1);
	}

	@Override
	public int getChargeSpeed(ItemStack item) {
		return 1000;
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int i, boolean b) {
		if (!world.isRemote && item.getTagCompound() != null && item.getTagCompound().getBoolean("ON") && entity instanceof EntityPlayer) {
			InventoryPlayer inv = ((EntityPlayer)entity).inventory;
			EnergyItem energy = new EnergyItem(item, this, -2);
			energy.fractal = item.getTagCompound().getFloat("buff");
			IEnergyAccess eit;
			for (int j = 0; j < inv.mainInventory.size(); j++) {
				ItemStack it = inv.mainInventory.get(j);
				if (!(it != null && it.getItem() instanceof ItemInvEnergy) && (eit = EnergyAPI.get(it, 0)) != EnergyAPI.NULL)
					energy.addEnergy(-eit.addEnergy(energy.getStorage()));
			}
			for (int j = 0; j < inv.armorInventory.size(); j++) {
				ItemStack it = inv.armorInventory.get(j);
				if ((eit = EnergyAPI.get(it, 0)) != EnergyAPI.NULL)
					energy.addEnergy(-eit.addEnergy(energy.getStorage()));
			}
			item.getTagCompound().setFloat("buff", (float)energy.fractal);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		if (world.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
		if (item.getTagCompound() == null || !item.getTagCompound().getBoolean("ON")) {
			item.getTagCompound().setBoolean("ON", true);
			player.sendMessage(new TextComponentString("Inventory power supply enabled"));
		} else if (item.getTagCompound().getBoolean("ON")) {
			item.getTagCompound().setBoolean("ON", false);
			player.sendMessage(new TextComponentString("Inventory power supply disabled"));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		if (item.getTagCompound() == null || !item.getTagCompound().getBoolean("ON")) return super.getItemStackDisplayName(item);
		else return super.getItemStackDisplayName(item) + " (On)";
	}

}
