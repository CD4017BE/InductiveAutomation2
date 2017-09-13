package cd4017be.indaut.item;

import cd4017be.lib.DefaultItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAlgaeFood extends DefaultItem {

	public ItemAlgaeFood(String id, int dur) {
		super(id);
		this.setMaxDamage(dur);
		this.setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int slot, boolean par5) {
		if (!(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)entity;
		int food = player.getFoodStats().getFoodLevel();
		if (food <= 18) {
			item.setItemDamage(item.getItemDamage() + 1);
			player.getFoodStats().addStats(2, 1F);
		}
		if (item.getItemDamage() >= item.getMaxDamage())
			player.inventory.setInventorySlotContents(slot, new ItemStack(Items.GLASS_BOTTLE, 3));
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		int dmg = item.getItemDamage();
		if (dmg > 0) {
			dmg = this.getMaxDamage(item) - dmg;
			return super.getItemStackDisplayName(item) + String.format(" (%d left)", dmg);
		}
		return super.getItemStackDisplayName(item);
	}

}
