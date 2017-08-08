package cd4017be.indaut.multiblock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cd4017be.indaut.Objects;
import cd4017be.indaut.registry.Substances;
import cd4017be.indaut.registry.Substances.Environment;
import cd4017be.lib.templates.SharedNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class HeatPipeStructure extends SharedNetwork<HeatPipeComp, HeatPipeStructure> {

	public float T, C;
	public float heatCond, refTemp;
	public HashMap<Long, HeatExchCon> heatExch = new HashMap<Long, HeatExchCon>();

	static class HeatExchCon {
		HeatExchCon(HeatPipeComp pipe, IHeatReservoir other) {
			this.pipe = pipe;
			this.other = other;
		}
		final HeatPipeComp pipe;
		final IHeatReservoir other;
	}

	public HeatPipeStructure(HeatPipeComp core) {
		super(core);
		C = core.C;
		heatCond = core.heatCond;
		refTemp = core.refTemp;
	}

	protected HeatPipeStructure(HashMap<Long, HeatPipeComp> comps) {
		super(comps);
	}

	@Override
	public HeatPipeStructure onSplit(HashMap<Long, HeatPipeComp> comps) {
		HeatPipeStructure struc = new HeatPipeStructure(comps);
		for (HeatPipeComp comp : comps.values()) {
			struc.C += comp.C;
			struc.heatCond += comp.heatCond;
			struc.refTemp += comp.refTemp;
		}
		struc.T = T;
		C -= struc.C;
		heatCond -= struc.heatCond;
		refTemp -= struc.refTemp;
		for (Iterator<Entry<Long, HeatExchCon>> it = heatExch.entrySet().iterator(); it.hasNext();) {
			Entry<Long, HeatExchCon> e = it.next();
			if (e.getValue().pipe.network == struc) {
				struc.heatExch.put(e.getKey(), e.getValue());
				it.remove();
			}
		}
		return struc;
	}

	@Override
	public void onMerged(HeatPipeStructure network) {
		super.onMerged(network);
		float E = T * C + network.T * network.C;
		T = E / (C += network.C);
		heatCond += network.heatCond;
		refTemp += network.refTemp;
		heatExch.putAll(network.heatExch);
	}

	@Override
	public void remove(HeatPipeComp comp) {
		if (comp.network == this) {
			C -= comp.C;
			heatCond -= comp.heatCond;
			refTemp -= comp.refTemp;
		}
		super.remove(comp);
	}

	@Override
	public void updateCompCon(HeatPipeComp comp) {
		for (byte i : sides()) {
			HeatPipeComp obj;
			if (comp.canConnect(i) && (obj = comp.getNeighbor(i)) != null) {
				add(obj);
			}
		}
		comp.updateCon = false;
		ICapabilityProvider te;
		TileEntity tile = (TileEntity)comp.tile;
		World world = tile.getWorld();
		BlockPos pos = tile.getPos();
		Environment env = Substances.getEnvFor(world);
		float dC = -comp.heatCond;
		for (EnumFacing s : EnumFacing.VALUES) {
			int i = s.ordinal();
			IHeatReservoir hr;
			if (comp.tile.getCapability(Objects.HEAT_CAP, s) != this) continue;
			if ((te = comp.tile.getTileOnSide(s)) == null || (hr = te.getCapability(Objects.HEAT_CAP, s.getOpposite())) == null) {
				dC += env.getCond(world.getBlockState(pos), comp.R());
			} else if ((i & 1) == 0 && !(hr instanceof HeatPipeStructure)) {
				heatExch.put(SharedNetwork.SidedPosUID(comp.getUID(), i), new HeatExchCon(comp, hr));
			}
		}
		comp.heatCond += dC;
		this.heatCond += dC;
		float dT = env.getTemp(world, pos) * comp.heatCond - comp.refTemp;
		comp.refTemp += dT;
		this.refTemp += dT;
	}

	@Override
	protected void updatePhysics() {
		if (heatCond > 0) {
			float Tref = refTemp / heatCond;
			if (heatCond > C) T = Tref;
			else T -= (T - Tref) * heatCond / C;
		}
		if (!heatExch.isEmpty())
			for (Iterator<HeatExchCon> it = heatExch.values().iterator(); it.hasNext();) {
				HeatExchCon hc = it.next();
				if (hc.pipe.network != this || hc.other.invalid()) {
					it.remove();
					continue;
				}
				HeatReservoir.exchangeHeat(hc.pipe, hc.other);
			}
	}

}
