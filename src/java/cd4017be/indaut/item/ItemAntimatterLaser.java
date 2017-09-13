package cd4017be.indaut.item;

import java.util.List;
import java.util.Map;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cd4017be.api.automation.AntimatterItemHandler;
import cd4017be.api.automation.AntimatterItemHandler.IAntimatterItem;
import cd4017be.api.automation.AreaProtect;
import cd4017be.api.automation.MatterOrbItemHandler;
import cd4017be.api.automation.MatterOrbItemHandler.IMatterOrb;
import cd4017be.api.energy.EnergyAutomation.EnergyItem;
import cd4017be.indaut.Config;
import cd4017be.indaut.Objects;
import cd4017be.indaut.render.gui.GuiAMLEnchant;
import cd4017be.lib.ClientInputHandler.IScrollHandlerItem;
import cd4017be.lib.BlockGuiHandler;
import cd4017be.lib.IGuiItem;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.ItemGuiData;
import cd4017be.lib.Gui.SlotItemType;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.templates.InventoryItem;
import cd4017be.lib.templates.InventoryItem.IItemInventory;
import cd4017be.lib.templates.SingleFluidItemHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 *
 * @author CD4017BE
 */
public class ItemAntimatterLaser extends ItemEnergyCell implements IAntimatterItem, IMatterOrb, IGuiItem, IScrollHandlerItem, IItemInventory {

	public static int EnergyUsage = 16;
	public static float AmUsage = 1F;
	public static float BaseDamage = 12F;
	public static float MaxDamage = 60F;
	public static float DamageMult = 1.0F;
	public static float AMDamage = 1.0F;
	public static float AMDmgExp = 1.0F;

