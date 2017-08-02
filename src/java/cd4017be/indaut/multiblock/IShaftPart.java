package cd4017be.indaut.multiblock;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;

public interface IShaftPart {

	ShaftStructure getStructure();
	void setStructure(ShaftStructure structure);
	double getMass();
	void getInteractions(ArrayList<IKineticInteraction> list);
	BlockPos pos();
	void onShaftStateChanged();

}
