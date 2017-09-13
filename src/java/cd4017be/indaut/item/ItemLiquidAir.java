package cd4017be.indaut.item;

import cd4017be.lib.DefaultItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLiquidAir extends DefaultItem {

	public ItemLiquidAir(String id, int dur) {
		super(id);
		this.setMaxDamage(dur);
		this.setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int slot, boolean par5) {
		if (!(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)entity;
		int air = player.getAir();
		if (air <= 140) {
			item.setItemDamage(item.getItemDamage() + 1);
			player.setAir(air + 20);
		}
		if (item.getItemDamage() >= item.getMaxDamage())
			player.inventory.setInventorySlotContents(slot, new ItemStack(Items.GLASS_BOTTLE, 4));
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		int dmg = item.getItemDamage();
		if (dmg > 0) {
			dmg = getMaxDamage(item) - dmg;
			return super.getItemStackDisplayName(item) + String.format(" (%01d:%02d min)", dmg / 60, dmg % 60);
		}
		return super.getItemStackDisplayName(item);
	}

}
