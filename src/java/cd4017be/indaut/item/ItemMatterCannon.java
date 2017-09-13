package cd4017be.indaut.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cd4017be.api.automation.AreaProtect;
import cd4017be.api.automation.MatterOrbItemHandler;
import cd4017be.api.automation.MatterOrbItemHandler.IMatterOrb;
import cd4017be.indaut.Config;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.ClientInputHandler.IScrollHandlerItem;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import cd4017be.api.energy.EnergyAutomation.EnergyItem;

/**
 *
 * @author CD4017BE
 */
public class ItemMatterCannon extends ItemEnergyCell implements IMatterOrb, IGuiItem, IScrollHandlerItem {

	public static int EnergyUsage = 16;

	public ItemMatterCannon(String id) {
		super(id, Config.Ecap[1]);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack item) {
		return EnumRarity.EPIC;
	}

	@Override
	public int getChargeSpeed(ItemStack item) {
		return 1000;
	}

	private static final String[] modes = {" basic", " fill", " extend", " copy"};

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		int sel = item.getTagCompound() == null ? 0 : item.getTagCompound().getShort("sel");
		int mode = item.getTagCompound() == null ? 0 : item.getTagCompound().getByte("mode");
		if (mode < 0 || mode >= modes.length) mode = 0;
		ItemStack stack = MatterOrbItemHandler.getItem(item, sel);
		return super.getItemStackDisplayName(item) + modes[mode] + " (" + (stack != null ? stack.getCount() + "x " + stack.getDisplayName() + ")" : "Empty)");
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean f) {
		MatterOrbItemHandler.addInformation(item, list);
		super.addInformation(item, player, list, f);
	}

	@Override
	public int getMaxTypes(ItemStack item) {
		return ItemMatterOrb.MaxTypes;
	}

	@Override
	public String getMatterTag(ItemStack item) {
		return "matter";
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		Vec3d pos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3d dir = player.getLookVec();
		RayTraceResult obj = world.rayTraceBlocks(pos, pos.addVector(dir.xCoord * 128, dir.yCoord * 128, dir.zCoord * 128), false);
		if (obj != null) new ActionResult<ItemStack>(this.onItemUse(player, world, obj.getBlockPos(), hand, obj.sideHit, (float)obj.hitVec.xCoord, (float)obj.hitVec.yCoord, (float)obj.hitVec.zCoord), item);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	private class Position {
		public final BlockPos pos;
		public final EnumFacing s;
		
		public Position(BlockPos pos, EnumFacing s) {
			this.pos = pos;
			this.s = s;
		}
		
		public boolean place(ItemStack item, EntityPlayer player, World world, EnumHand hand, float X, float Y, float Z, int ax, ArrayList<Position> list) {
			BlockPos npos = pos.offset(s);
			if (world.getBlockState(npos).getBlock().isReplaceable(world, npos))
				for (int i = 0; i < 6; i++)
					if (i != ax && i != (ax^1) && i != (s.getIndex()^1))
						list.add(new Position(npos, EnumFacing.VALUES[i]));
			if (!ItemMatterCannon.this.placeItem(item, player, world, npos, hand, s, X, Y, Z)) return false;
			return true;
		} 
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing s, float X, float Y, float Z) {
		ItemStack item = player.getHeldItem(hand);
		if (world.isRemote) return EnumActionResult.SUCCESS;
		int mode = item.getTagCompound() == null ? 0 : item.getTagCompound().getByte("mode");
		if (mode < 0) return EnumActionResult.SUCCESS;
		if (mode == 0) {
			if (!AreaProtect.operationAllowed(player.getGameProfile(), world, pos.getX() >> 4, pos.getZ() >> 4)) {
				player.sendMessage(new TextComponentString("Block is Protected"));
				return EnumActionResult.SUCCESS;
			}
			this.placeItem(item, player, world, pos, hand, s, X, Y, Z);
		} else if (mode == 1) {
			if (!AreaProtect.operationAllowed(player.getGameProfile(), world, pos.getX() - 15, pos.getX() + 16, pos.getZ() - 15, pos.getZ() + 16)) {
				player.sendMessage(new TextComponentString("Block is Protected"));
				return EnumActionResult.SUCCESS;
			}
			ArrayList<Position> curList = new ArrayList<Position>();
			curList.add(new Position(pos, s));
			s = EnumFacing.VALUES[Utils.getLookDir(player)];
			ArrayList<Position> newList;
			int n = 0;
			while (!curList.isEmpty() && n < 256) {
				newList = new ArrayList<Position>();
				for (Position p : curList)
					if (p.place(item, player, world, hand, X, Y, Z, s.getIndex(), newList)) n++;
					else return EnumActionResult.SUCCESS;
				curList = newList;
			}
		} else if (mode == 2) {
			if (!AreaProtect.operationAllowed(player.getGameProfile(), world, pos.getX() - 7, pos.getX() + 8, pos.getZ() - 7, pos.getZ() + 8)) {
				player.sendMessage(new TextComponentString("Block is Protected"));
				return EnumActionResult.SUCCESS;
			}
			byte ax = (byte)(s.getIndex() / 2);
			BlockPos p;
			for (int i = -7; i <= 7; i++)
				for (int j = -7; j <= 7; j++) {
					p = pos.add(ax==2?0:i, ax==0?0:j, ax==1?0: ax==2?i:j);
					if (!world.getBlockState(p).getBlock().isReplaceable(world, p) && 
							!this.placeItem(item, player, world, p, hand, s, X, Y, Z))
						return EnumActionResult.SUCCESS;
				}
		} else if (mode == 3) {
			if (!AreaProtect.operationAllowed(player.getGameProfile(), world, pos.getX() - 7, pos.getX() + 8, pos.getZ() - 7, pos.getZ() + 8)) {
				player.sendMessage(new TextComponentString("Block is Protected"));
				return EnumActionResult.SUCCESS;
			}
			IBlockState state = world.getBlockState(pos);
			byte ax = (byte)(s.getIndex() / 2);
			BlockPos p;
			for (int i = -7; i <= 7; i++)
				for (int j = -7; j <= 7; j++) {
					p = pos.add(ax==2?0:i, ax==0?0:j, ax==1?0: ax==2?i:j);
					if (world.getBlockState(p) == state && 
							!this.placeItem(item, player, world, p, hand, s, X, Y, Z))
						return EnumActionResult.SUCCESS;
				}
		}
		return EnumActionResult.SUCCESS;
	}

	private boolean placeItem(ItemStack item, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing s, float X, float Y, float Z) {
		EnergyItem energy = new EnergyItem(item, this, -1);
		if (energy.getStorageI() < EnergyUsage){
			player.sendMessage(new TextComponentString("Out of Energy"));
			return false;
		}
		IBlockState state = world.getBlockState(pos);
		int sel = item.getTagCompound().getShort("sel");
		int tps = MatterOrbItemHandler.getUsedTypes(item);
		if (sel >= tps && tps > 0) sel %= tps;
		ItemStack stack = MatterOrbItemHandler.decrStackSize(item, sel, 1);
		ItemStack refStack = MatterOrbItemHandler.getItem(item, sel);
		boolean empty = refStack == null || !refStack.isItemEqual(stack);
		player.setHeldItem(hand, stack);
		if (state.getBlock().onBlockActivated(world, pos, state, player, hand, s, X, Y, Z) || (stack != null && stack.getItem() != null && stack.getItem().onItemUse(stack, player, world, pos, hand, s, X, Y, Z) == EnumActionResult.SUCCESS)) {
			energy.addEnergy(-EnergyUsage);
		}
		stack = player.getHeldItemMainhand();
		if (stack != null) {
			ItemStack[] remain = MatterOrbItemHandler.addItemStacks(item, stack);
			if (remain != null && remain.length == 1) {
				stack = remain[0];
				if (!player.inventory.addItemStackToInventory(stack)) {
					EntityItem entity = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
					world.spawnEntity(entity);
				}
			}
		}
		player.setHeldItem(hand, item);
		return !empty;
	}

	@Override
	public Container getContainer(World world, EntityPlayer player, int x, int y, int z) {
		return null;
	}

	@Override
	public GuiContainer getGui(World world, EntityPlayer player, int x, int y, int z) {
		return null;
	}

	@Override
	public void onPlayerCommand(ItemStack item, EntityPlayer player, PacketBuffer dis) {
		byte cmd = dis.readByte();
		int n = MatterOrbItemHandler.getUsedTypes(item);
		if (cmd == 1 && n > 0) {
			item.getTagCompound().setShort("sel", (short)((item.getTagCompound().getShort("sel") + 1) % n));
		} else if (cmd == 0 && n > 0) {
			item.getTagCompound().setShort("sel", (short)((item.getTagCompound().getShort("sel") + n - 1) % n));
		} else if (cmd == 2) {
			item.getTagCompound().setByte("mode", (byte)((item.getTagCompound().getByte("mode") + 1) % modes.length));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSneakScroll(ItemStack item, EntityPlayer player, int scroll) {
		byte cmd = 2;
		if (scroll < 0) cmd = 0;
		else if (scroll > 0) cmd = 1;
			PacketBuffer dos = BlockGuiHandler.getPacketTargetData(new BlockPos(0, -1, 0));
			dos.writeByte(cmd);
			BlockGuiHandler.sendPacketToServer(dos);
	}

}
