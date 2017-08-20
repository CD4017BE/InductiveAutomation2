package cd4017be.indaut.tileentity;

import java.io.IOException;

import cd4017be.indaut.multiblock.HeatReservoir;
import cd4017be.lib.BlockGuiHandler.ServerPacketReceiver;
import cd4017be.lib.Gui.inWorld.ClickType;
import cd4017be.lib.Gui.inWorld.InWorldUITile;
import cd4017be.lib.block.AdvancedBlock.INeighborAwareTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SolidHeater extends InWorldUITile implements ITickable, INeighborAwareTile, IItemHandler, ServerPacketReceiver {

	public static int maxFuel = 25600;
	public static float C, Ri, Rc, fuelValue = 10000F, burnTemp = 2500F, MaxBurnRate = 10000F; // eff = 1 - dT / burnTemp
	private static final AxisAlignedBB[] boxes = {
			new AxisAlignedBB(.125, .375, 0, .875, .625, .75), //fuel hatch
			new AxisAlignedBB(.125, .125, 0, .625, .25, .5) //air intake
		};

	private HeatReservoir heat;
	public int fuel;
	public float burnRate;

	public SolidHeater() {
		heat = new HeatReservoir(C, Ri);
		heat.Rc[1] = Rc;
	}

	public SolidHeater(IBlockState state) {
		super(state);
		heat = new HeatReservoir(C, Ri);
		heat.Rc[1] = Rc;
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		heat.update(this);
		if (burnRate > 0) {
			float dQ = Math.min((float)fuel * fuelValue, burnRate);
			float C = burnRate / burnTemp;
			heat.T = (heat.T * heat.C + heat.envTemp * C + dQ) / (heat.C + C);
		}
		sendPacket(false);
	}

	@Override
	protected void onInteract(EntityPlayer player, ItemStack item, EnumHand hand, RayTraceResult hit, ClickType type) {
		if (hit == null) return;
		switch(hit.subHit) {
		case 0:
			player.setHeldItem(hand, insertItem(0, item, false));
			break;
		case 1:
			AxisAlignedBB box = boxes[1];
			burnRate = (float)MathHelper.clamp((box.maxX - hit.hitVec.xCoord) / (box.maxX - box.minX) * 1.1 + 0.05, 0, 1) * MaxBurnRate;
			break;
		}
		sendPacket(true);
	}

	@Override
	protected AxisAlignedBB[] getBoxes() {
		return boxes;
	}

	@Override
	public void neighborBlockChange(Block b, BlockPos src) {
		heat.check = true;
	}

	@Override
	public void neighborTileChange(BlockPos src) {
		heat.check = true;
	}

	@Override
	protected void getPacketData(PacketBuffer data) {
		data.writeFloat(heat.T);
		data.writeInt(fuel);
		data.writeFloat(burnRate);
	}

	@Override
	public void onPacketFromServer(PacketBuffer data) throws IOException {
		heat.T = data.readFloat();
		fuel = data.readInt();
		burnRate = data.readFloat();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		heat.load(nbt, "heat");
		fuel = nbt.getInteger("fuel");
		burnRate = nbt.getFloat("burn");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		heat.save(nbt, "heat");
		nbt.setInteger("fuel", fuel);
		nbt.setFloat("burn", burnRate);
		return super.writeToNBT(nbt);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return null;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (fuel > maxFuel / 2) return stack;
		int n = TileEntityFurnace.getItemBurnTime(stack);
		if (n == 0) return stack;
		int m = Math.min(stack.getCount(), Math.max(1, (maxFuel - fuel) / n));
		if (!simulate) fuel += (float)m * n;
		return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - m);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return null;
	}

	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}

}
