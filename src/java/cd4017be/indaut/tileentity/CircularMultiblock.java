package cd4017be.indaut.tileentity;

import java.util.HashMap;
import cd4017be.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class CircularMultiblock extends Shaft {

	private final HashMap<BlockPos, MultiblockPart> parts = new HashMap<BlockPos, MultiblockPart>();
	protected boolean updateRings = true;
	protected double M;
	protected float R = 1;
	/**inner most invalid square radius = point to check from */
	private int firstInvalid;
	/**outer most valid square radius = current size of the structure*/
	private int lastValid;

	@Override
	public void update() {
		super.update();
		if (updateRings) {
			int start = 1;
			while(start * start * 2 < firstInvalid) start++;
			firstInvalid = Integer.MAX_VALUE;
			for (int r = start; r * r < firstInvalid; r++)
				Utils.forRing(pos, structure.axis, r, this::check);
			int end = Math.max(lastValid + 1, firstInvalid);
			for (int r = start; r * r < end; r++)
				Utils.forRing(pos, structure.axis, r, this::fix);
			lastValid = firstInvalid - 1;
			R = (float)Math.sqrt(firstInvalid);
		}
	}

	private void check(BlockPos pos, int sqr) {
		if (sqr >= firstInvalid || parts.containsKey(pos)) return;
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == multiblockPart()) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof MultiblockPart) {
				MultiblockPart part = (MultiblockPart)te;
				if (part.link == null || part.link == this) return;
			}
		} else if (valid(state, pos)) return;
		firstInvalid = sqr;
	}

	private void fix(BlockPos pos, int sqr) {
		if (sqr > lastValid ^ sqr < firstInvalid) 
			if (sqr > lastValid) {
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				MultiblockPart part;
				if (block == multiblockPart()) {
					part = (MultiblockPart)world.getTileEntity(pos);
				} else {
					world.setBlockState(pos, multiblockPart().getDefaultState());
					part = (MultiblockPart)world.getTileEntity(pos);
					part.storedBlock = state;
					create(part);
				}
				part.link(this);
				parts.put(pos, part);
			} else {
				MultiblockPart part = parts.remove(pos);
				if (part != null) part.unlink();
			}
	}

	protected abstract Block multiblockPart();
	protected abstract void create(MultiblockPart part);
	protected abstract boolean valid(IBlockState state, BlockPos pos);

	public void mergeMomentum(double m, double v) {
		double p = v * m + structure.v * structure.m;
		structure.v = p / (structure.m += m);
		M += m;
	}

	public void remove(MultiblockPart part) {
		part.v = structure.v * part.r;
		if (tileEntityInvalid) return;
		double m = part.m * part.r * part.r;
		M -= m;
		structure.m -= m;
		BlockPos pos = part.getPos();
		parts.remove(pos);
		int d = (int)pos.distanceSq(pos);
		if (d < firstInvalid) {
			firstInvalid = d;
			updateRings = true;
		}
	}

	@Override
	public double getMass() {
		return M + BASE_MASS;
	}

	@Override
	public void neighborBlockChange(Block b, BlockPos src) {
		if (!updateRings && Utils.coord(src, structure.axis) == Utils.coord(pos, structure.axis) && src.distanceSq(pos) > lastValid) {
			IBlockState state = world.getBlockState(src);
			updateRings = state.getBlock() == multiblockPart() || valid(state, src);
		}
	}

	@Override
	public void neighborTileChange(BlockPos src) {
		this.neighborBlockChange(null, src);
		super.neighborTileChange(src);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		for (MultiblockPart part : parts.values()) part.unlink();
		parts.clear();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		for (MultiblockPart part : parts.values()) part.unlink();
		parts.clear();
	}

}
