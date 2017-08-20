package cd4017be.indaut.tileentity;

import java.io.IOException;

import cd4017be.indaut.Objects;
import cd4017be.indaut.multiblock.HeatReservoir;
import cd4017be.indaut.multiblock.IHeatReservoir;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.indaut.multiblock.SimpleHeatReservoir;
import cd4017be.indaut.registry.Substances;
import cd4017be.indaut.registry.Substances.Environment;
import cd4017be.lib.BlockGuiHandler.ServerPacketReceiver;
import cd4017be.lib.Gui.inWorld.ClickType;
import cd4017be.lib.Gui.inWorld.InWorldUITile;
import cd4017be.lib.block.AdvancedBlock.INeighborAwareTile;
import cd4017be.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class StirlingEngine extends InWorldUITile implements IKineticInteraction, INeighborAwareTile, ITickable, ServerPacketReceiver {

	public static float Rwork = 10F, Rx = 0.05F, C = 1000F, loss = 20F, maxMult = 16F;
	private final SimpleHeatReservoir cold, hot;
	private IHeatReservoir conC, conH;
	private Environment env;
	public float mult;
	private boolean updateCon = true;

	public StirlingEngine() {
		cold = new SimpleHeatReservoir(this, C, Rx);
		hot = new SimpleHeatReservoir(this, C, Rx);
	}

	public StirlingEngine(IBlockState state) {
		super(state);
		cold = new SimpleHeatReservoir(this, C, Rx);
		hot = new SimpleHeatReservoir(this, C, Rx);
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		if (updateCon || conC.invalid() || conH.invalid()) {
			EnumFacing side = getOrientation().front;
			conC = Utils.neighborCapability(this, side, Objects.HEAT_CAP);
			if (conC == null) conC = getEnvHeat(pos.offset(side));
			conH = Utils.neighborCapability(this, side.getOpposite(), Objects.HEAT_CAP);
			if (conH == null) conH = getEnvHeat(pos.offset(side.getOpposite()));
		}
		HeatReservoir.exchangeHeat(cold, conC);
		HeatReservoir.exchangeHeat(hot, conH);
		sendPacket(false);
	}

	private IHeatReservoir getEnvHeat(BlockPos pos) {
		return world.isBlockLoaded(pos) ? new IHeatReservoir.ConstantHeat(env.getTemp(world, pos), env.getCond(world.getBlockState(pos), 0)) : IHeatReservoir.NULL;
	}

	@Override
	public double estimateTorque(double ds) {
		//      approximated heat flow per shaft rotation  *   Carnot efficiency
		return (hot.T - cold.T * mult) / (Rx * ds + Rwork) * (1F - cold.T / hot.T);
	}

	@Override
	public double work(double ds, double v) {
		//    heat flow =  temperature gradient   *   heat conductivity
		final float Qin = (hot.T - cold.T * mult) / (Rx + Rwork / (float)ds);
		hot.addHeat(-Qin);
		final float Qout = Qin * cold.T / hot.T + (float)(ds * ds) * loss;
		cold.addHeat(Qout);
		return Qin - Qout;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing s) {
		EnumFacing o = getOrientation().front;
		if (cap == Objects.KINETIC_CAP) return o.getAxis() != s.getAxis();
		if (cap == Objects.HEAT_CAP) return o.getAxis() == s.getAxis();
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing s) {
		EnumFacing o = getOrientation().front;
		if (cap == Objects.KINETIC_CAP) return o.getAxis() != s.getAxis() ? (T) this : null;
		if (cap == Objects.HEAT_CAP) return o == s ? (T) cold : o == s.getOpposite() ? (T) hot : null;
		return null;
	}

	@Override
	public void neighborBlockChange(Block b, BlockPos src) {
		updateCon = true;
	}

	@Override
	public void neighborTileChange(BlockPos src) {
		updateCon = true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		cold.T = nbt.getFloat("Tc");
		hot.T = nbt.getFloat("Th");
		mult = nbt.getFloat("mult");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("Tc", cold.T);
		nbt.setFloat("Th", hot.T);
		nbt.setFloat("mult", mult);
		return super.writeToNBT(nbt);
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		env = Substances.getEnvFor(world);
		if (Float.isNaN(cold.T) || Float.isNaN(hot.T)) {
			hot.T = cold.T = env.getTemp(world, pos);
		}
	}

	@Override
	protected void getPacketData(PacketBuffer buff) {
		buff.writeFloat(mult);
	}

	@Override
	public void onPacketFromServer(PacketBuffer data) throws IOException {
		mult = data.readFloat();
	}

	@Override
	protected AxisAlignedBB[] getBoxes() {
		return new AxisAlignedBB[] {new AxisAlignedBB(0, 0, 0, 1, 1, .525)};
	}

	@Override
	protected void onInteract(EntityPlayer player, ItemStack item, EnumHand hand, RayTraceResult hit, ClickType type) {
		if (hit.subHit == 0 && item.isEmpty()) {
			float l = (float)hit.hitVec.zCoord;
			mult = (1F - l) / l;
			if (mult < 1F) mult = 1F;
			else if (mult > maxMult) mult = maxMult;
		}
		sendPacket(true);
	}

}
