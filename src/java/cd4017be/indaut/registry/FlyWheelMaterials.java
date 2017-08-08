package cd4017be.indaut.registry;

import java.util.HashMap;

import cd4017be.api.recipes.RecipeAPI;
import cd4017be.api.recipes.RecipeAPI.IRecipeHandler;
import cd4017be.lib.script.Parameters;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class FlyWheelMaterials implements IRecipeHandler {
	
	private static final String NAME = "flywheel";
	private static HashMap<Integer, Entry> registry = new HashMap<Integer, Entry>();

	public static void register() {
		RecipeAPI.Handlers.put(NAME, new FlyWheelMaterials());
	}

	public static Entry getFor(ItemStack item) {
		for (int ore : OreDictionary.getOreIDs(item)) {
			Entry e = registry.get(ore);
			if (e != null) return e;
		}
		return null;
	}

	public static Entry getFor(String oreDict) {
		return OreDictionary.doesOreNameExist(oreDict) ? registry.get(OreDictionary.getOreID(oreDict)) : null;
	}

	public static class Entry {

		public final double mass;

		private Entry(double mass) {
			this.mass = mass;
		}

	}

	@Override
	public void addRecipe(Parameters param) {
		String ore = param.getString(1);
		double mass = param.getNumber(2);
		registry.put(OreDictionary.getOreID(ore), new Entry(mass));
	}

}
