package cd4017be.indaut.jetpack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cd4017be.indaut.Config;
import cd4017be.indaut.item.ItemInvEnergy;
import cd4017be.indaut.item.ItemJetpack;
import cd4017be.indaut.item.ItemJetpackFuel;
import cd4017be.indaut.jetpack.JetPackConfig.Mode;
import cd4017be.lib.util.Vec2;
import cd4017be.lib.util.Vec3;

/**
 *
 * @author CD4017BE
 */
public class TickHandler {

	private static final Minecraft mc = Minecraft.getMinecraft();
	private Vec2 dirVec = Vec2.Def(0, 0);
	public int power = 0;
	private float energy = 0, H2 = 0, O2 = 0;
	public static TickHandler instance = new TickHandler();
	private static final KeyBinding keyOn = new KeyBinding("Jetpack On/Off", Keyboard.KEY_F, "Automation");
	private static final KeyBinding keyMode = new KeyBinding("Jetpack control-mode", Keyboard.KEY_Y, "Automation");
	private static boolean lastKeyOnState = false;
	private static boolean lastKeyModeState = false;
	/**	used for rendering of multiblock structures */
	public static int tick = 0;

	public static void init() {
		ClientRegistry.registerKeyBinding(keyMode);
		ClientRegistry.registerKeyBinding(keyOn);
		MinecraftForge.EVENT_BUS.register(instance);
	}

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START && mc.player != null && mc.gameSettings != null && !mc.isGamePaused())
			this.checkJetpack();
	}

	private void checkJetpack() {
		ItemStack item = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (item == null || !(item.getItem() instanceof ItemJetpack)) return;
		this.updateKeyStates(item);
		NBTTagCompound nbt = item.getTagCompound();
		if (nbt == null || !nbt.getBoolean("On")) return;
		if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) power++;
		if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) power--;
		if (power < 0) power = 0;
		if (power > ItemJetpack.maxPower) power = ItemJetpack.maxPower;
		Vec3 dir;
		Mode mode = JetPackConfig.getMode();
		if (mode.vertAngleFaktor == 0) {
			dir = Vec3.Def(0, mode.verticalComp, 0);
		} else {
			float a = mode.vertAngleOffset - mode.vertAngleFaktor * mc.player.rotationPitch;
			float f1 = MathHelper.cos(-mc.player.rotationYaw * 0.017453292F);// - (float)Math.PI
			float f2 = MathHelper.sin(-mc.player.rotationYaw * 0.017453292F);// - (float)Math.PI
			float f3 = MathHelper.cos(a * 0.017453292F);
			float f4 = MathHelper.sin(a * 0.017453292F);
			dir = Vec3.Def((double)(f2 * f3), (double)f4, (double)(f1 * f3)).scale(mode.verticalComp);
		}
		if (mode.moveStrength > 0) {
			Vec2 ctr = Vec2.Def(0, 0);
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)) ctr.z--;
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindBack)) ctr.z++;
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) ctr.x++;
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) ctr.x--;
			dirVec = dirVec.scale(1 - mode.moveStrength).add(ctr.scale(mode.moveStrength));
			float cos = MathHelper.cos(-mc.player.rotationYaw * 0.017453292F - (float)Math.PI);
			float sin = MathHelper.sin(-mc.player.rotationYaw * 0.017453292F - (float)Math.PI);
			dir = dir.add(new Vec3(dirVec.rotate(cos, sin), 0));
		}
		dir = dir.norm();
		O2 = H2 = energy = 0;
		if (nbt.getInteger("power") < 0) return;
		else for (int i = mc.player.inventory.mainInventory.size() - 1; i >= 0; i--) {
			ItemStack stack = mc.player.inventory.mainInventory.get(i);
			if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) continue;
			if (stack.getItem() instanceof ItemInvEnergy) {
				energy = (float)stack.getTagCompound().getInteger("energy") / (float)((ItemInvEnergy)stack.getItem()).getEnergyCap(stack);
				break;
			} else if (stack.getItem() instanceof ItemJetpackFuel) {
				O2 = stack.getTagCompound().getFloat("O2") / (float)Config.tankCap[2];
				H2 = stack.getTagCompound().getFloat("H2") / (float)Config.tankCap[2] / 2F;
				break;
			}
		}
		ItemJetpack.updateMovement(mc.player, dir, power);
		PacketBuffer dos = new PacketBuffer(Unpooled.buffer());
		//command-ID
		dos.writeByte(0);
		dos.writeInt(power);
		dos.writeFloat((float)dir.x);
		dos.writeFloat((float)dir.y);
		dos.writeFloat((float)dir.z);
		/*
		dos.writeFloat((float)mc.player.posX);
		dos.writeFloat((float)mc.player.posY);
		dos.writeFloat((float)mc.player.posZ);
		dos.writeFloat((float)mc.player.motionX);
		dos.writeFloat((float)mc.player.motionY);
		dos.writeFloat((float)mc.player.motionZ);
		*/
		PacketHandler.eventChannel.sendToServer(new FMLProxyPacket(dos, PacketHandler.channel));
	}

	private void updateKeyStates(ItemStack item) {
		boolean pressOn = !lastKeyOnState && keyOn.isPressed();
		boolean pressMode = !lastKeyModeState && keyMode.isPressed();
		lastKeyOnState = keyOn.isPressed();
		lastKeyModeState = keyMode.isPressed();
		if (mc.currentScreen == null) {
			if (pressOn) {
				PacketBuffer dos = new PacketBuffer(Unpooled.buffer());
				dos.writeByte(2);
				PacketHandler.eventChannel.sendToServer(new FMLProxyPacket(dos, PacketHandler.channel));
			}
			if (pressMode && item.getTagCompound() != null && item.getTagCompound().getBoolean("On")) {
				JetPackConfig.mode++;
				return;
			} else if (pressMode) {
				FMLClientHandler.instance().showGuiScreen(new GuiJetpackConfig());
			}
		}
	}

	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event) {
		tick++;
		if (mc.currentScreen == null && event.phase == TickEvent.Phase.END) {
			ItemStack item = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (item == null || !(item.getItem() instanceof ItemJetpack) || item.getTagCompound() == null) return;
			NBTTagCompound nbt = item.getTagCompound();
			if (!nbt.getBoolean("On")) return;
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			mc.fontRendererObj.drawString("Jetpack Control Mode: " + JetPackConfig.getMode().name, 8, 8, 0x40ff40);
			GL11.glColor4f(1, 1, 1, 1);
			mc.renderEngine.bindTexture(new ResourceLocation("automation", "textures/gui/jetpack.png"));
			int hgt = this.getScreenHeight();
			mc.ingameGUI.drawTexturedModalRect(8, (hgt - 176) / 2, 0, 0, 16, 169);
			int n = power * 160 / ItemJetpack.maxPower;
			mc.ingameGUI.drawTexturedModalRect(9, (hgt - 176) / 2 + 161 - n, 16, 160 - n, 6, n);
			int y = hgt - 74;
			if (nbt.getInteger("power") < 0)
				mc.fontRendererObj.drawString("Out of Fuel!", 8, y, 0xff7f3f);
			else if (energy > 0) {
				mc.ingameGUI.drawTexturedModalRect(4, y, 0, 187, 74, 18);
				n = (int)(energy * 1024F);
				mc.ingameGUI.drawTexturedModalRect(5, y + 1, 0, 240, n / 16, 16);
				mc.ingameGUI.drawTexturedModalRect(5 + n / 16, y + 1, n / 16, 240, 1, n % 16);
			} else if (O2 > 0 || H2 > 0) {
				mc.ingameGUI.drawTexturedModalRect(4, y, 0, 169, 74, 18);
				n = (int)(O2 * 320F);
				mc.ingameGUI.drawTexturedModalRect(5, y + 1, 0, 225, n / 5, 5);
				mc.ingameGUI.drawTexturedModalRect(5 + n / 5, y + 1, n / 5, 225, 1, n % 5);
				n = (int)(H2 * 640F);
				mc.ingameGUI.drawTexturedModalRect(5, y + 7, 0, 230, n / 10, 10);
				mc.ingameGUI.drawTexturedModalRect(5 + n / 10, y + 7, n / 10, 230, 1, n % 10);
			}
			Vec3 mov = Vec3.Def(mc.player.posX - mc.player.lastTickPosX, mc.player.posY - mc.player.lastTickPosY, mc.player.posZ - mc.player.lastTickPosZ).scale(20D);
			Gui.drawRect(4, hgt - 52, 110, hgt - 4, 0x80000000);
			mc.fontRendererObj.drawString(String.format("Speed  = %5.1f m/s", mov.l()), 8, hgt - 48, 0xff3f00);
			mc.fontRendererObj.drawString(String.format("Ascent = %+5.1f m/s", mov.y), 8, hgt - 32, 0xff3f00);
			mc.fontRendererObj.drawString(String.format("Height = %5.1f m", mc.player.posY), 8, hgt - 16, 0xff3f00);
		}
	}

	private int getScreenHeight() {
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		return scaledresolution.getScaledHeight();
	}

}
