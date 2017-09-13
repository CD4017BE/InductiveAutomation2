package cd4017be.indaut.item;

import java.util.List;

import cd4017be.api.energy.EnergyAutomation.IEnergyItem;
import cd4017be.api.energy.EnergyAutomation.EnergyItem;
import cd4017be.lib.DefaultItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CD4017BE
 */
public class ItemEnergyCell extends DefaultItem implements IEnergyItem {

	private final int storage;

	public ItemEnergyCell(String id, int es) {
		super(id);
		this.storage = es;
		this.setMaxDamage(16);
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean f) {
		new EnergyItem(item, this, -1).addInformation(list);
		super.addInformation(item, player, list, f);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1D - (double)new EnergyItem(stack, this, -1).getStorageI() / (double)this.getEnergyCap(stack);
	}

	@Override
	public int getEnergyCap(ItemStack item) {
		return storage;
	}

	@Override
	public int getChargeSpeed(ItemStack item) {
		return 160;
	}

	@Override
	public String getEnergyTag() {
		return "energy";
	}

}
