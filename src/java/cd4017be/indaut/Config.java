package cd4017be.indaut;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraft.init.Blocks;
import cd4017be.api.automation.AreaProtect;
import cd4017be.api.automation.ProtectLvl;
import cd4017be.api.energy.EnergyAPI;
import cd4017be.api.recipes.AutomationRecipes;
import cd4017be.api.recipes.RecipeScriptContext;
import cd4017be.api.recipes.RecipeScriptContext.ConfigConstants;
import cd4017be.indaut.item.ItemAntimatterLaser;
import cd4017be.indaut.item.ItemAntimatterTank;
import cd4017be.indaut.item.ItemFurnace;
import cd4017be.indaut.item.ItemJetpack;
import cd4017be.indaut.item.ItemJetpackFuel;
import cd4017be.indaut.item.ItemMatterCannon;
import cd4017be.indaut.item.ItemPortablePump;
import cd4017be.indaut.item.ItemPortableTeleporter;
import cd4017be.indaut.tileentity.AdvancedFurnace;
import cd4017be.indaut.tileentity.AntimatterAnihilator;
import cd4017be.indaut.tileentity.AntimatterBomb;
import cd4017be.indaut.tileentity.Builder;
import cd4017be.indaut.tileentity.DecompCooler;
import cd4017be.indaut.tileentity.ElectricCompressor;
import cd4017be.indaut.tileentity.Electrolyser;
import cd4017be.indaut.tileentity.EnergyFurnace;
import cd4017be.indaut.tileentity.Farm;
import cd4017be.indaut.tileentity.GeothermalFurnace;
import cd4017be.indaut.tileentity.GraviCond;
import cd4017be.indaut.tileentity.ItemPipe;
import cd4017be.indaut.tileentity.LiquidPipe;
import cd4017be.indaut.tileentity.Magnet;
import cd4017be.indaut.tileentity.Miner;
import cd4017be.indaut.tileentity.Pump;
import cd4017be.indaut.tileentity.SecuritySys;
import cd4017be.indaut.tileentity.SteamCompressor;
import cd4017be.indaut.tileentity.Teleporter;
import cd4017be.lib.templates.Inventory;

/**
 *
 * @author CD4017BE
 */
public class Config {

	static ConfigConstants data;
	public static final int[] Umax = {240, 1200, 8000, 24000, 120000};//LV, MV, HV, teslaLV, teslaHV
	public static final float[] Rcond = {0.01F, 0.001F, 0.0F};//Basic, Conductive, SuperConductive
	public static final int[] Ecap = {16000, 128000, 1024000};//Single, Octa, Crystal
	public static final int[] tankCap = {1000, 8000, 64000, 4096000, 160000000, 2097152000};//Pipe, Internal, Tank, HugeTank, AntimatterTank, QuantumTank
	//energy
	public static float E_Steam = 0.2F; // kJ/L
	//Power
	public static final float[] Pgenerator = {4.0F, 24.0F, 120.0F, 0.5F, 4.0F, 160.0F, 14400.0F};
	public static final int[] Ugenerator = {240, 1200, 1200, 120, 240, 4000, 8000};
	//steamBoiler, LavaCooler
	public static int steam_water = 200;
	public static int steam_biomassIn = 125;
	public static int maxK_steamBoiler = 6400;
	public static int K_Cooking_steamBoiler = 50;
	public static int steam_biomass_burning = 25;
	public static int Lsteam_Kfuel_burning = 1;
	//BioReactor
	public static float algaeGrowing = 0.00009F;
	public static float algaeDecaying = 0.0002F;
	public static int m_interdimDist = 10000;
	public static int taskQueueSize = 16;
	public static int Rmin = 5;
	public static float Pscale = 0.894427191F;

	public static int get_LSteam_Cooking() {
		return (int)(K_Cooking_steamBoiler / E_Steam);
	}

	public static int get_LWater_Cooking() {
		return get_LSteam_Cooking() / steam_water;
	}

	public static int get_LBiomass_Cooking() {
		return get_LSteam_Cooking() / steam_biomassIn;
	}

