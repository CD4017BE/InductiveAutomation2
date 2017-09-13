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
		boolean check = checkNeighbors;
		super.update();
		if (check) checkInteractions();
	}

	@Override
	public void getInteractions(ArrayList<IKineticInteraction> list) {
		if (checkNeighbors && world != null) checkInteractions();
		for (IKineticInteraction kin : interactions)
			if (kin != null) {
				list.add(kin);
				kin.setShaft(structure);
			}
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

	@Override
	protected void clearData() {
		super.clearData();
		for (int i = 0; i < interactions.length; i++)
			if (interactions[i] != null) {
				interactions[i].setShaft(null);
				interactions[i] = null;
			}
	}

}
