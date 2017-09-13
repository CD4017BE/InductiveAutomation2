package cd4017be.indaut;

import cd4017be.api.Capabilities.EmptyCallable;
import cd4017be.api.Capabilities.EmptyStorage;
import cd4017be.indaut.block.*;
import cd4017be.indaut.item.*;
import cd4017be.indaut.multiblock.HeatPipeComp;
import cd4017be.indaut.multiblock.IHeatReservoir;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.tileentity.*;
import cd4017be.lib.DefaultItemBlock;
import cd4017be.lib.block.BaseBlock;
import cd4017be.lib.block.BlockPipe;
import cd4017be.lib.block.OrientedBlock;
import cd4017be.lib.property.PropertyOrientation;
import cd4017be.lib.templates.TabMaterials;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Objects {

	public static TabMaterials tabIndAut;
	
	@CapabilityInject(IKineticInteraction.class)
	public static Capability<IKineticInteraction> KINETIC_CAP;
	@CapabilityInject(IHeatReservoir.class)
	public static Capability<IHeatReservoir> HEAT_CAP;
	@CapabilityInject(HeatPipeComp.class)
	public static Capability<HeatPipeComp> HEAT_PIPE_CAP;

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

	static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(IKineticInteraction.class, new EmptyStorage<IKineticInteraction>(), new EmptyCallable<IKineticInteraction>());
		CapabilityManager.INSTANCE.register(IHeatReservoir.class, new EmptyStorage<IHeatReservoir>(), new EmptyCallable<IHeatReservoir>());
		CapabilityManager.INSTANCE.register(HeatPipeComp.class, new EmptyStorage<HeatPipeComp>(), new EmptyCallable<HeatPipeComp>());
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
		
		tabIndAut.item = new ItemStack(shaft);
	}

	static void createItems() {
		(rotationSensor = new ItemRotationSensor("rotation_sensor")).setCreativeTab(tabIndAut);
		(thermometer = new ItemThermometer("thermometer")).setCreativeTab(tabIndAut);
	}

}
