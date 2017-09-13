package cd4017be.indaut.render.gui;

import org.lwjgl.opengl.GL11;

import cd4017be.indaut.tileentity.SolidHeater;
import cd4017be.lib.render.InWorldUIRenderer;
import cd4017be.lib.render.InWorldUIRenderer.Gui;
import cd4017be.lib.render.Util;
import cd4017be.lib.util.TooltipUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;

public class GuiSolidHeater implements Gui<SolidHeater> {

	private static final float TEX_RES = 64F;
	private static final ResourceLocation TEXTURE = new ResourceLocation("indaut:textures/tesr/solid_heater.png");

	@Override
	public void renderComponents(SolidHeater tile, RayTraceResult aim, float t) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0.5, -0.5);
		GlStateManager.scale(-0.0625, -0.0625, 0.0625);
		GlStateManager.cullFace(CullFace.FRONT);
		InWorldUIRenderer.instance.bindTexture(TEXTURE);
		final float px = 2F, py = 12F, w = 8, h = 2;
		final float burn = tile.burnRate / SolidHeater.MaxBurnRate * w;
		Tessellator render = Tessellator.getInstance();
		VertexBuffer buff = render.getBuffer();
		buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buff.addVertexData(Util.texturedRect(px-.5F, py-.5F, -0.02F, w+1, h+1, 0, 0, 18F/TEX_RES, 6F/TEX_RES));
		buff.addVertexData(Util.texturedRect(px + burn, py, -.01F, w - burn, h, 0, 6F/TEX_RES, 16F/TEX_RES, 4F/TEX_RES));
		render.draw();
		GlStateManager.cullFace(CullFace.BACK);
		GlStateManager.popMatrix();
	}

	@Override
	public String getTooltip(SolidHeater tile, int comp) {
		switch(comp) {
		case 0: return TooltipUtil.format("gui.cd4017be.solidHeater.fuel", TooltipUtil.formatNumber(tile.fuel, 5, 0), TooltipUtil.formatNumber(SolidHeater.maxFuel, 5, 0));
		case 1: return TooltipUtil.format("gui.cd4017be.solidHeater.burn", TooltipUtil.formatNumber(tile.burnRate * 20F, 3, 0));
		case 2: return TooltipUtil.format("gui.cd4017be.solidHeater.temp", tile.heat.T - 273.15F);
		default: return null;
		}
	}

}
