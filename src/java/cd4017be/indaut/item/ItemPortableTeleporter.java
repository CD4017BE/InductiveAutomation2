package cd4017be.indaut.item;

import java.io.IOException;
import java.util.List;

import cd4017be.api.automation.AreaProtect;
import cd4017be.indaut.Config;
import cd4017be.indaut.render.gui.GuiPortableTeleporter;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.BlockGuiHandler.ClientItemPacketReceiver;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.MovedBlock;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.ItemGuiData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import cd4017be.api.energy.EnergyAutomation.EnergyItem;

public class ItemPortableTeleporter extends ItemEnergyCell implements IGuiItem, ClientItemPacketReceiver {

	public static float energyUse = 8.0F;

	public ItemPortableTeleporter(String id) {
		super(id, Config.Ecap[1]);
		this.setMaxDamage(16);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack item) {
		return EnumRarity.RARE;
	}

	@Override
	public Container getContainer(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new DataContainer(new ItemGuiData(this), player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(ItemStack item, EntityPlayer player, World world, BlockPos pos, int slot) {
		return new GuiPortableTeleporter(new DataContainer(new ItemGuiData(this), player));
	}

	@Override
	public void onPacketFromClient(PacketBuffer dis, EntityPlayer player, ItemStack item, int slot) throws IOException {
		if (item.getTagCompound() == null) item.setTagCompound(new NBTTagCompound());
		NBTTagList points = item.getTagCompound().getTagList("points", 10);
		NBTTagCompound tag;
		byte cmd = dis.readByte();
		if (cmd == 0) {//teleport
			int i = dis.readByte();
			if (i < 0 || i >= points.tagCount()) return;
			tag = points.getCompoundTagAt(i);
			int x = tag.getInteger("x");
			int y = tag.getInteger("y");
			int z = tag.getInteger("z");
			int d = tag.getByte("d");
			if (y < 0 || y > 256 || d != player.world.provider.getDimension()) {
				player.sendMessage(new TextComponentString("Destination outside the world!"));
				return;
			} else if (player.world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() || player.world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial().blocksMovement()) {
				player.sendMessage(new TextComponentString("Destination obscured!"));
				return;
			}
			this.teleportTo(item, player.world, x, y, z, player);
			player.setHeldItem(player.getActiveHand(), item);
			return;
		} else if (cmd == 1) {//add Point
			if (points.tagCount() >= 64) {
				player.sendMessage(new TextComponentString("64 waypoints max!"));
				return;
			}
			tag = new NBTTagCompound();
			tag.setInteger("x", MathHelper.floor(player.posX));
			tag.setInteger("y", MathHelper.floor(player.posY));
			tag.setInteger("z", MathHelper.floor(player.posZ));
			tag.setByte("d", (byte)player.world.provider.getDimension());
			tag.setString("n", "P" + points.tagCount());
			points.appendTag(tag);
		} else if (cmd == 2) {//edit Point
			int i = dis.readByte();
			if (i < 0 || i >= points.tagCount()) return;
			tag = points.getCompoundTagAt(i);
			tag.setInteger("x", dis.readInt());
			tag.setInteger("y", dis.readInt());
			tag.setInteger("z", dis.readInt());
			tag.setByte("d", dis.readByte());
			tag.setString("n", dis.readString(32));
		} else if (cmd == 3) {//delete Point
			int i = dis.readByte();
			if (i >= 0 && i < points.tagCount()) points.removeTag(i);
		}
		item.getTagCompound().setTag("points", points);
	}

	@Override
	public int getChargeSpeed(ItemStack item) {
		return 400;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		if (player.isSneaking()) {
			if (world.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
			Vec3d pos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
			Vec3d dir = player.getLookVec();
			RayTraceResult obj = world.rayTraceBlocks(pos, pos.addVector(dir.xCoord * 256, dir.yCoord * 256, dir.zCoord * 256), false, true, false);
			if (obj == null) return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
			int[] t = this.findTarget(obj, world);
			if (t != null) {
				this.teleportTo(item, world, t[0], t[1], t[2], player);
			} else {
				player.sendMessage(new TextComponentString("Destination obscured!"));
			}
		} else {
			BlockGuiHandler.openItemGui(player, hand);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	private int[] findTarget(RayTraceResult pos, World world) {
		if (!world.getBlockState(pos.getBlockPos().up()).getMaterial().blocksMovement() && !world.getBlockState(pos.getBlockPos().up(2)).getMaterial().blocksMovement())
			return new int[]{pos.getBlockPos().getX(), pos.getBlockPos().getY() + 1, pos.getBlockPos().getZ()};
		if (pos.sideHit == EnumFacing.UP) return null;
		if (pos.sideHit == EnumFacing.DOWN) {
			if (!world.getBlockState(pos.getBlockPos().down()).getMaterial().blocksMovement() && !world.getBlockState(pos.getBlockPos().down(2)).getMaterial().blocksMovement())
				return new int[]{pos.getBlockPos().getX(), pos.getBlockPos().getY() - 2, pos.getBlockPos().getZ()};
			else return null;
		}
		BlockPos p = pos.getBlockPos().offset(pos.sideHit);
		if (world.getBlockState(p).getMaterial().blocksMovement()) return null;
		else if (!world.getBlockState(p.down()).getMaterial().blocksMovement()) return new int[]{p.getX(), p.getY() - 1, p.getZ()};
		else if (!world.getBlockState(p.up()).getMaterial().blocksMovement()) return new int[]{p.getX(), p.getY(), p.getZ()};
		else return null;
	}

	public void teleportTo(ItemStack item, World world, int x, int y, int z, EntityPlayer player) {
		int x0 = MathHelper.floor(player.posX),
			y0 = MathHelper.floor(player.posY) - 1,
			z0 = MathHelper.floor(player.posZ);
		int dx = x - x0, dy = y - y0, dz = z - z0;
		int e = MathHelper.floor(Math.sqrt(dx * dx + dy * dy + dz * dz) * energyUse);
		EnergyItem energy = new EnergyItem(item, this, -1);
		if (energy.getStorageI() < e) {
			player.sendMessage(new TextComponentString("Not enough Energy: " + e + " kJ needed!"));
			return;
		}
		if (!AreaProtect.interactingAllowed(player.getGameProfile(), world, x >> 4, z >> 4)) {
			player.sendMessage(new TextComponentString("Destination protected!"));
			return;
		}
		energy.addEnergyI(-e);
		int dim = player.dimension;
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x0, y0, z0, x0 + 1, y0 + 2, z0 + 1));
		for (Entity entity : entities) {
			MovedBlock.moveEntity(entity, dim, (double)x + 0.5D, (double)y, (double)z + 0.5D);
		}
	}

}
