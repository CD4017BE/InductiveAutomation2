package cd4017be.indaut.jeiPlugin;

import cd4017be.api.recipes.AutomationRecipes.CoolRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class DecompCoolerRecipeHandler implements IRecipeHandler<CoolRecipe> {

	@Override
	public Class<CoolRecipe> getRecipeClass() {
		return CoolRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return "automation.decompCool";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CoolRecipe recipe) {
		return new DecompCoolerRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(CoolRecipe recipe) {
		return Utils.valid(recipe.in0) && Utils.valid(recipe.in1) && Utils.valid(recipe.out0) && Utils.valid(recipe.out1);
	}

	@Override
	public String getRecipeCategoryUid(CoolRecipe arg0) {
		return this.getRecipeCategoryUid();
	}
	
}
