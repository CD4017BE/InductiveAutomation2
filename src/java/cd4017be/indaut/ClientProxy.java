package cd4017be.indaut;

import cd4017be.indaut.render.*;
import cd4017be.indaut.render.gui.*;
import cd4017be.indaut.tileentity.FlyWheel;
import cd4017be.indaut.tileentity.Shaft;
import cd4017be.indaut.tileentity.ShaftHandle;
import cd4017be.indaut.tileentity.SolidHeater;
import cd4017be.indaut.tileentity.StirlingEngine;
import cd4017be.lib.BlockItemRegistry;
import cd4017be.lib.ClientInputHandler;
import cd4017be.lib.render.InWorldUIRenderer;
import cd4017be.lib.render.SpecialModelLoader;
import cd4017be.lib.render.model.BlockMimicModel;
import cd4017be.lib.render.model.MultipartModel;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import static cd4017be.indaut.Objects.*;

import cd4017be.indaut.jetpack.JetPackConfig;
import cd4017be.indaut.jetpack.TickHandler;

public class ClientProxy extends CommonProxy {

	public ShaftRenderer shaftRenderer;

	@Override
	public void init() {
		SpecialModelLoader.setMod("indaut");
		JetPackConfig.loadData();
	}

	@Override
	public void registerRenderers() {
		super.registerRenderers();
		TickHandler.init();
		ClientInputHandler.init();
		
		//Blocks
		BlockItemRegistry.registerRender(heatInsulation);
		BlockItemRegistry.registerRender(heatSink);
		BlockItemRegistry.registerRender(flyWheel);
		BlockItemRegistry.registerRender(heatPipe);
		BlockItemRegistry.registerRender(shaft);
		BlockItemRegistry.registerRender(shaftHandle);
		BlockItemRegistry.registerRender(stirlingEngine);
		BlockItemRegistry.registerRender(solidHeater);
		//Items
		BlockItemRegistry.registerRender(rotationSensor);
		BlockItemRegistry.registerRender(thermometer);
		
		SpecialModelLoader.registerBlockModel(flyWheelPart, BlockMimicModel.instance);
		SpecialModelLoader.registerBlockModel(heatPipe, new MultipartModel(heatPipe).setPipeVariants(2));
		
		shaftRenderer = new ShaftRenderer();
		SpecialModelLoader.instance.tesrs.add(shaftRenderer);
		
		ClientRegistry.bindTileEntitySpecialRenderer(Shaft.class, shaftRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(ShaftHandle.class, shaftRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(FlyWheel.class, shaftRenderer);
		InWorldUIRenderer.register(StirlingEngine.class, new GuiStirlingEngine());
		InWorldUIRenderer.register(SolidHeater.class, new GuiSolidHeater());
		
		/*
		//BlockItems
		BlockItemRegistry.registerRender(ore, 0, 2);
		BlockItemRegistry.registerRender(pool);
		BlockItemRegistry.registerRender(unbrStone, 0, 15);
		BlockItemRegistry.registerRender(unbrGlass);
		BlockItemRegistry.registerRender(solarpanel);
		BlockItemRegistry.registerRender(teslaTransmitterLV);
		BlockItemRegistry.registerRender(teslaTransmitter);
		BlockItemRegistry.registerRender(wormhole, 0, 1);
		BlockItemRegistry.registerRender(lightShaft);
		BlockItemRegistry.registerRender(wireC);
		BlockItemRegistry.registerRender(wireA);
		BlockItemRegistry.registerRender(wireH);
		BlockItemRegistry.registerRender(SCSU);
		BlockItemRegistry.registerRender(OCSU);
		BlockItemRegistry.registerRender(CCSU);
		BlockItemRegistry.registerRender(CEU);
		BlockItemRegistry.registerRender(steamEngine);
		BlockItemRegistry.registerRender(steamTurbine);
		BlockItemRegistry.registerRender(steamGenerator);
		BlockItemRegistry.registerRender(steamBoiler);
		BlockItemRegistry.registerRender(lavaCooler);
		BlockItemRegistry.registerRender(energyFurnace);
		BlockItemRegistry.registerRender(farm);
		BlockItemRegistry.registerRender(miner);
		BlockItemRegistry.registerRender(magnet);
		BlockItemRegistry.registerRender(link);
		BlockItemRegistry.registerRender(linkHV);
		BlockItemRegistry.registerRender(texMaker);
		BlockItemRegistry.registerRender(builder);
		BlockItemRegistry.registerRender(algaePool);
		BlockItemRegistry.registerRender(teleporter);
		BlockItemRegistry.registerRender(advancedFurnace);
		BlockItemRegistry.registerRender(pump);
		BlockItemRegistry.registerRender(massstorageChest);
		BlockItemRegistry.registerRender(matterOrb);
		BlockItemRegistry.registerRender(antimatterBombE);
		BlockItemRegistry.registerRender(antimatterBombF);
		BlockItemRegistry.registerRender(antimatterTank);
		BlockItemRegistry.registerRender(antimatterFabricator);
		BlockItemRegistry.registerRender(antimatterAnihilator);
		BlockItemRegistry.registerRender(hpSolarpanel);
		BlockItemRegistry.registerRender(autoCrafting);
		BlockItemRegistry.registerRender(geothermalFurnace);
		BlockItemRegistry.registerRender(steamCompressor);
		BlockItemRegistry.registerRender(electricCompressor);
		BlockItemRegistry.registerRender(tank);
		BlockItemRegistry.registerRender(security);
		BlockItemRegistry.registerRender(decompCooler);
		BlockItemRegistry.registerRender(collector);
		BlockItemRegistry.registerRender(trash);
		BlockItemRegistry.registerRender(electrolyser);
		BlockItemRegistry.registerRender(fuelCell);
		BlockItemRegistry.registerRender(itemSorter);
		BlockItemRegistry.registerRender(matterInterfaceB);
		BlockItemRegistry.registerRender(fluidPacker);
		BlockItemRegistry.registerRender(hugeTank);
		BlockItemRegistry.registerRender(fluidVent);
		BlockItemRegistry.registerRender(gravCond);
		BlockItemRegistry.registerRender(itemBuffer);
		BlockItemRegistry.registerRender(quantumTank);
		BlockItemRegistry.registerRender(vertShemGen);
		BlockItemRegistry.registerRender(heatRadiator);
		BlockItemRegistry.registerRender(shaft);
		BlockItemRegistry.registerRender(electricCoilC);
		BlockItemRegistry.registerRender(electricCoilA);
		BlockItemRegistry.registerRender(electricCoilH);
		BlockItemRegistry.registerRender(electricHeater);
		BlockItemRegistry.registerRender(thermIns);
		BlockItemRegistry.registerRender(pneumaticPiston);
		BlockItemRegistry.registerRender(gasPipe);
		BlockItemRegistry.registerRender(solidFuelHeater);
		BlockItemRegistry.registerRender(gasVent);
		BlockItemRegistry.registerRender(heatedFurnace);
		//Items
		BlockItemRegistry.registerRender(fluidDummy);
		BlockItemRegistry.registerRender(selectionTool, null);
		BlockItemRegistry.registerRender(voltMeter);
		BlockItemRegistry.registerRender(rotationSensor);
		BlockItemRegistry.registerRender(thermometer);
		BlockItemRegistry.registerRender(manometer);
		BlockItemRegistry.registerRender(energyCell);
		BlockItemRegistry.registerRender(chisle);
		BlockItemRegistry.registerRender(cutter);
		BlockItemRegistry.registerRender(portableMagnet);
		BlockItemRegistry.registerRender(builderTexture);
		BlockItemRegistry.registerRender(teleporterCoords);
		BlockItemRegistry.registerRender(stoneDrill);
		BlockItemRegistry.registerRender(ironDrill);
		BlockItemRegistry.registerRender(diamondDrill);
		BlockItemRegistry.registerRender(amLaser);
		BlockItemRegistry.registerRender(mCannon);
		BlockItemRegistry.registerRender(contLiquidAir);
		BlockItemRegistry.registerRender(contAlgaeFood);
		BlockItemRegistry.registerRender(contInvEnergy);
		BlockItemRegistry.registerRender(contJetFuel);
		BlockItemRegistry.registerRender(jetpack);
		BlockItemRegistry.registerRender(jetpackIron);
		BlockItemRegistry.registerRender(jetpackSteel);
		BlockItemRegistry.registerRender(jetpackGraphite);
		BlockItemRegistry.registerRender(jetpackUnbr);
		BlockItemRegistry.registerRender(matterInterface);
		BlockItemRegistry.registerRender(fluidUpgrade);
		BlockItemRegistry.registerRender(itemUpgrade);
		BlockItemRegistry.registerRender(portableFurnace);
		BlockItemRegistry.registerRender(portableInventory);
		BlockItemRegistry.registerRender(portableCrafter);
		BlockItemRegistry.registerRender(portableGenerator);
		BlockItemRegistry.registerRender(portableRemoteInv);
		BlockItemRegistry.registerRender(portableTeleporter);
		BlockItemRegistry.registerRender(portablePump);
		BlockItemRegistry.registerRender(translocator);
		BlockItemRegistry.registerRender(portableTesla);
		BlockItemRegistry.registerRender(placement);
		BlockItemRegistry.registerRender(synchronizer, null);
		BlockItemRegistry.registerRender(remBlockType);
		BlockItemRegistry.registerRender(vertexSel);
		//Tiles
		ClientRegistry.bindTileEntitySpecialRenderer(ItemPipe.class, new ItemPipeRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(FluidPipe.class, new FluidPipeRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(Miner.class, new SelectionRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(Builder.class, new SelectionRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(Farm.class, new SelectionRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(Pump.class, new SelectionRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(Teleporter.class, new SelectionRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(Tank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(AntimatterBomb.class, new TileEntityAntimatterBombRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(VertexShematicGen.class, new Render3DVertexShem());
		ClientRegistry.bindTileEntitySpecialRenderer(Shaft.class, new ShaftRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(PneumaticPiston.class, new PistonRenderer());
		SpecialModelLoader.registerTESRModel("automation:models/tileEntity/shaft");
		SpecialModelLoader.registerTESRModel("automation:models/tileEntity/shaftPermMag");
		SpecialModelLoader.registerTESRModel("automation:models/tileEntity/shaftCoilC");
		SpecialModelLoader.registerTESRModel("automation:models/tileEntity/shaftCoilA");
		SpecialModelLoader.registerTESRModel("automation:models/tileEntity/shaftCoilH");
		SpecialModelLoader.registerTESRModel("automation:models/tileEntity/shaftMass");
		SpecialModelLoader.registerTESRModel("automation:models/tileEntity/shaftGear");
		SpecialModelLoader.registerTESRModel(TileEntityTankRenderer.model);
		SpecialModelLoader.registerTESRModel(PistonRenderer.model);

		TileBlockRegistry.registerGui(steamEngine, GuiSteamEngine.class);
		TileBlockRegistry.registerGui(steamGenerator, GuiSteamGenerator.class);
		TileBlockRegistry.registerGui(steamBoiler, GuiSteamBoiler.class);
		TileBlockRegistry.registerGui(lavaCooler, GuiLavaCooler.class);
		TileBlockRegistry.registerGui(energyFurnace, GuiEnergyFurnace.class);
		TileBlockRegistry.registerGui(SCSU, GuiESU.class);
		TileBlockRegistry.registerGui(OCSU, GuiESU.class);
		TileBlockRegistry.registerGui(CCSU, GuiESU.class);
		TileBlockRegistry.registerGui(CEU, GuiCEU.class);
		TileBlockRegistry.registerGui(farm, GuiFarm.class);
		TileBlockRegistry.registerGui(miner, GuiMiner.class);
		TileBlockRegistry.registerGui(link, GuiELink.class);
		TileBlockRegistry.registerGui(linkHV, GuiELink.class);
		TileBlockRegistry.registerGui(texMaker, GuiTextureMaker.class);
		TileBlockRegistry.registerGui(builder, GuiBuilder.class);
		TileBlockRegistry.registerGui(algaePool, GuiAlgaePool.class);
		TileBlockRegistry.registerGui(teslaTransmitter, GuiTeslaTransmitter.class);
		TileBlockRegistry.registerGui(teslaTransmitterLV, GuiTeslaTransmitterLV.class);
		TileBlockRegistry.registerGui(teleporter, GuiTeleporter.class);
		TileBlockRegistry.registerGui(advancedFurnace, GuiAdvancedFurnace.class);
		TileBlockRegistry.registerGui(pump, GuiPump.class);
		TileBlockRegistry.registerGui(steamTurbine, GuiSteamTurbine.class);
		TileBlockRegistry.registerGui(massstorageChest, GuiMassstorageChest.class);
		TileBlockRegistry.registerGui(antimatterTank, GuiAntimatterTank.class);
		TileBlockRegistry.registerGui(antimatterFabricator, GuiAntimatterFabricator.class);
		TileBlockRegistry.registerGui(antimatterAnihilator, GuiAntimatterAnihilator.class);
		TileBlockRegistry.registerGui(autoCrafting, GuiAutoCrafting.class);
		TileBlockRegistry.registerGui(geothermalFurnace, GuiGeothermalFurnace.class);
		TileBlockRegistry.registerGui(steamCompressor, GuiSteamCompressor.class);
		TileBlockRegistry.registerGui(electricCompressor, GuiElectricCompressor.class);
		TileBlockRegistry.registerGui(tank, GuiTank.class);
		TileBlockRegistry.registerGui(security, GuiSecuritySys.class);
		TileBlockRegistry.registerGui(decompCooler, GuiDecompCooler.class);
		TileBlockRegistry.registerGui(collector, GuiCollector.class);
		TileBlockRegistry.registerGui(trash, GuiTrash.class);
		TileBlockRegistry.registerGui(electrolyser, GuiElectrolyser.class);
		TileBlockRegistry.registerGui(fuelCell, GuiFuelCell.class);
		TileBlockRegistry.registerGui(itemSorter, GuiItemSorter.class);
		TileBlockRegistry.registerGui(fluidPacker, GuiFluidPacker.class);
		TileBlockRegistry.registerGui(hugeTank, GuiHugeTank.class);
		TileBlockRegistry.registerGui(fluidVent, GuiFluidVent.class);
		TileBlockRegistry.registerGui(gravCond, GuiGraviCond.class);
		TileBlockRegistry.registerGui(itemBuffer, GuiItemBuffer.class);
		TileBlockRegistry.registerGui(quantumTank, GuiQuantumTank.class);
		TileBlockRegistry.registerGui(vertShemGen, GuiVertexShematicGen.class);
		TileBlockRegistry.registerGui(heatRadiator, GuiHeatRadiator.class);
		TileBlockRegistry.registerGui(electricCoilC, GuiElectricCoil.class);
		TileBlockRegistry.registerGui(electricCoilA, GuiElectricCoil.class);
		TileBlockRegistry.registerGui(electricCoilH, GuiElectricCoil.class);
		TileBlockRegistry.registerGui(pneumaticPiston, GuiPneumaticPiston.class);
		TileBlockRegistry.registerGui(gasPipe, GuiGasPipe.class);
		TileBlockRegistry.registerGui(solidFuelHeater, GuiSolidFuelHeater.class);
		TileBlockRegistry.registerGui(electricHeater, GuiElectricHeater.class);
		TileBlockRegistry.registerGui(heatedFurnace, GuiHeatedFurnace.class);
		TileBlockRegistry.registerGui(matterInterfaceB, GuiMatterInterface.class);
		//set block transparencies
		Objects.itemPipe.setBlockLayer(BlockRenderLayer.CUTOUT);
		Objects.fluidPipe.setBlockLayer(BlockRenderLayer.CUTOUT);
		Objects.warpPipe.setBlockLayer(BlockRenderLayer.CUTOUT);
		Objects.tank.setBlockLayer(BlockRenderLayer.CUTOUT);
		Objects.hugeTank.setBlockLayer(BlockRenderLayer.CUTOUT);
		Objects.quantumTank.setBlockLayer(BlockRenderLayer.CUTOUT);
		Objects.pool.setBlockLayer(BlockRenderLayer.CUTOUT);
		Objects.wormhole.setBlockLayer(BlockRenderLayer.TRANSLUCENT);
		Objects.pneumaticPiston.setBlockLayer(BlockRenderLayer.CUTOUT);
		//fluids
		SpecialModelLoader.setMod("automation");
		SpecialModelLoader.registerFluid(L_steam);
		SpecialModelLoader.registerFluid(L_waterG);
		SpecialModelLoader.registerFluid(L_biomass);
		SpecialModelLoader.registerFluid(L_antimatter);
		SpecialModelLoader.registerFluid(L_nitrogenG);
		SpecialModelLoader.registerFluid(L_nitrogenL);
		SpecialModelLoader.registerFluid(L_hydrogenG);
		SpecialModelLoader.registerFluid(L_hydrogenL);
		SpecialModelLoader.registerFluid(L_heliumG);
		SpecialModelLoader.registerFluid(L_heliumL);
		SpecialModelLoader.registerFluid(L_oxygenG);
		SpecialModelLoader.registerFluid(L_oxygenL);
		//pipe models
		SpecialModelLoader.registerBlockModel(Objects.itemPipe, new ModelPipe("automation:itemPipe", 3, 5));
		SpecialModelLoader.registerBlockModel(Objects.fluidPipe, new ModelPipe("automation:liquidPipe", 3, 5));
		SpecialModelLoader.registerBlockModel(Objects.wireC, new ModelPipe("automation:wireC", 1, 1));
		SpecialModelLoader.registerBlockModel(Objects.wireA, new ModelPipe("automation:wireA", 1, 1));
		SpecialModelLoader.registerBlockModel(Objects.wireH, new ModelPipe("automation:wireH", 1, 1));
		SpecialModelLoader.registerBlockModel(Objects.shaft, new ModelPipe("automation:shaft", 0, 1));
		SpecialModelLoader.registerBlockModel(Objects.gasPipe, new ModelPipe("automation:gasPipe", 1, 1));
		SpecialModelLoader.registerItemModel(Objects.fluidDummy, new FluidTextures());
		*/
	}

}
