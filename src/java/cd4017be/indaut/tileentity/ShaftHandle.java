package cd4017be.indaut.tileentity;

import java.util.ArrayList;

import cd4017be.indaut.Objects;
import cd4017be.indaut.multiblock.IKineticInteraction;
import cd4017be.lib.util.Utils;
import net.minecraft.util.EnumFacing;

public class ShaftHandle extends Shaft {

	protected final IKineticInteraction[] interactions = new IKineticInteraction[4];

	@Override
	public void update() {
		if (checkNeighbors) checkInteractions();
		super.update();
	}

	@Override
	public void getInteractions(ArrayList<IKineticInteraction> list) {
		if (checkNeighbors) checkInteractions();
		for (IKineticInteraction kin : interactions)
			if (kin != null) list.add(kin);
	}

	private void checkInteractions() {
		int i = 0;
		for (EnumFacing s : EnumFacing.VALUES)
			if (s.getAxis() != structure.axis) {
				IKineticInteraction pre = interactions[i];
				IKineticInteraction kin = Utils.neighborCapability(this, s, Objects.KINETIC_CAP);
				if (pre == null && kin != null) structure.addInteraction(kin);
				else if (kin != pre) structure.interactionRemoved();
				interactions[i++] = kin;
			}
	}

	@Override
	public String getModel() {
		return "handle";
	}

}