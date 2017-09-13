package cd4017be.indaut.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import cd4017be.api.circuits.ItemBlockSensor;
import cd4017be.indaut.tileentity.Shaft;

public class ItemRotationSensor extends ItemBlockSensor {

	public ItemRotationSensor(String id) {
		super(id, 20F);
	}

	@Override
	protected float measure(ItemStack sensor, NBTTagCompound nbt, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof Shaft) {
			Shaft shaft = (Shaft)te;
			return (float)shaft.structure.v;
		}
		return 0F;
	}

}
