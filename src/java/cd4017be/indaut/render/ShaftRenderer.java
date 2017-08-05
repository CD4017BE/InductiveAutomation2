package cd4017be.indaut.render;

import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import cd4017be.indaut.multiblock.ShaftStructure;
import cd4017be.indaut.tileentity.Shaft;
import cd4017be.lib.render.IModeledTESR;
import cd4017be.lib.render.Util;
import cd4017be.lib.render.model.IntArrayModel;
import cd4017be.lib.render.model.ModelContext;
import cd4017be.lib.script.Module;
import cd4017be.lib.util.Orientation;
import cd4017be.lib.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class ShaftRenderer extends TileEntitySpecialRenderer<Shaft> implements IModeledTESR {

	protected static final ResourceLocation texture = new ResourceLocation("indaut:textures/tesr/shafts.png");
	protected final HashMap<String, IntArrayModel> modelCache = new HashMap<String, IntArrayModel>();
	protected ModelContext modelGenerator;
	protected Module baseScript;
	
	@Override
	public void renderTileEntityAt(Shaft te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.structure.lastRendered < Util.RenderFrame) {
			BlockPos pos = te.getPos();
			renderStructure(te.getWorld(), te.structure, x - pos.getX(), y - pos.getY(), z - pos.getZ(), partialTicks * 0.05F);
		}
	}

	protected void renderStructure(World world, ShaftStructure struc, double x, double y, double z, float t) {
		struc.lastRendered = Util.RenderFrame;
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableBlend();
		GlStateManager.enableCull();
		if (Minecraft.isAmbientOcclusionEnabled()) GlStateManager.shadeModel(7425);
		else GlStateManager.shadeModel(7424);
		GlStateManager.pushMatrix();
		BlockPos pos = struc.components.getFirst().getPos();
		Util.moveAndOrientToBlock(x + pos.getX(), y + pos.getY(), z + pos.getZ(), struc.axis == Axis.Z ? Orientation.N : struc.axis == Axis.Y ? Orientation.Bn : Orientation.W);
		GlStateManager.rotate(360F * ((float)struc.s + (float)struc.v * t), 0, 0, 1);
		
		bindTexture(texture);
		VertexBuffer render = Tessellator.getInstance().getBuffer();
		render.begin(GL11.GL_QUADS, IntArrayModel.FORMAT);
		int z0 = struc.components.getMin();
		for (Shaft part : struc.components) {
			IntArrayModel model = getModel(part.getModel());
			pos = part.getPos();
			model.setOffset(Utils.coord(pos, struc.axis) - z0, Axis.Z);
			model.setBrightness(world.getCombinedLight(pos, 0));
			render.addVertexData(model.vertexData);
		}
		Tessellator.getInstance().draw();
		
		GlStateManager.popMatrix();
	}

	protected IntArrayModel getModel(String name) {
		IntArrayModel model = modelCache.get(name);
		if (model != null) return model;
		try {
			modelGenerator.run(baseScript, name);
		} catch (Exception e) {
			//In case of crash: log error but still try to create a model out of the vertices that survived.
			FMLLog.log("InductiveAutomation ShaftRenderer", Level.ERROR, e, "creating model for \"%s\" failed:", name);
		}
		model = new IntArrayModel(modelGenerator);
		modelCache.put(name, model);
		return model;
	}

	@Override
	public void bakeModels(IResourceManager manager) {
		modelGenerator = new ModelContext(new ResourceLocation("indaut", "models/tesr/"));
		try {
			baseScript = modelGenerator.getOrLoad("shafts", manager);
			modelCache.clear();
		} catch (Exception e) {
			baseScript = null;
			FMLLog.log("InductiveAutomation ShaftRenderer", Level.ERROR, e, "loading script failed: ");
		}
	}

}
