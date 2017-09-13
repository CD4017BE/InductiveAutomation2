package cd4017be.indaut.item;

import java.util.List;

import cd4017be.lib.DefaultItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CD4017BE
 */
public class ItemInterdimHole extends DefaultItemBlock {
	
	public ItemInterdimHole(Block id) {
		super(id);
	}

	@Override
	public EnumRarity getRarity(ItemStack item) {
		return EnumRarity.RARE;
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		return (item.getItemDamage() != 0 ? "Linked" : "" ) + super.getItemStackDisplayName(item);
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean f) {
		if (item.getItemDamage() != 0 && item.getTagCompound() != null) {
			list.add(String.format("Linked: x= %d ,y= %d ,z= %d in dim %d", item.getTagCompound().getInteger("lx"), item.getTagCompound().getInteger("ly"), item.getTagCompound().getInteger("lz"), item.getTagCompound().getInteger("ld")));
		}
		super.addInformation(item, player, list, f);
	}

}
