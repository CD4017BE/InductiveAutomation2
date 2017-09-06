package cd4017be.indaut;

import cd4017be.api.recipes.RecipeScriptContext;
import cd4017be.indaut.registry.FlyWheelMaterials;
import cd4017be.indaut.registry.Substances;
import cd4017be.lib.script.ScriptFiles.Version;
import cd4017be.lib.templates.TabMaterials;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.ID, useMetadata = true)
public class Main {

	public static final String ID = "indaut";

	@Instance
	public static Main instance;

	@SidedProxy(serverSide = "cd4017be.indaut.CommonProxy", clientSide = "cd4017be.indaut.ClientProxy")
	public static CommonProxy proxy;

	public Main() {
		RecipeScriptContext.scriptRegistry.add(new Version("inductiveAutomation", 1, "/assets/indaut/config/recipes.rcp"));
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Objects.tabIndAut = new TabMaterials(ID);
		Objects.createBlocks();
		Objects.createItems();
		FlyWheelMaterials.register();
		RecipeScriptContext.instance.run("inductiveAutomation.PRE_INIT");
		proxy.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderers();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Substances.makeDefEnv();
	}
}
