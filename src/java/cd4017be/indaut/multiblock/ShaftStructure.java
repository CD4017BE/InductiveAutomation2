package cd4017be.indaut.multiblock;

import java.util.ArrayList;

import cd4017be.indaut.tileentity.Shaft;
import cd4017be.lib.util.CoordMap1D;
import cd4017be.lib.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ShaftStructure {

	public Shaft updater;
	public final Axis axis;
	public final CoordMap1D<Shaft> components;
	private final ArrayList<IKineticInteraction> interactions = new ArrayList<IKineticInteraction>();
	private boolean reloadInteractions = false, updateStructure = false;
	/**[kg] effective rotation mass */
	public double m = 0;
	/**[rot/s = m/s] rotation speed */
	public double v = 0;
	/**[rot = m] partial rotation */
	public double s = 0;

	public ShaftStructure(Shaft owner, NBTTagCompound nbt) {
		this(owner, Axis.values()[nbt.getByte("ax")]);
		s = nbt.getDouble("s");
		v = nbt.getDouble("v");
	}

	public ShaftStructure(Shaft owner, Axis axis) {
		this.components = new CoordMap1D<Shaft>();
		this.axis = axis;
		this.updater = owner;
		this.m = owner.getMass();
		components.set(Utils.coord(owner.getPos(), axis), owner);
		owner.getInteractions(interactions);
	}

	private ShaftStructure(ShaftStructure parent, int from, int to) {
		this.axis = parent.axis;
		this.components = new CoordMap1D<Shaft>(to - from);
		parent.components.copyInto(from, to, components);
		parent.components.clear(from, to);
		for (Shaft part : components) {
			part.structure = this;
			m += part.getMass();
			part.getInteractions(interactions);
		}
		parent.m -= m;
		v = parent.v;
		s = parent.s;
	}

	public void addShaft(Shaft part) {
		if (part.structure == this || part.structure == null || part.structure.axis != axis) return;
		if (part.structure.components.range() > components.range()) part.structure.merge(this);
		else merge(part.structure);
	}

	private void merge(ShaftStructure struc) {
		struc.components.copyInto(Integer.MIN_VALUE, Integer.MAX_VALUE, components);
		for (Shaft part : struc.components) part.structure = this;
		if (!(reloadInteractions |= struc.reloadInteractions))
			interactions.addAll(struc.interactions);
		v *= m;
		v += struc.v * struc.m;
		m += struc.m;
		v /= m;
	}

	public void removeShaft(Shaft part) {
		if (components.remove(Utils.coord(part.getPos(), axis)) != null) {
			m -= part.getMass();
			updateStructure = true;
		}
	}

	public void update(Shaft updater, boolean client) {
		if (this.updater == null) this.updater = updater;
		else if (this.updater != updater) return;

		if (updateStructure) {
			int min = components.getMin(), max = components.getMax();
			for (int i = min + 1; i < max; i++)
				if (components.get(i) == null) {
					new ShaftStructure(this, min, i - 1);
					i = min = components.getMin();
				}
			updateStructure = false;
			reloadInteractions = true;
		}
		if (reloadInteractions && !client) {
			interactions.clear();
			for (Shaft part : components) part.getInteractions(interactions);
			reloadInteractions = false;
		}
		
		double dt = 0.05F, ds = v * dt;
		if (client || interactions.isEmpty()) {
			s += ds;//just simulate constant rotation speed
			if (s > 1F) s -= Math.floor(s);
		} else {
			double F = 0;
			for (IKineticInteraction kin : interactions)
				F += kin.estimateTorque(ds); //get the estimated total force of all components on the shaft
			if (v == 0 && F <= 0) return; //can't slow down any further if already stopped
			double a = F / m; //convert to acceleration
			double v1 = (v + a * dt) * dt; //calculate estimated target velocity
			if (v1 < 0) {dt = -v / a; v1 = 0;} //if rotation would stop, only calculate till that point
			ds = 0.5F * a * dt * dt + v * dt; //now use the real distance moved
			double E = 0.5F * m * v * v; //get the kinetic Energy of the shaft and add the work of all components to it
			for (IKineticInteraction kin : interactions)
				E += kin.work(ds, v1);
			v = Math.sqrt(2F * E / m); //convert back to speed
			if (Double.isNaN(v)) v = 0;
			s += ds; //move the shaft forward
			if (s > 1F) s -= Math.floor(s);
			
			int min = components.getMin(), max = components.getMax();
			Shaft shaft = components.get((min + max) / 2);
			if (shaft != null) shaft.sendVelocityUpdate(max - min);
		}
	}

	public void interactionRemoved() {
		reloadInteractions = true;
	}

	public void addInteraction(IKineticInteraction kin) {
		interactions.add(kin);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setDouble("s", s);
		nbt.setDouble("v", v);
	}

	//################# client side only rendering stuff #################

	/**	used in combination with {@link TickHandler.tick} to check whether this has already been rendered */
	@SideOnly(Side.CLIENT)
	public int lastRendered = -1;

}
