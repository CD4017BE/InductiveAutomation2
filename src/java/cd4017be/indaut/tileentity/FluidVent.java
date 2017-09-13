package cd4017be.indaut.tileentity;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cd4017be.indaut.Config;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.DataContainer.IGuiData;
import cd4017be.lib.Gui.SlotTank;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.Gui.TileContainer.TankSlot;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.Inventory;
import cd4017be.lib.templates.TankContainer;
import cd4017be.lib.util.CachedChunkProtection;
import cd4017be.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;

/**
 *
 * @author CD4017BE
 */
public class FluidVent extends AutomatedTile implements IGuiData
{
	private static final GameProfile defaultUser = new GameProfile(new UUID(0, 0), "#FluidVent");
	private GameProfile lastUser = defaultUser;
	private int[] blocks = new int[0];
	private int dist = -1;
	private boolean flowDir;
	private static final EnumFacing[] SearchArray = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST};
	private final EnumFacing[] lastDir = new EnumFacing[2];
	private Block blockId;
	public boolean blockNotify;
	private CachedChunkProtection prot;
	public int mode, netI1;
	
	public FluidVent()
	{
		inventory = new Inventory(1, 0, null);
		tanks = new TankContainer(1, 1).tank(0, Config.tankCap[1], Utils.IN, 0, -1);
	}

	@Override
	public void update() 
	{
		super.update();
		if (world.isRemote) return;
		if (tanks.getAmount(0) < 1000) return;
		Fluid fluid = tanks.fluids[0].getFluid();
		if (!fluid.canBePlacedInWorld()) return;
		flowDir = fluid.getDensity() <= 0;
		blockId = fluid.getBlock();
		if (blocks.length == 0) return;
		EnumFacing dir;
		if (dist < 0) {
			dir = EnumFacing.VALUES[this.getOrientation()];
			if (this.canFill(pos.offset(dir))) {
				dist = 0;
				blocks[dist] = (dir.getFrontOffsetX() & 0xff) | (dir.getFrontOffsetY() & 0xff) << 8 | (dir.getFrontOffsetZ() & 0xff) << 16;
				lastDir[0] = lastDir[1] = null;
			} else return;
		}
		int target = blocks[dist];
		netI1 = target;
		byte dx = (byte)target, dy = (byte)(target >> 8), dz = (byte)(target >> 16);
		BlockPos np = pos.add(dx, dy, dz);
		if (prot == null || !prot.equalPos(np)) prot = CachedChunkProtection.get(lastUser, world, np);
		if (!prot.allow) return;
		if (dist >= blocks.length - 1) {
			this.moveBack(np);
			return;
		}
		dir = this.findNextDir(np);
		if (dir == null) this.moveBack(np);
		else {
			if (dir == EnumFacing.UP || dir == EnumFacing.DOWN) lastDir[0] = lastDir[1] = null;
			else if (dir != lastDir[0]) {
				lastDir[1] = lastDir[0];
				lastDir[0] = dir;
			}
			blocks[dist] = (blocks[dist] & 0x00ffffff) | (dir.ordinal() << 24);
			blocks[++dist] = (dx + dir.getFrontOffsetX() & 0xff) | (dy + dir.getFrontOffsetY() & 0xff) << 8 | (dz + dir.getFrontOffsetZ() & 0xff) << 16;
		}
	}
	
	private EnumFacing findNextDir(BlockPos pos)
	{
		if (this.isValidPos(pos.up(flowDir ? 1 : -1))) return flowDir ? EnumFacing.UP : EnumFacing.DOWN;
		else if (lastDir[0] != null && this.isValidPos(pos.add(lastDir[0].getFrontOffsetX(), 0, lastDir[0].getFrontOffsetZ()))) return lastDir[0];
		else if (lastDir[0] == null || lastDir[1] == null) {
			for (EnumFacing dir : SearchArray) {
				if (this.isValidPos(pos.add(dir.getFrontOffsetX(), 0, dir.getFrontOffsetZ()))) return dir;
			}
			return null;
		} else if (this.isValidPos(pos.add(lastDir[1].getFrontOffsetX(), 0, lastDir[1].getFrontOffsetZ()))) return lastDir[1];
		else if (this.isValidPos(pos.add(-lastDir[1].getFrontOffsetX(), 0, -lastDir[1].getFrontOffsetZ()))) {
			BlockPos pos1 = pos.add(-lastDir[1].getFrontOffsetX(), 0, -lastDir[1].getFrontOffsetZ()); int d1 = dist - 1;
			while (--d1 > 0) {
				pos1.add(-lastDir[0].getFrontOffsetX(), 0, -lastDir[0].getFrontOffsetZ());
				if ((byte)blocks[d1] + this.pos.getX() == pos1.getX() && (byte)(blocks[d1] >> 16) + this.pos.getZ() == pos1.getZ()) return null;
				else if (!this.isValidPos(pos1)) return lastDir[1].getOpposite();
			}
			return null;
		} else return null;
	}
	
	private boolean isValidPos(BlockPos pos)
	{
		BlockPos pos1 = pos.subtract(this.pos);
		int l = blocks.length / 3;
		if (pos.getY() < 0 || pos.getY() >= 256 || Math.abs(pos1.getY()) > l || Math.abs(pos1.getX()) > l || Math.abs(pos1.getZ()) > l) return false;
		if (!canFill(pos)) return false;
		int p = (pos1.getX() & 0xff) | (pos1.getY() & 0xff) << 8 | (pos1.getZ() & 0xff) << 16;
		for (int i = dist - 1; i >= 0; i -= 2)
			if ((blocks[i] & 0xffffff) == p) return false;
		return true;
	}
	
	private boolean canFill(BlockPos pos)
	{
		if (world.isAirBlock(pos)) return true;
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == blockId) return state != state.getBlock().getDefaultState();
		Material material = state.getMaterial();
		return material.isReplaceable() && !material.isLiquid();
	}
	
	private void moveBack(BlockPos pos)
	{
		if (this.canFill(pos)) {
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (block != null) block.dropBlockAsItem(world, pos, state, 0);
			if (world.setBlockState(pos, blockId.getDefaultState(), this.blockNotify ? 3 : 2)) tanks.drain(0, 1000, true);
		}
		dist--;
		if (dist <= 0) {
			lastDir[0] = null;
			return;
		}
		byte d0 = (byte)(blocks[dist - 1] >> 24), d1;
		if (d0 <= 1) lastDir[0] = null;
		else if (EnumFacing.VALUES[d0] != lastDir[0]) {
			lastDir[0] = EnumFacing.VALUES[d0];
			for (int i = dist - 2; i >= 0; i--) {
				d1 = (byte)(blocks[i] >> 24);
				if (d1 <= 1) break;
				else if (d1 != d0) {
					lastDir[1] = EnumFacing.VALUES[d1];
					return;
				}
			}
			lastDir[1] = null;
		}
	}

	@Override
	protected void customPlayerCommand(byte cmd, PacketBuffer dis, EntityPlayerMP player) throws IOException 
	{
		lastUser = player.getGameProfile();
		prot = null;
		if (cmd == 0) {
			blockNotify = !blockNotify;
			mode = (mode & 0xff) | (blockNotify ? 0x100 : 0);
		} else if (cmd == 1) {
			byte l = dis.readByte();
			if (l < 0) l = 0;
			else if (l > 127) l = 127;
			blocks = new int[l * 3];
			dist = -1;
			mode = (mode & 0xf00) | (l & 0xff);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setInteger("mode", mode);
		nbt.setString("lastUser", lastUser.getName());
		nbt.setLong("lastUserID0", lastUser.getId().getMostSignificantBits());
		nbt.setLong("lastUserID1", lastUser.getId().getLeastSignificantBits());
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		try {lastUser = new GameProfile(new UUID(nbt.getLong("lastUserID0"), nbt.getLong("lastUserID1")), nbt.getString("lastUser"));
		} catch (Exception e) {lastUser = defaultUser;}
		prot = null;
		mode = nbt.getInteger("mode");
		blocks = new int[(mode & 0x7f) * 3];
		blockNotify = (mode & 0x100) != 0;
		dist = -1;
	}

	@Override
	public void initContainer(DataContainer cont) {
		TileContainer container = (TileContainer)cont;
		container.addItemSlot(new SlotTank(inventory, 0, 202, 34));
		
		container.addPlayerInventory(8, 16);
		
		container.addTankSlot(new TankSlot(tanks, 0, 184, 16, (byte)0x23));
	}

	@Override
	public int[] getSyncVariables() {
		return new int[]{mode, netI1};
	}

	@Override
	public void setSyncVariable(int i, int v) {
		switch(i) {
		case 0: mode = v; break;
		case 1: netI1 = v; break;
		}
	}

}