	public ItemAntimatterLaser(String id) {
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

	private static final String[] modes = {" 1x1", " 3x3", " 5x5", " 7x7", " 9x9", " vein", " off"};

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		if (item.getTagCompound() == null) return super.getItemStackDisplayName(item);
		int am = AntimatterItemHandler.getAntimatter(item);
		int mode = item.getTagCompound().getByte("mode") & 0xff;
		return super.getItemStackDisplayName(item) + modes[mode % modes.length] + " (" + (am > 0 ? am + " ng)" : "Empty)");
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean f) {
		AntimatterItemHandler.addInformation(item, list);
		MatterOrbItemHandler.addInformation(item, list);
		super.addInformation(item, player, list, f);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		int mode = item.getTagCompound().getByte("mode");
		Vec3d pos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3d dir = player.getLookVec();
		Vec3d pos1 = pos.addVector(dir.xCoord * 128, dir.yCoord * 128, dir.zCoord * 128);
		double x0 = pos.xCoord, y0 = pos.yCoord, z0 = pos.zCoord, 
				x1 = pos1.xCoord, y1 = pos1.yCoord, z1 = pos1.zCoord;
		RayTraceResult obj = mode == 6 || player.isSneaking() ? null : world.rayTraceBlocks(pos, pos1, false);
		pos = new Vec3d(x0, y0, z0);
		pos1 = new Vec3d(x1, y1, z1);
		if (obj != null) {
			x1 = obj.hitVec.xCoord;
			y1 = obj.hitVec.yCoord;
			z1 = obj.hitVec.zCoord;
		}
		if (x1 < x0) {double a = x0; x0 = x1; x1 = a;}
		if (y1 < y0) {double a = y0; y0 = y1; y1 = a;}
		if (z1 < z0) {double a = z0; z0 = z1; z1 = a;}
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(x0, y0, z0, x1, y1, z1));
		RayTraceResult obj1;
		for (Entity e : list) {
			obj1 = e.getEntityBoundingBox().calculateIntercept(pos, pos1);
			if (obj1 != null && (obj == null || obj1.hitVec.squareDistanceTo(pos) < obj.hitVec.squareDistanceTo(pos))) 
				obj = new RayTraceResult(e);
		}
		if (obj == null) return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
		else if (obj.typeOfHit == RayTraceResult.Type.BLOCK) return new ActionResult<ItemStack>(this.onItemUse(player, world, obj.getBlockPos(), hand, obj.sideHit, (float)obj.hitVec.xCoord, (float)obj.hitVec.yCoord, (float)obj.hitVec.zCoord), item);
		else if (obj.typeOfHit == RayTraceResult.Type.ENTITY) this.useOnEntity(item, player, obj.entityHit);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	private void useOnEntity(ItemStack item, EntityPlayer player, Entity entity) {
		if (player.world.isRemote || entity.isDead) return;
		if (!(entity instanceof EntityItem || entity instanceof EntityXPOrb)) {
			EnergyItem energy = new EnergyItem(item, this, -1);
			if (energy.getStorageI() < EnergyUsage) {
				player.sendMessage(new TextComponentString("Out of Energy!"));
				return;
			}
			float dmg = BaseDamage;
			if (entity instanceof EntityLivingBase) {
				dmg = Math.min(MaxDamage, Math.max(dmg, ((EntityLivingBase)entity).getMaxHealth()));
			}
			float am = AntimatterItemHandler.getAntimatter(item) + item.getTagCompound().getFloat("buff");
			dmg = Math.min(dmg, (float)Math.pow(am / AMDamage, 1D / AMDmgExp));
			if (dmg < 1F) return;
			energy.addEnergyI(-EnergyUsage);
			float r = (float)Math.pow(dmg, AMDmgExp) * AMDamage - item.getTagCompound().getFloat("buff");
			int n = (int)Math.ceil(r);
			if (n != 0) item.getTagCompound().setInteger("antimatter", item.getTagCompound().getInteger("antimatter") - n);
			item.getTagCompound().setFloat("buff", n - r);
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), dmg);
		}
		for (EntityItem ei : (List<EntityItem>)player.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entity.posX - 2.5D, entity.posY - 2.5D, entity.posZ - 2.5D, entity.posX + 2.5D, entity.posY + 2.5D, entity.posZ + 2.5D))) {
			if (!ei.isDead && MatterOrbItemHandler.canInsert(item, ei.getEntityItem())) {
				MatterOrbItemHandler.addItemStacks(item, ei.getEntityItem());
				ei.setDead();
			}
		}
		for (EntityXPOrb ei : (List<EntityXPOrb>)player.world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(entity.posX - 5D, entity.posY - 5D, entity.posZ - 5D, entity.posX + 5D, entity.posY + 5D, entity.posZ + 5D))) {
			ei.setPosition(player.posX, player.posY - 0.5D, player.posZ);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing s, float X, float Y, float Z) {
		ItemStack item = player.getHeldItem(hand);
		int mode = item.getTagCompound().getByte("mode");
		if (player.isSneaking()) return EnumActionResult.FAIL;
		if (world.isRemote) return EnumActionResult.FAIL;
		if (mode < 0) return EnumActionResult.FAIL;
		else if (mode < 5) {
			if (!AreaProtect.operationAllowed(player.getGameProfile(), world, pos.getX() - mode, pos.getX() + mode + 1, pos.getZ() - mode, pos.getZ() + mode + 1)) {
				player.sendMessage(new TextComponentString("Block is Protected"));
				return EnumActionResult.SUCCESS;
			}
			byte ax = (byte)(s.getIndex() / 2);
			for (int i = -mode; i <= mode; i++)
				for (int j = -mode; j <= mode; j++)
					if (!this.breakBlock(item, player, world, pos.add(ax==2?0:i, ax==0?0:j, ax==1?0: ax==2?i:j)))
						return EnumActionResult.SUCCESS;;
		} else if (mode == 5) {
			if (!AreaProtect.operationAllowed(player.getGameProfile(), world, pos.getX() - 7, pos.getX() + 8, pos.getZ() - 7, pos.getZ() + 8)) {
				player.sendMessage(new TextComponentString("Block is Protected"));
				return EnumActionResult.SUCCESS;
			}
			IBlockState state = world.getBlockState(pos);
			for (int i = -7; i <= 7; i++)
				for (int j = -7; j <= 7; j++)
					for (int k = -7; k <= 7; k++)
						if (world.getBlockState(pos.add(i, j, k)) == state)
							if (!this.breakBlock(item, player, world, pos.add(i, j, k)))
								return EnumActionResult.SUCCESS;;
		}
		return EnumActionResult.SUCCESS;
	}

	private boolean breakBlock(ItemStack item, EntityPlayer player, World world, BlockPos pos) {
		Enchantments ench = new Enchantments(item);
		EnergyItem energy = new EnergyItem(item, this, -1);
		if (energy.getStorageI() < ench.Euse) {
			player.sendMessage(new TextComponentString("Out of Energy!"));
			return false;
		}
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().isAir(state, world, pos)) return true;
		float r = state.getBlock().getExplosionResistance(world, pos, player, null) * ench.amMult;
		float am = AntimatterItemHandler.getAntimatter(item) + item.getTagCompound().getFloat("buff");
		if (am < r) {
			player.sendMessage(new TextComponentString("Not enough Antimatter, Needed: " + r + " pg"));
			return false;
		}
		ItemStack[] drop;
		if (ench.silktouch && state.getBlock().canSilkHarvest(world, pos, state, player)) {
			drop = new ItemStack[]{new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state))};
		} else {
			List<ItemStack> list = state.getBlock().getDrops(world, pos, state, ench.fortune);
			drop = list == null ? null : list.toArray(new ItemStack[list.size()]);
		}
		if (!MatterOrbItemHandler.canInsert(item, drop)) {
			player.sendMessage(new TextComponentString("Matter Orb is full!"));
			return false;
		}
		energy.addEnergyI(-ench.Euse);
		r -= item.getTagCompound().getFloat("buff");
		int n = (int)Math.ceil(r);
		if (n != 0) item.getTagCompound().setInteger("antimatter", item.getTagCompound().getInteger("antimatter") - n);
		item.getTagCompound().setFloat("buff", n - r);
		world.setBlockToAir(pos);
		MatterOrbItemHandler.addItemStacks(item, drop);
		return true;
	}

	public static class Enchantments {
		public boolean silktouch;
		public short fortune;
		public short efficiency;
		public short unbreaking;
		public float amMult;
		public int Euse;
		public Enchantments(ItemStack item) {
			if (item != null) {
				Map<Enchantment, Integer> list = EnchantmentHelper.getEnchantments(item);
				this.silktouch = list.containsKey(net.minecraft.init.Enchantments.SILK_TOUCH);
				Integer lvl = list.get(net.minecraft.init.Enchantments.FORTUNE);
				if (lvl != null) fortune = lvl.shortValue();
				lvl = list.get(net.minecraft.init.Enchantments.EFFICIENCY);
				if (lvl != null) efficiency = lvl.shortValue();
				lvl = list.get(net.minecraft.init.Enchantments.UNBREAKING);
				if (lvl != null) unbreaking = lvl.shortValue();
			}
			Euse = EnergyUsage * 3 / (3 + unbreaking);
			amMult = AmUsage * 0.5F / (float)(4 + efficiency) * (silktouch ? 2.0F : 1F + 0.5F * (float)fortune);
		}
	}

	@Override
	public int getAmCapacity(ItemStack item) {
		return Config.tankCap[4];
	}

	@Override
	public String getAntimatterTag(ItemStack item) {
		return "antimatter";
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
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new SingleFluidItemHandler(stack, getAmCapacity(stack), Objects.L_antimatter, getAntimatterTag(stack));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onSneakScroll(ItemStack item, EntityPlayer player, int scroll) {
		byte cmd = 2;
		if (scroll < 0) cmd = 0;
		else if (scroll > 0) cmd = 1;
			PacketBuffer bos = BlockGuiHandler.getPacketTargetData(new BlockPos(0, -1, 0));
			bos.writeByte(cmd);
			BlockGuiHandler.sendPacketToServer(bos);
	}

	@Override
	public Container getContainer(World world, EntityPlayer player, int x, int y, int z) {
		return new TileContainer(new GuiData(), player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(World world, EntityPlayer player, int x, int y, int z) {
		return new GuiAMLEnchant(new TileContainer(new GuiData(), player));
	}

	@Override
	public void onPlayerCommand(ItemStack item, EntityPlayer player, PacketBuffer dis) {
		byte cmd = dis.readByte();
		if (cmd == 0) {
			item.getTagCompound().setByte("mode", (byte)((item.getTagCompound().getByte("mode") - 1 + modes.length) % modes.length));
		} else if (cmd == 1) {
			item.getTagCompound().setByte("mode", (byte)((item.getTagCompound().getByte("mode") + 1) % modes.length));
		} else if (cmd == 2) {
			BlockGuiHandler.openItemGui(player, player.world, 0, -1, 0);
		}
	}

	@Override
	public ItemStack[] loadInventory(ItemStack inv, EntityPlayer player) {
		NBTTagList list = inv.getEnchantmentTagList();
		ItemStack[] items = new ItemStack[2];
		if (list != null) for (int i = 0; i < list.tagCount() && i < items.length; i++) {
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			int id = nbt.getShort("id");
			int lvl = nbt.getShort("lvl");
			items[i] = Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantment.getEnchantmentByID(id), lvl));
		}
		return items;
	}

	@Override
	public void saveInventory(ItemStack inv, EntityPlayer player, ItemStack[] items) {
		if (!inv.hasTagCompound()) inv.setTagCompound(new NBTTagCompound());
		inv.getTagCompound().removeTag("ench");
		NBTTagList list = new NBTTagList();
		boolean ench = false;
		for (int i = 0; i < items.length; i++)
			if (items[i] != null && items[i].getItem() == Items.ENCHANTED_BOOK) {
				NBTTagList l = Items.ENCHANTED_BOOK.getEnchantments(items[i]);
				if (l.tagCount() > 0) {
					list.appendTag(l.getCompoundTagAt(0));
					ench = true;
				}
			}
		if (ench) inv.getTagCompound().setTag("ench", list);
	}

	class GuiData extends ItemGuiData {

		public GuiData() {
			super(ItemAntimatterLaser.this);
		}

		@Override
		public void initContainer(DataContainer container) {
			TileContainer cont = (TileContainer)container;
			inv = new InventoryItem(cont.player);
			cont.addItemSlot(new SlotItemType(inv, 0, 26, 16, new ItemStack(Items.ENCHANTED_BOOK)));
			cont.addItemSlot(new SlotItemType(inv, 1, 44, 16, new ItemStack(Items.ENCHANTED_BOOK)));
			cont.addPlayerInventory(8, 50, false, true);
		}

	}

}
