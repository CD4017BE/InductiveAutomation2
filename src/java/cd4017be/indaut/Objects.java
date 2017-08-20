package cd4017be.indaut;

import cd4017be.indaut.block.*;
import cd4017be.indaut.multiblock.HeatPipeComp;
import cd4017be.indaut.multiblock.IHeatReservoir;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.tileentity.*;
import cd4017be.lib.DefaultItemBlock;
import cd4017be.lib.block.BaseBlock;
import cd4017be.lib.block.OrientedBlock;
import cd4017be.lib.templates.TabMaterials;
import cd4017be.lib.util.Orientation;
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
	public static BlockHeatPipe heatPipe;
	public static OrientedBlock heatSink;
	public static BaseBlock heatInsulation;
	public static OrientedBlock stirlingEngine;
	public static OrientedBlock solidHeater;
	
	static void createBlocks() {
		new DefaultItemBlock((shaft = new BlockShaft("shaft", Material.IRON, Shaft.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((shaftHandle = new BlockShaft("shaftHandle", Material.IRON, ShaftHandle.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((flyWheel = new BlockShaft("flyWheel", Material.IRON, FlyWheel.class)).setCreativeTab(tabIndAut));
		flyWheelPart = new BlockMultiblockPart("flyWheelPart", Material.IRON, MultiblockPart.class);
		new DefaultItemBlock((heatPipe = new BlockHeatPipe("heatPipe", Material.IRON, HeatPipe.class).setSize(0.5)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((heatSink = OrientedBlock.create("heatSink", Material.IRON, 2, null, Orientation.XY_12_ROT)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((heatInsulation = new BaseBlock("heatInsulation", Material.ROCK)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((stirlingEngine = OrientedBlock.create("stirlingEngine", Material.IRON, 2, StirlingEngine.class, Orientation.ALL_AXIS)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((solidHeater = OrientedBlock.create("solidHeater", Material.ROCK, 2, SolidHeater.class, Orientation.HOR_AXIS)));
		
		tabIndAut.item = new ItemStack(shaft);
	}

	static void createItems() {
	}

}
