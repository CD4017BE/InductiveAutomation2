package cd4017be.indaut.tileentity;

import java.io.IOException;

import cd4017be.indaut.Objects;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SolidHeater extends InWorldUITile implements ITickable, INeighborAwareTile, IItemHandler, ServerPacketReceiver {

	public static float maxFuel = 256000000F;
	public static float C = 10000, Ri = 100, Rc = 0.05F, fuelValue = 10000F, burnTemp = 2500F, MaxBurnRate = 10000F; // eff = 1 - dT / burnTemp
	private static final UIElement[] boxes = {
			new UIElement(0, 2, 6, 0, 14, 10, 12, 0x04), //fuel hatch
			new UIElement(1, 6, 2, 0, 14, 4, 8, 0x04), //air intake
			new UIElement(2, 2, 12, 0, 4, 14, 2, 0x04) //temp display
		};

	public HeatReservoir heat;
	public float fuel;
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
			float dQ = Math.min(fuel, burnRate);
			if (dQ > 0) {
				fuel -= dQ;
				float C = burnRate / burnTemp;
				heat.T = (heat.T * heat.C + heat.envTemp * C + dQ) / (heat.C + C);
			}
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
			if(!item.isEmpty()) return;
			UIElement box = boxes[1];
			burnRate = (float)MathHelper.clamp(box.interpolate(hit.hitVec, EnumFacing.WEST) * 1.1 - 0.05, 0, 1) * MaxBurnRate;
			break;
		case 2: return;
		}
		sendPacket(true);
	}

	@Override
	protected UIElement[] getBoxes() {
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
		data.writeFloat(fuel);
		data.writeFloat(burnRate);
	}

	@Override
	public void onPacketFromServer(PacketBuffer data) throws IOException {
		heat.T = data.readFloat();
		fuel = data.readFloat();
		burnRate = data.readFloat();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		heat.load(nbt, "heat");
		fuel = nbt.getFloat("fuel");
		burnRate = nbt.getFloat("burn");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		heat.save(nbt, "heat");
		nbt.setFloat("fuel", fuel);
		nbt.setFloat("burn", burnRate);
		return super.writeToNBT(nbt);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
		return cap == Objects.HEAT_CAP && facing == EnumFacing.UP
			|| cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
		if (cap == Objects.HEAT_CAP) return facing == EnumFacing.UP ? (T)heat.getCapability(this, facing) : null;
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T)this;
		return null;
	}

	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);
		heat.initialize(this);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (fuel > maxFuel / 2) return stack;
		float n = (float)TileEntityFurnace.getItemBurnTime(stack) * fuelValue;
		if (n <= 0) return stack;
		int m = Math.min(stack.getCount(), Math.max(1, (int)((maxFuel - fuel) / n)));
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
