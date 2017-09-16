package cd4017be.indaut;

import cd4017be.api.Capabilities.EmptyCallable;
import cd4017be.api.Capabilities.EmptyStorage;
import cd4017be.indaut.block.*;
import cd4017be.indaut.item.*;
import cd4017be.indaut.multiblock.BasicWarpPipe;
import cd4017be.indaut.multiblock.HeatPipeComp;
import cd4017be.indaut.multiblock.IHeatReservoir;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.tileentity.*;
import cd4017be.lib.DefaultBlock;
import cd4017be.lib.DefaultFluid;
import cd4017be.lib.DefaultItem;
import cd4017be.lib.DefaultItemBlock;
import cd4017be.lib.TileBlock;
import cd4017be.lib.block.BaseBlock;
import cd4017be.lib.block.BlockPipe;
import cd4017be.lib.block.OrientedBlock;
import cd4017be.lib.property.PropertyOrientation;
import cd4017be.lib.templates.BlockSuperfluid;
import cd4017be.lib.templates.TabMaterials;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class Objects {

	public static TabMaterials tabIndAut;
	public static CreativeTabs tabFluids;
	
	@CapabilityInject(IKineticInteraction.class)
	public static Capability<IKineticInteraction> KINETIC_CAP;
	@CapabilityInject(IHeatReservoir.class)
	public static Capability<IHeatReservoir> HEAT_CAP;
	@CapabilityInject(HeatPipeComp.class)
	public static Capability<HeatPipeComp> HEAT_PIPE_CAP;
	@CapabilityInject(BasicWarpPipe.class)
	public static Capability<BasicWarpPipe> WARP_PIPE_CAP;

	public static ItemRotationSensor rotationSensor;
	public static ItemThermometer thermometer;

	public static BlockShaft shaft;
	public static BlockShaft shaftHandle;
	public static BlockShaft flyWheel;
	public static BlockMultiblockPart flyWheelPart;
	public static BlockPipe heatPipe;
	public static OrientedBlock heatSink;
	public static BaseBlock heatInsulation;
	public static OrientedBlock stirlingEngine;
	public static OrientedBlock solidHeater;
	public static BlockPipe itemPipe;
	public static BlockPipe fluidPipe;
	public static BlockPipe warpPipe;

	
	//Items
	public static ItemSelectionTool selectionTool;
	public static ItemVoltMeter voltMeter;
	public static ItemManometer manometer;
	public static ItemEnergyCell energyCell;
	public static ItemEnergyTool chisle;
	public static ItemCutter cutter;
	public static ItemPortableMagnet portableMagnet;
	public static ItemBuilderTexture builderTexture;
	public static ItemTeleporterCoords teleporterCoords;
	public static ItemMinerDrill stoneDrill;
	public static ItemMinerDrill ironDrill;
	public static ItemMinerDrill diamondDrill;
	public static ItemAntimatterLaser amLaser;
	public static ItemMatterCannon mCannon;
	public static ItemLiquidAir contLiquidAir;
	public static ItemAlgaeFood contAlgaeFood;
	public static ItemInvEnergy contInvEnergy;
	public static ItemJetpackFuel contJetFuel;
	public static ItemJetpack jetpack;
	public static ItemJetpack jetpackIron;
	public static ItemJetpack jetpackSteel;
	public static ItemJetpack jetpackGraphite;
	public static ItemJetpack jetpackUnbr;
	public static ItemMatterInterface matterInterface;
	public static ItemFluidDummy fluidDummy;
	public static ItemFluidUpgrade fluidUpgrade;
	public static ItemItemUpgrade itemUpgrade;
	public static ItemFurnace portableFurnace;
	public static ItemInventory portableInventory;
	public static ItemPortableCrafter portableCrafter;
	public static ItemPortableGenerator portableGenerator;
	public static ItemRemoteInv portableRemoteInv;
	public static ItemPortableTeleporter portableTeleporter;
	public static ItemPortablePump portablePump;
	public static ItemTranslocator translocator;
	public static ItemPortableTesla portableTesla;
	public static ItemPlacement placement;
	public static ItemMachineSynchronizer synchronizer;
	public static DefaultItem remBlockType;
	public static ItemVertexSel vertexSel;

	//Blocks
	public static BlockOre ore;
	public static TileBlock pool;
	public static BlockUnbreakable unbrStone;
	public static GlassUnbreakable unbrGlass;
	public static BlockSkyLight light;
	public static GhostBlock placementHelper;
	public static TileBlock solarpanel;
	public static TileBlock teslaTransmitterLV;
	public static TileBlock teslaTransmitter;
	public static TileBlock wormhole;
	public static TileBlock lightShaft;
	public static BlockPipe wireC;
	public static BlockPipe wireA;
	public static BlockPipe wireH;
	public static TileBlock SCSU;
	public static TileBlock OCSU;
	public static TileBlock CCSU;
	public static TileBlock CEU;
	public static TileBlock steamEngine;
	public static TileBlock steamTurbine;
	public static TileBlock steamGenerator;
	public static TileBlock steamBoiler;
	public static TileBlock lavaCooler;
	public static TileBlock energyFurnace;
	public static TileBlock farm;
	public static TileBlock miner;
	public static TileBlock magnet;
	public static TileBlock link;
	public static TileBlock linkHV;
	public static TileBlock texMaker;
	public static TileBlock builder;
	public static TileBlock algaePool;
	public static TileBlock teleporter;
	public static TileBlock advancedFurnace;
	public static TileBlock pump;
	public static TileBlock massstorageChest;
	public static TileBlock matterOrb;
	public static TileBlock antimatterBombE;
	public static TileBlock antimatterBombF;
	public static TileBlock antimatterTank;
	public static TileBlock antimatterFabricator;
	public static TileBlock antimatterAnihilator;
	public static TileBlock hpSolarpanel;
	public static TileBlock autoCrafting;
	public static TileBlock geothermalFurnace;
	public static TileBlock steamCompressor;
	public static TileBlock electricCompressor;
	public static TileBlock tank;
	public static TileBlock security;
	public static TileBlock decompCooler;
	public static TileBlock collector;
	public static TileBlock trash;
	public static TileBlock electrolyser;
	public static TileBlock fuelCell;
	public static TileBlock itemSorter;
	public static TileBlock matterInterfaceB;
	public static TileBlock fluidPacker;
	public static TileBlock hugeTank;
	public static TileBlock fluidVent;
	public static TileBlock gravCond;
	public static TileBlock itemBuffer;
	public static TileBlock quantumTank;
	public static TileBlock vertShemGen;
	public static TileBlock heatRadiator;
	public static TileBlock electricCoilC;
	public static TileBlock electricCoilA;
	public static TileBlock electricCoilH;
	public static TileBlock electricHeater;
	public static DefaultBlock thermIns;
	public static TileBlock pneumaticPiston;
	public static BlockPipe gasPipe;
	public static TileBlock solidFuelHeater;
	public static TileBlock gasVent;
	public static TileBlock heatedFurnace;
	//Fluids
	public static Fluid L_water; //1000g/l
	public static Fluid L_lava; //??
	public static Fluid L_steam; //5g/l (8xComp) :x200
	public static Fluid L_waterG; //0.625g/l :x1600
	public static Fluid L_biomass;
	public static Fluid L_antimatter;
	public static Fluid L_nitrogenG; //1.25g/l, 273K :x640
	public static Fluid L_nitrogenL; //800g/l, 77K
	public static Fluid L_hydrogenG; //.09g/l, 273K :x800 
	public static Fluid L_hydrogenL; //72g/l, 21K
	public static Fluid L_heliumG; //.18g/l, 273K :x800
	public static Fluid L_heliumL; //144g/l, 4K
	public static Fluid L_oxygenG; //1.62g/l, 273K :x800
	public static Fluid L_oxygenL; //1296g/l, 90K

	static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(IKineticInteraction.class, new EmptyStorage<IKineticInteraction>(), new EmptyCallable<IKineticInteraction>());
		CapabilityManager.INSTANCE.register(IHeatReservoir.class, new EmptyStorage<IHeatReservoir>(), new EmptyCallable<IHeatReservoir>());
		CapabilityManager.INSTANCE.register(HeatPipeComp.class, new EmptyStorage<HeatPipeComp>(), new EmptyCallable<HeatPipeComp>());
		CapabilityManager.INSTANCE.register(BasicWarpPipe.class, new EmptyStorage<BasicWarpPipe>(), new EmptyCallable<BasicWarpPipe>());
	}

	static void createBlocks() {
		new DefaultItemBlock((shaft = new BlockShaft("shaft", Material.IRON, SoundType.METAL, Shaft.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((shaftHandle = new BlockShaft("shaft_handle", Material.IRON, SoundType.METAL, ShaftHandle.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((flyWheel = new BlockShaft("fly_wheel", Material.IRON, SoundType.METAL, FlyWheel.class)).setCreativeTab(tabIndAut));
		flyWheelPart = new BlockMultiblockPart("fly_wheel_part", Material.IRON, SoundType.METAL, MultiblockPart.class);
		new DefaultItemBlock((heatPipe = BlockPipe.create("heat_pipe", Material.IRON, SoundType.METAL, HeatPipe.class, 1).setSize(0.5)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((heatSink = OrientedBlock.create("heat_sink", Material.IRON, SoundType.METAL, 3, null, PropertyOrientation.XY_12_ROT)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((heatInsulation = new BaseBlock("heat_insulation", Material.ROCK)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((stirlingEngine = OrientedBlock.create("stirling_engine", Material.IRON, SoundType.METAL, 3, StirlingEngine.class, PropertyOrientation.ALL_AXIS)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((solidHeater = OrientedBlock.create("solid_heater", Material.ROCK, SoundType.METAL, 2, SolidHeater.class, PropertyOrientation.HOR_AXIS)).setCreativeTab(tabIndAut));
		new ItemItemPipe((itemPipe = BlockPipe.create("item_pipe", Material.WOOD, SoundType.WOOD, ItemPipe.class, 3)).setCreativeTab(tabIndAut).setHardness(0.5F));
		new ItemLiquidPipe((fluidPipe = BlockPipe.create("fluid_pipe", Material.GLASS, SoundType.GLASS, FluidPipe.class, 3)).setCreativeTab(tabIndAut).setHardness(0.5F));
		new DefaultItemBlock((warpPipe = BlockPipe.create("warp_pipe", Material.IRON, SoundType.METAL, WarpPipe.class, 1)).setCreativeTab(tabIndAut).setHardness(1.0F).setResistance(20F));
		/*
		new DefaultItemBlock((heatRadiator = TileBlock.create("heatRadiator", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((lightShaft = TileBlock.create("lightShaft", Material.GLASS, SoundType.GLASS, 0)).setCreativeTab(tabIndAut).setHardness(1.0F).setResistance(10F));
		new DefaultItemBlock((wireC = new BlockPipe("wireC", Material.IRON, SoundType.METAL, 0x20)).setCreativeTab(tabIndAut).setHardness(0.5F).setResistance(10F));
		new DefaultItemBlock((wireA = new BlockPipe("wireA", Material.IRON, SoundType.METAL, 0x20)).setCreativeTab(tabIndAut).setHardness(0.5F).setResistance(10F));
		new DefaultItemBlock((wireH = new BlockPipe("wireH", Material.IRON, SoundType.METAL, 0x20)).setCreativeTab(tabIndAut).setHardness(0.5F).setResistance(10F));
		new ItemESU((SCSU = TileBlock.create("SCSU", Material.IRON, SoundType.METAL, 0x40)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new ItemESU((OCSU = TileBlock.create("OCSU", Material.IRON, SoundType.METAL, 0x40)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new ItemESU((CCSU = TileBlock.create("CCSU", Material.IRON, SoundType.METAL, 0x40)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((steamEngine = TileBlock.create("steamEngine", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((steamTurbine = TileBlock.create("steamTurbine", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(10F));
		new DefaultItemBlock((steamGenerator = TileBlock.create("steamGenerator", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(2.0F).setResistance(10F));
		new DefaultItemBlock((steamBoiler = TileBlock.create("steamBoiler", Material.IRON, SoundType.STONE, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((lavaCooler = TileBlock.create("lavaCooler", Material.ROCK, SoundType.STONE, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((energyFurnace = TileBlock.create("energyFurnace", Material.IRON, SoundType.STONE, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((farm = TileBlock.create("farm", Material.IRON, SoundType.STONE, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((miner = TileBlock.create("miner", Material.IRON, SoundType.STONE, 0x10)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((magnet = TileBlock.create("magnet", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((link = TileBlock.create("link", Material.IRON, SoundType.METAL, 2)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((linkHV = TileBlock.create("linkHV", Material.IRON, SoundType.METAL, 2)).setCreativeTab(tabIndAut).setHardness(2.0F).setResistance(10F));
		new DefaultItemBlock((texMaker = TileBlock.create("texMaker", Material.WOOD, SoundType.WOOD, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((builder = TileBlock.create("builder", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((algaePool = TileBlock.create("algaePool", Material.GLASS, SoundType.GLASS, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((teleporter = TileBlock.create("teleporter", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((advancedFurnace = TileBlock.create("advancedFurnace", Material.IRON, SoundType.METAL, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((pump = TileBlock.create("pump", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((massstorageChest = TileBlock.create("massstorageChest", Material.WOOD, SoundType.WOOD, 0x41)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(50F));
		new ItemMatterOrb((matterOrb = TileBlock.create("matterOrb", Material.IRON, SoundType.STONE, 0x40)).setCreativeTab(tabIndAut).setHardness(1.0F).setResistance(10F));
		new ItemMatterOrb((antimatterBombE = TileBlock.create("antimatterBombE", Material.TNT, SoundType.STONE, 0x40)).setCreativeTab(tabIndAut).setHardness(1.0F).setResistance(10F));
		new ItemAntimatterTank((antimatterBombF = TileBlock.create("antimatterBombF", Material.TNT, SoundType.STONE, 0x41)).setCreativeTab(tabIndAut).setHardness(1.0F).setResistance(10F));
		new ItemAntimatterTank((antimatterTank = TileBlock.create("antimatterTank", Material.IRON, SoundType.METAL, 0x40)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(40F));
		new DefaultItemBlock((antimatterFabricator = TileBlock.create("antimatterFabricator", Material.IRON, SoundType.METAL, 1)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(40F));
		new DefaultItemBlock((antimatterAnihilator = TileBlock.create("antimatterAnihilator", Material.IRON, SoundType.METAL, 1)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(40F));
		new DefaultItemBlock((hpSolarpanel = TileBlock.create("hpSolarpanel", Material.GLASS, SoundType.GLASS, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((autoCrafting = TileBlock.create("autoCrafting", Material.WOOD, SoundType.WOOD, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((geothermalFurnace = TileBlock.create("geothermalFurnace", Material.ROCK, SoundType.STONE, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((steamCompressor = TileBlock.create("steamCompressor", Material.IRON, SoundType.METAL, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((electricCompressor = TileBlock.create("electricCompressor", Material.IRON, SoundType.METAL, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new ItemTank((tank = TileBlock.create("tank", Material.GLASS, SoundType.GLASS, 0x60)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((security = TileBlock.create("security", Material.IRON, SoundType.METAL, 0x1)).setCreativeTab(tabIndAut).setBlockUnbreakable().setResistance(1000000F));
		new DefaultItemBlock((decompCooler = TileBlock.create("decompCooler", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(2.0F).setResistance(10F));
		new DefaultItemBlock((collector = TileBlock.create("collector", Material.IRON, SoundType.METAL, 2)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((trash = TileBlock.create("trash", Material.ROCK, SoundType.STONE, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((electrolyser = TileBlock.create("electrolyser", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((fuelCell = TileBlock.create("fuelCell", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((itemSorter = TileBlock.create("itemSorter", Material.WOOD, SoundType.WOOD, 0)).setCreativeTab(tabIndAut).setHardness(0.5F).setResistance(10F));
		new DefaultItemBlock((matterInterfaceB = TileBlock.create("matterInterfaceB", Material.IRON, SoundType.METAL, 0)).setCreativeTab(tabIndAut).setHardness(2.0F).setResistance(10F));
		new DefaultItemBlock((fluidPacker = TileBlock.create("fluidPacker", Material.IRON, SoundType.METAL, 1)).setCreativeTab(tabIndAut).setHardness(2.0F).setResistance(10F));
		new ItemHugeTank((hugeTank = TileBlock.create("hugeTank", Material.GLASS, SoundType.GLASS, 0x60)).setCreativeTab(tabIndAut).setHardness(2.0F).setResistance(15F));
		new DefaultItemBlock((fluidVent = TileBlock.create("fluidVent", Material.IRON, SoundType.METAL, 2)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((gravCond = TileBlock.create("gravCond", Material.IRON, SoundType.METAL, 0x10)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(20F));
		new DefaultItemBlock((itemBuffer = TileBlock.create("itemBuffer", Material.WOOD, SoundType.WOOD, 0)).setCreativeTab(tabIndAut).setHardness(0.5F).setResistance(10F));
		new ItemQuantumTank((quantumTank = TileBlock.create("quantumTank", Material.GLASS, SoundType.GLASS, 0x60)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(20F));
		new DefaultItemBlock((vertShemGen = TileBlock.create("vertShemGen", Material.ROCK, SoundType.WOOD, 0)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new ItemOre((ore = new BlockOre("ore")).setHardness(2.0F).setResistance(10F));
		new DefaultItemBlock((pool = TileBlock.create("pool", Material.GLASS, SoundType.STONE, 0x20)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F).setLightOpacity(5));
		new ItemBlockUnbreakable(unbrStone = new BlockUnbreakable("unbrStone"));
		new DefaultItemBlock(unbrGlass = new GlassUnbreakable("unbrGlass"));
		new DefaultItemBlock((solarpanel = TileBlock.create("solarpanel", Material.GLASS, SoundType.GLASS, 0x20)).setBlockBounds(new AxisAlignedBB(0, 0, 0, 1, 0.125, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((teslaTransmitterLV = TileBlock.create("teslaTransmitterLV", Material.IRON, SoundType.METAL, 0x60)).setBlockBounds(new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 1, 0.8125)).setCreativeTab(tabIndAut).setHardness(2.0F).setResistance(15F));
		new DefaultItemBlock((teslaTransmitter = TileBlock.create("teslaTransmitter", Material.IRON, SoundType.METAL, 0x60)).setBlockBounds(new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(20F));
		new ItemInterdimHole((wormhole = TileBlock.create("wormhole", Material.PORTAL, SoundType.GLASS, 0x60)).setBlockBounds(new AxisAlignedBB(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375)).setCreativeTab(tabIndAut).setHardness(2.5F).setResistance(20F));
		new DefaultItemBlock((electricCoilC = TileBlock.create("electricCoilC", Material.IRON, SoundType.METAL, 0x22)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((electricCoilA = TileBlock.create("electricCoilA", Material.IRON, SoundType.METAL, 0x22)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((electricCoilH = TileBlock.create("electricCoilH", Material.IRON, SoundType.METAL, 0x22)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((electricHeater = TileBlock.create("electricHeater", M_thermIns, SoundType.METAL, 0x22)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((thermIns = new DefaultBlock("thermIns", M_thermIns)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((pneumaticPiston = TileBlock.create("pneumaticPiston", Material.IRON, SoundType.METAL, 0x22)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((gasPipe = new BlockPipe("gasPipe", Material.IRON, SoundType.METAL, 0x20)).setCreativeTab(tabIndAut).setHardness(1.0F).setResistance(20F));
		new DefaultItemBlock((solidFuelHeater = TileBlock.create("solidFuelHeater", Material.ROCK, SoundType.STONE, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((gasVent = TileBlock.create("gasVent", Material.GLASS, SoundType.GLASS, 0x22)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((heatedFurnace = TileBlock.create("heatedFurnace", Material.ROCK, SoundType.STONE, 1)).setCreativeTab(tabIndAut).setHardness(1.5F).setResistance(10F));
		new DefaultItemBlock((CEU = TileBlock.create("CEU", Material.IRON, SoundType.METAL, 1)).setCreativeTab(tabIndAut).setBlockUnbreakable().setResistance(Float.POSITIVE_INFINITY));
		gasPipe.size = 0.5F;
		ore.setHarvestLevel("pickaxe", 1, ore.getStateFromMeta(Ore.Copper.ordinal()));
		ore.setHarvestLevel("pickaxe", 2, ore.getStateFromMeta(Ore.Silver.ordinal()));
		ore.setHarvestLevel("pickaxe", 1, ore.getStateFromMeta(Ore.Aluminium.ordinal()));
		*/
		tabIndAut.item = new ItemStack(shaft);
	}

	static void createItems() {
		(rotationSensor = new ItemRotationSensor("rotation_sensor")).setCreativeTab(tabIndAut);
		(thermometer = new ItemThermometer("thermometer")).setCreativeTab(tabIndAut);
		/*
		selectionTool = new ItemSelectionTool("selectionTool");
		voltMeter = new ItemVoltMeter("voltMeter");
		manometer = new ItemManometer("manometer");
		energyCell = new ItemEnergyCell("energyCell", Config.Ecap[0]);
		chisle = new ItemEnergyTool("chisle", Config.Ecap[0], (int)Config.data.getNumber("Chisle_Euse", 25), (float)Config.data.getNumber("Chisle_digSpeed", 16F), 4).setToolClass(new String[]{"pickaxe", "shovel"}, (int)Config.data.getNumber("Chisle_harvestLvl", 3));
		cutter = new ItemCutter("cutter", Config.Ecap[0], (int)Config.data.getNumber("Cutter_Euse", 25), (float)Config.data.getNumber("Cutter_Dmg", 7), (float)Config.data.getNumber("Cutter_digSpeed", 16F), (float)Config.data.getNumber("Cutter_delay", 4));
		portableMagnet = new ItemPortableMagnet("portableMagnet", Config.Ecap[0]);
		builderTexture = new ItemBuilderTexture("builderTexture");
		teleporterCoords = new ItemTeleporterCoords("teleporterCoords");
		int[] dur = Config.data.getVect("minerDrill_durability", new int[]{4096, 8192, 16384});
		int[] harvst = Config.data.getVect("minerDrill_harvestLvl", new int[]{1, 2, 3});
		float[] eff = Config.data.getVect("minerDrill_efficiency", new float[]{1.5F, 2.0F, 4.0F});
		stoneDrill = new ItemMinerDrill("stoneDrill", dur[0], harvst[0], eff[0], ItemMinerDrill.defaultClass);
		ironDrill = new ItemMinerDrill("ironDrill", dur[1],harvst[1], eff[1], ItemMinerDrill.defaultClass);
		diamondDrill = new ItemMinerDrill("diamondDrill", dur[2], harvst[2], eff[2], ItemMinerDrill.defaultClass);
		amLaser = new ItemAntimatterLaser("amLaser");
		mCannon = new ItemMatterCannon("mCannon");
		contLiquidAir = new ItemLiquidAir("contLiquidAir", (int)Config.data.getNumber("Tool.AirTank.dur", 600));
		contAlgaeFood = new ItemAlgaeFood("contAlgaeFood", (int)Config.data.getNumber("Tool.AlgaeFood.dur", 250));
		contInvEnergy = new ItemInvEnergy("contInvEnergy", Config.Ecap[2]);
		contJetFuel = new ItemJetpackFuel("contJetFuel");
		jetpack = new ItemJetpack("jetpack", 0);
		jetpackIron = new ItemJetpack("jetpackIron", 1);
		jetpackSteel = new ItemJetpack("jetpackSteel", 2);
		jetpackGraphite = new ItemJetpack("jetpackGraphite", 3);
		jetpackUnbr = new ItemJetpack("jetpackUnbr", 4);
		matterInterface = new ItemMatterInterface("matterInterface");
		fluidDummy = new ItemFluidDummy("fluidDummy");
		fluidUpgrade = new ItemFluidUpgrade("fluidUpgrade");
		itemUpgrade = new ItemItemUpgrade("itemUpgrade");
		portableFurnace = new ItemFurnace("portableFurnace");
		portableInventory = new ItemInventory("portableInventory");
		portableCrafter = new ItemPortableCrafter("portableCrafter");
		portableGenerator = new ItemPortableGenerator("portableGenerator");
		portableRemoteInv = new ItemRemoteInv("portableRemoteInv");
		portableTeleporter = new ItemPortableTeleporter("portableTeleporter");
		portablePump = new ItemPortablePump("portablePump");
		translocator = new ItemTranslocator("translocator");
		portableTesla = new ItemPortableTesla("portableTesla");
		placement = new ItemPlacement("placement");
		synchronizer = new ItemMachineSynchronizer("synchronizer");
		(remBlockType = new DefaultItem("remBlockType")).setCreativeTab(tabIndAut);
		vertexSel = new ItemVertexSel("vertexSel");
		*/
	}

	static void createFluids() {
		String p = "automation:blocks/fluids/";
		L_steam = registerFluid(new DefaultFluid("steam", p+"steam").setDensity(0).setGaseous(true).setTemperature(523).setViscosity(10));
		L_waterG = registerFluid(new DefaultFluid("waterG", p+"waterG").setDensity(-1).setGaseous(true).setTemperature(373).setViscosity(10));
		L_biomass = registerFluid(new DefaultFluid("biomass", p+"biomass").setTemperature(310).setViscosity(1500));
		L_antimatter = registerFluid(new DefaultFluid("antimatter", p+"antimatter").setDensity(-1000).setGaseous(true).setTemperature(10000000).setViscosity(1));
		L_nitrogenG = registerFluid(new DefaultFluid("nitrogenG", p+"nitrogenG").setDensity(0).setGaseous(true).setTemperature(273).setViscosity(10));
		L_nitrogenL = registerFluid(new DefaultFluid("nitrogenL", p+"nitrogenL").setDensity(800).setTemperature(77));
		L_heliumG = registerFluid(new DefaultFluid("heliumG", p+"heliumG").setDensity(-1).setGaseous(true).setTemperature(273).setViscosity(5));
		L_heliumL = registerFluid(new DefaultFluid("heliumL", p+"heliumL").setDensity(144).setTemperature(4).setViscosity(1));
		L_hydrogenG = registerFluid(new DefaultFluid("hydrogenG", p+"hydrogenG").setDensity(-1).setGaseous(true).setTemperature(273).setViscosity(5));
		L_hydrogenL = registerFluid(new DefaultFluid("hydrogenL", p+"hydrogenL").setDensity(72).setTemperature(21).setViscosity(500));
		L_oxygenG = registerFluid(new DefaultFluid("oxygenG", p+"oxygenG").setDensity(0).setGaseous(true).setTemperature(273).setViscosity(10));
		L_oxygenL = registerFluid(new DefaultFluid("oxygenL", p+"oxygenL").setDensity(1160).setTemperature(90).setViscosity(800));
		L_water = FluidRegistry.WATER;
		L_lava = FluidRegistry.LAVA;
		/* TODO reimplement
		BlockSuperfluid.reactConversions.put(L_biomass, L_steam);
		BlockSuperfluid.reactConversions.put(L_nitrogenL, L_nitrogenG);
		BlockSuperfluid.reactConversions.put(L_heliumL, L_heliumG);
		BlockSuperfluid.reactConversions.put(L_hydrogenL, L_hydrogenG);
		BlockSuperfluid.reactConversions.put(L_oxygenL, L_oxygenG);
		BlockSuperfluid.effects.put(L_oxygenG, new PotionEffect[]{new PotionEffect(Potion.moveSpeed.id, 5), new PotionEffect(Potion.digSpeed.id, 5), new PotionEffect(Potion.damageBoost.id, 5)});
		BlockSuperfluid.effects.put(L_nitrogenG, new PotionEffect[]{new PotionEffect(Potion.moveSlowdown.id, 5), new PotionEffect(Potion.digSlowdown.id, 5), new PotionEffect(Potion.weakness.id, 5)});
		BlockSuperfluid.effects.put(L_heliumG, new PotionEffect[]{new PotionEffect(Potion.fireResistance.id, 5), new PotionEffect(Potion.moveSlowdown.id, 5), new PotionEffect(Potion.digSlowdown.id, 5)});
		BlockSuperfluid.effects.put(L_hydrogenG, new PotionEffect[]{new PotionEffect(Potion.confusion.id, 5), new PotionEffect(Potion.moveSlowdown.id, 5), new PotionEffect(Potion.digSlowdown.id, 5)});
		BlockSuperfluid.effects.put(L_antimatter, new PotionEffect[]{new PotionEffect(Potion.wither.id, 50), new PotionEffect(Potion.blindness.id, 50), new PotionEffect(Potion.hunger.id, 50)});
		*/
		if (L_hydrogenG.canBePlacedInWorld()) Blocks.FIRE.setFireInfo(L_hydrogenG.getBlock(), 500, 20);
	}

	private static Fluid registerFluid(Fluid fluid) {
		Fluid ret = FluidRegistry.getFluid(fluid.getName());
		if (ret == null) {
			FluidRegistry.registerFluid(fluid);
			fluid.setBlock(new BlockSuperfluid(fluid.getName(), (DefaultFluid)fluid));
			return fluid;
		} else return ret;
	}

}
