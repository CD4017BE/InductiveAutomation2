package cd4017be.indaut.jeiPlugin;

import cd4017be.api.recipes.AutomationRecipes.CmpRecipe;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 *
 * @author CD4017BE
 */
public class AssemblerRecipeHandler implements IRecipeHandler<CmpRecipe> {

	@Override
	public Class<CmpRecipe> getRecipeClass() {
		return CmpRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return "automation.assembler";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CmpRecipe recipe) {
		return new AssemblerRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(CmpRecipe recipe) {
		for (Object o : recipe.input)
			if (!Utils.valid(o)) return false;
		return true;
	}

	@Override
	public String getRecipeCategoryUid(CmpRecipe arg0) {
		return this.getRecipeCategoryUid();
	}

}