	public static void loadConfig() {
		data = new ConfigConstants(RecipeScriptContext.instance.modules.get("inductiveAutomation"));
		//Tuning
		data.getVect("Tiers_Umax", Umax);
		data.getVect("Tiers_Rcond", Rcond);
		data.getVect("Tiers_Ecap", Ecap);
		data.getVect("FluidTiers_TankCap", tankCap);
		data.getVect("Generators_Pmax", Pgenerator);
		data.getVect("Generators_Umax", Ugenerator);
		AntimatterAnihilator.setP(Pgenerator[6]);
		AutomationRecipes.Lnutrients_healAmount = (float)data.getNumber("bioReact_FoodNutr", AutomationRecipes.Lnutrients_healAmount);
		algaeGrowing *= data.getNumber("bioReact_growth", 1.0);
		algaeDecaying *= data.getNumber("bioReact_decay", 1.0);
		//Energy conversions
		E_Steam = (float)data.getNumber("EnergyConv_Steam", E_Steam);
		EnergyAPI.RF_value = (float)data.getNumber("EnergyConv_RF", EnergyAPI.RF_value / 1000F) * 1000F;
		EnergyAPI.EU_value = (float)data.getNumber("EnergyConv_EU", EnergyAPI.EU_value / 1000F) * 1000F;
		EnergyAPI.OC_value = (float)data.getNumber("EnergyConv_OC", EnergyAPI.OC_value / 1000F) * 1000F;
		//Energy usage
		float f = 1000F;
		Inventory.ticks = (int)data.getNumber("inventory_ticks", Inventory.ticks);
		ItemPipe.ticks = (byte)data.getNumber("itemPipe_ticks", ItemPipe.ticks);
		LiquidPipe.ticks = (byte)data.getNumber("fluidPipe_ticks", LiquidPipe.ticks);
		Builder.Energy = f * (float)data.getNumber("Builder_Euse", Builder.Energy / f);
		Builder.resistor = (float)data.getNumber("Builder_Rwork", Builder.resistor);
		Builder.eScale = (float)Math.sqrt(1D - 1D / Builder.resistor);
		Builder.Umax = (int)data.getNumber("Builder_Umax", Builder.Umax);
		Builder.maxSpeed = (byte)data.getNumber("Builder_maxSpeed", Builder.maxSpeed);
		ElectricCompressor.Energy = (float)data.getNumber("ElCompr_Euse", ElectricCompressor.Energy);
		ElectricCompressor.Umax = (int)data.getNumber("ElCompr_Umax", ElectricCompressor.Umax);
		SteamCompressor.Euse = (int)data.getNumber("StCompr_Euse", SteamCompressor.Euse);
		EnergyFurnace.Euse = (float)data.getNumber("Furnace_Euse", EnergyFurnace.Euse);
		EnergyFurnace.Umax = (int)data.getNumber("Furnace_Umax", EnergyFurnace.Umax);
		GeothermalFurnace.Euse = (int)data.getNumber("GeothFurn_Euse", GeothermalFurnace.Euse);
		AdvancedFurnace.Umax = (int)data.getNumber("AdvFurn_Umax", AdvancedFurnace.Umax);
		DecompCooler.Umax = (int)data.getNumber("Cooler_Umax", DecompCooler.Umax);
		Electrolyser.Umax = (int)data.getNumber("Electrolyser_Umax", Electrolyser.Umax);
		Magnet.Energy = f * (float)data.getNumber("Magnet_Euse", Magnet.Energy / f);
		Magnet.Umax = (int)data.getNumber("Magnet_Umax", Magnet.Umax);
		Farm.Energy = f * (float)data.getNumber("Farm_Euse", Farm.Energy / f);
		Farm.resistor = (float)data.getNumber("Farm_Rwork", Farm.resistor);
		Farm.eScale = (float)Math.sqrt(1D - 1D / Farm.resistor);
		Farm.Umax = (int)data.getNumber("Farm_Umax", Farm.Umax);
		Miner.Energy = f * (float)data.getNumber("Miner_Emult", Miner.Energy / f);
		Miner.resistor = (float)data.getNumber("Miner_Rwork", Miner.resistor);
		Miner.eScale = (float)Math.sqrt(1D - 1D / Miner.resistor);
		Miner.Umax = (int)data.getNumber("Miner_Umax", Miner.Umax);
		Miner.maxSpeed = (byte)data.getNumber("Miner_maxSpeed", Miner.maxSpeed);
		Pump.Energy = f * (float)data.getNumber("Pump_Euse", Pump.Energy / f);
		Pump.resistor = (float)data.getNumber("Pump_Rwork", Pump.resistor);
		Pump.eScale = (float)Math.sqrt(1D - 1D / Pump.resistor);
		Pump.Umax = (int)data.getNumber("Pump_Umax", Pump.Umax);
		Teleporter.Energy = f * (float)data.getNumber("Teleport_Emult", Teleporter.Energy / f);
		Teleporter.resistor = (float)data.getNumber("Teleport_Rwork", Teleporter.resistor);
		Teleporter.eScale = (float)Math.sqrt(1D - 1D / Teleporter.resistor);
		Teleporter.Umax = (int)data.getNumber("Teleporter_Umax", Teleporter.Umax);
		ItemAntimatterLaser.EnergyUsage = (int)data.getNumber("Tool_AMLaser_Euse", ItemAntimatterLaser.EnergyUsage);
		ItemFurnace.energyUse = (int)data.getNumber("Tool_Furnace_Euse", ItemFurnace.energyUse);
		ItemMatterCannon.EnergyUsage = (int)data.getNumber("Tool_MCannon_Euse", ItemMatterCannon.EnergyUsage);
		ItemPortablePump.energyUse = (int)data.getNumber("Tool_Pump_Euse", ItemPortablePump.energyUse);
		ItemPortableTeleporter.energyUse = (float)data.getNumber("Tool_Teleport_Emult", ItemPortableTeleporter.energyUse);
		//Antimatter Bomb
		AntimatterBomb.maxSize = (int)data.getNumber("AmBomb_MaxRad", AntimatterBomb.maxSize);
		AntimatterBomb.explMult = (float)data.getNumber("AmBomb_ExplMult", AntimatterBomb.explMult);
		ItemAntimatterLaser.AmUsage = (float)data.getNumber("Tool_AmLaser_AMmult", ItemAntimatterLaser.AmUsage);
		ItemAntimatterLaser.AMDamage = (float)data.getNumber("Tool_AmLaser_AMDmgMult", ItemAntimatterLaser.AMDamage);
		ItemAntimatterLaser.AMDmgExp = (float)data.getNumber("Tool_AmLaser_AMDmgExp", ItemAntimatterLaser.AMDmgExp);
		ItemAntimatterLaser.BaseDamage = (float)data.getNumber("Tool_AmLaser_MinDmg", ItemAntimatterLaser.BaseDamage);
		ItemAntimatterLaser.MaxDamage = (float)data.getNumber("Tool_AmLaser_MaxDmg", ItemAntimatterLaser.MaxDamage);
		ItemAntimatterLaser.DamageMult = (float)data.getNumber("Tool_AmLaser_AMmult", ItemAntimatterLaser.DamageMult);
		ItemAntimatterTank.BombMaxCap = (int)data.getNumber("AmBomb_MaxAM", ItemAntimatterTank.BombMaxCap);
		ItemAntimatterTank.explFaktor = AntimatterBomb.PowerFactor * AntimatterBomb.explMult / Blocks.STONE.getExplosionResistance(null) * 0.125D;
		ItemJetpackFuel.H2Mult = (float)data.getNumber("Jetpack_H2_val", ItemJetpackFuel.H2Mult * 1000F) / 1000F;
		ItemJetpackFuel.O2Mult = ItemJetpackFuel.H2Mult * 2F;
		ItemJetpackFuel.electricEmult = (float)data.getNumber("Jetpack_el_val", ItemJetpackFuel.electricEmult * 1000F) / 1000F;
		GraviCond.energyCost = (float)data.getNumber("GravCond_Euse", GraviCond.energyCost);
		GraviCond.forceEnergy = (float)data.getNumber("GravCond_Eforce", GraviCond.forceEnergy);
		GraviCond.Umax = (int)data.getNumber("GravCond_Umax", GraviCond.Umax);
		GraviCond.blockWeight = (int)data.getNumber("GravCond_mBlock", GraviCond.blockWeight);
		GraviCond.itemWeight = (int)data.getNumber("GravCond_mItem", GraviCond.itemWeight);
		taskQueueSize = (int)data.getNumber("computer_taskQueue_size", taskQueueSize);
		Rmin = (int)data.getNumber("Rwork_min", Rmin);
		if (Rmin < 1) Rmin = 1;
		Pscale = (float)Math.sqrt(1D - 1D / (double)Rmin);
		AreaProtect.permissions = (byte)data.getNumber("SecuritySys_permMode", AreaProtect.permissions);
		AreaProtect.chunkloadPerm = (byte)data.getNumber("SecuritySys_chunkloadPerm", AreaProtect.chunkloadPerm);
		AreaProtect.maxChunksPBlock = (byte)data.getNumber("SecuritySys_maxChunks", AreaProtect.maxChunksPBlock);
		if (AreaProtect.maxChunksPBlock < 0) {AreaProtect.maxChunksPBlock = 0; AreaProtect.chunkloadPerm = -1;}
		else if (AreaProtect.maxChunksPBlock > 64) AreaProtect.maxChunksPBlock = 64;
		if (AreaProtect.maxChunksPBlock > ForgeChunkManager.getMaxTicketLengthFor("Automation")) {
			FMLLog.log("Automation", Level.INFO, "set Forge config property \"%s\" to %d", "maximumChunksPerTicket", AreaProtect.maxChunksPBlock);
			ForgeChunkManager.addConfigProperty(Main.instance, "maximumChunksPerTicket", "" + AreaProtect.maxChunksPBlock, Property.Type.INTEGER);
		}
		float[] af = data.getVect("SecuritySys_Euse", new float[]{ProtectLvl.Free.energyCost, ProtectLvl.Protected.energyCost, ProtectLvl.NoAcces.energyCost, ProtectLvl.NoInventory.energyCost, AreaConfig.ChunkLoadCost});
		for (ProtectLvl lvl : ProtectLvl.values()) lvl.energyCost = af[lvl.ordinal()];
		AreaConfig.ChunkLoadCost = af[4];
		SecuritySys.Umax = (int)data.getNumber("SecuritySys_Umax", SecuritySys.Umax);
		SecuritySys.Ecap = (float)data.getNumber("SecuritySys_Ecap", SecuritySys.Ecap);
		ItemJetpack.durability = data.getVect("Jetpack_armor_dur", ItemJetpack.durability);
		ItemJetpack.absorption = data.getVect("Jetpack_armor_abs", ItemJetpack.absorption);
	}

}
