package cd4017be.indaut;

import cd4017be.indaut.block.*;
import cd4017be.indaut.multiblock.HeatPipeComp;
import cd4017be.indaut.multiblock.IHeatReservoir;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.tileentity.*;
import cd4017be.lib.DefaultItemBlock;
import cd4017be.lib.block.BaseBlock;
import cd4017be.lib.block.BlockPipe;
import cd4017be.lib.block.OrientedBlock;
import cd4017be.lib.templates.TabMaterials;
import cd4017be.lib.util.Orientation;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Objects {

	public static TabMaterials tabIndAut;
	
	@CapabilityInject(IKineticInteraction.class)
	public static Capability<IKineticInteraction> KINETIC_CAP;
	@CapabilityInject(IHeatReservoir.class)
	public static Capability<IHeatReservoir> HEAT_CAP;
	@CapabilityInject(IHeatReservoir.class)
	public static Capability<HeatPipeComp> HEAT_PIPE_CAP;

	public static BlockShaft shaft;
	public static BlockShaft shaftHandle;
	public static BlockShaft flyWheel;
	public static BlockMultiblockPart flyWheelPart;
	public static BlockPipe heatPipe;
	public static OrientedBlock heatSink;
	public static BaseBlock heatInsulation;
	public static OrientedBlock stirlingEngine;
	public static OrientedBlock solidHeater;
	
	static void createBlocks() {
		new DefaultItemBlock((shaft = new BlockShaft("shaft", Material.IRON, SoundType.METAL, Shaft.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((shaftHandle = new BlockShaft("shaft_handle", Material.IRON, SoundType.METAL, ShaftHandle.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((flyWheel = new BlockShaft("fly_wheel", Material.IRON, SoundType.METAL, FlyWheel.class)).setCreativeTab(tabIndAut));
		flyWheelPart = new BlockMultiblockPart("fly_wheel_part", Material.IRON, SoundType.METAL, MultiblockPart.class);
		new DefaultItemBlock((heatPipe = BlockPipe.create("heat_pipe", Material.IRON, SoundType.METAL, HeatPipe.class, 1).setSize(0.5)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((heatSink = OrientedBlock.create("heat_sink", Material.IRON, SoundType.METAL, 2, null, Orientation.XY_12_ROT)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((heatInsulation = new BaseBlock("heat_insulation", Material.ROCK)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((stirlingEngine = OrientedBlock.create("stirling_engine", Material.IRON, SoundType.METAL, 2, StirlingEngine.class, Orientation.ALL_AXIS)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((solidHeater = OrientedBlock.create("solid_heater", Material.ROCK, SoundType.METAL, 2, SolidHeater.class, Orientation.HOR_AXIS)));
		
		tabIndAut.item = new ItemStack(shaft);
	}

	static void createItems() {
	}

}
