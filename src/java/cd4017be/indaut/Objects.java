package cd4017be.indaut;

import cd4017be.indaut.block.BlockHeatPipe;
import cd4017be.indaut.block.BlockMultiblockPart;
import cd4017be.indaut.block.BlockShaft;
import cd4017be.indaut.multiblock.HeatPipeComp;
import cd4017be.indaut.multiblock.IHeatReservoir;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.tileentity.FlyWheel;
import cd4017be.indaut.tileentity.HeatPipe;
import cd4017be.indaut.tileentity.MultiblockPart;
import cd4017be.indaut.tileentity.Shaft;
import cd4017be.indaut.tileentity.ShaftHandle;
import cd4017be.lib.DefaultItemBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Objects {

	public static CreativeTabs tabIndAut;
	
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

	static void createBlocks() {
		new DefaultItemBlock((shaft = new BlockShaft("shaft", Material.IRON, Shaft.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((shaftHandle = new BlockShaft("shaftHandle", Material.IRON, ShaftHandle.class)).setBlockBounds(new AxisAlignedBB(.25, .25, 0, .75, .75, 1)).setCreativeTab(tabIndAut));
		new DefaultItemBlock((flyWheel = new BlockShaft("flyWheel", Material.IRON, FlyWheel.class)).setCreativeTab(tabIndAut));
		flyWheelPart = new BlockMultiblockPart("flyWheelPart", Material.IRON, MultiblockPart.class);
		new DefaultItemBlock((heatPipe = new BlockHeatPipe("heatPipe", Material.IRON, HeatPipe.class).setSize(0.5)).setCreativeTab(tabIndAut));
	}

	static void createItems() {
	}

}
