package cd4017be.indaut.jeiPlugin;

import net.minecraft.item.ItemStack;
import cd4017be.api.recipes.AutomationRecipes;
import cd4017be.indaut.Objects;
import cd4017be.indaut.render.gui.GuiAdvancedFurnace;
import cd4017be.indaut.render.gui.GuiAlgaePool;
import cd4017be.indaut.render.gui.GuiDecompCooler;
import cd4017be.indaut.render.gui.GuiElectricCompressor;
import cd4017be.indaut.render.gui.GuiElectrolyser;
import cd4017be.indaut.render.gui.GuiEnergyFurnace;
import cd4017be.indaut.render.gui.GuiGeothermalFurnace;
import cd4017be.indaut.render.gui.GuiGraviCond;
import cd4017be.indaut.render.gui.GuiPortableCrafting;
import cd4017be.indaut.render.gui.GuiSteamBoiler;
import cd4017be.indaut.render.gui.GuiSteamCompressor;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

/**
 *
 * @author CD4017BE
 */
@JEIPlugin
public class InductiveAutomationPlugin extends BlankModPlugin {

	@Override
	public void register(IModRegistry registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		IRecipeTransferHandlerHelper transferHelper = registry.getJeiHelpers().recipeTransferHandlerHelper();
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		
		registry.addRecipeCategories(
				new AssemblerRecipeCategory(guiHelper), 
				new AdvFurnaceRecipeCategory(guiHelper),
				new DecompCoolerRecipeCategory(guiHelper),
				new ElectrolyserRecipeCategory(guiHelper),
				new GravCondenserRecipeCategory(guiHelper),
				new BioFuelRecipeCategory(guiHelper));
		
		registry.addRecipeHandlers(
				new AssemblerRecipeHandler(), 
				new AdvFurnaceRecipeHandler(),
				new DecompCoolerRecipeHandler(),
				new ElectrolyserRecipeHandler(),
				new GravCondenserRecipeHandler(),
				new BioFuelRecipeHandler());
		
		registry.addRecipeClickArea(GuiGeothermalFurnace.class, 117, 37, 32, 10, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiEnergyFurnace.class, 81, 37, 32, 10, VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeClickArea(GuiGeothermalFurnace.class, 63, 35, 14, 14, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(GuiSteamBoiler.class, 27, 35, 14, 14, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(GuiPortableCrafting.class, 110, 55, 10, 10, VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeClickArea(GuiSteamCompressor.class, 108, 37, 32, 10, "automation.assembler");
		registry.addRecipeClickArea(GuiElectricCompressor.class, 99, 37, 32, 10, "automation.assembler");
		registry.addRecipeClickArea(GuiAdvancedFurnace.class, 97, 37, 18, 10, "automation.advFurnace");
		registry.addRecipeClickArea(GuiDecompCooler.class, 98, 16, 16, 52, "automation.decompCool");
		registry.addRecipeClickArea(GuiElectrolyser.class, 63, 55, 32, 10, "automation.electrolyser");
		registry.addRecipeClickArea(GuiGraviCond.class, 98, 16, 16, 52, "automation.gravCond");
		registry.addRecipeClickArea(GuiAlgaePool.class, 119, 37, 10, 10, "automation.bioFuel");
		
		registry.addRecipes(AutomationRecipes.getCompressorRecipes());
		registry.addRecipes(AutomationRecipes.getAdvancedFurnaceRecipes());
		registry.addRecipes(AutomationRecipes.getCoolerRecipes());
		registry.addRecipes(AutomationRecipes.getElectrolyserRecipes());
		registry.addRecipes(AutomationRecipes.getGraviCondRecipes());
		registry.addRecipes(AutomationRecipes.getBioFuels());
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.autoCrafting), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.portableCrafter), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.geothermalFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.energyFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.portableFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.steamCompressor), "automation.assembler");
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.electricCompressor), "automation.assembler");
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.advancedFurnace), "automation.advFurnace");
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.decompCooler), "automation.decompCool");
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.electrolyser), "automation.electrolyser");
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.gravCond), "automation.gravCond");
		registry.addRecipeCategoryCraftingItem(new ItemStack(Objects.algaePool), "automation.bioFuel");
		
		recipeTransferRegistry.addRecipeTransferHandler(new AssemblerTransferHandler(transferHelper));
		recipeTransferRegistry.addRecipeTransferHandler(new PortableCraftingHandler(transferHelper));
	}

}
