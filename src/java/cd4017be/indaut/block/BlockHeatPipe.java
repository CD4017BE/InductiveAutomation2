package cd4017be.indaut.block;

import cd4017be.lib.block.MultipartBlock;
import cd4017be.lib.property.PropertyBoolean;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockHeatPipe extends MultipartBlock {

	public BlockHeatPipe(String id, Material m, Class<? extends TileEntity> tile) {
		super(id, m, 3, tile);
	}

	@Override
	protected IUnlistedProperty<?>[] createModules() {
		return new IUnlistedProperty<?>[] {
			new PropertyBoolean("conB"),
			new PropertyBoolean("conT"),
			new PropertyBoolean("conN"),
			new PropertyBoolean("conS"),
			new PropertyBoolean("conW"),
			new PropertyBoolean("conE")
		};
	}

	@Override
	protected PropertyInteger createBaseState() {
		return null;
	}

	public BlockHeatPipe setSize(double size) {
		size /= 2.0;
		double min = 0.5 - size, max = 0.5 + size;
		boundingBox = new AxisAlignedBB[] {
			new AxisAlignedBB(min, min, min, max, max, max),
			new AxisAlignedBB(min, 0.0, min, max, min, max),
			new AxisAlignedBB(min, max, min, max, 1.0, max),
			new AxisAlignedBB(min, min, 0.0, max, max, min),
			new AxisAlignedBB(min, min, max, max, max, 1.0),
			new AxisAlignedBB(0.0, min, min, min, max, max),
			new AxisAlignedBB(max, min, min, 1.0, max, max),
		};
		return this;
	}

}
