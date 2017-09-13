package cd4017be.indaut.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cd4017be.api.automation.IOperatingArea;
import cd4017be.api.automation.PipeEnergy;
import cd4017be.indaut.Config;
import cd4017be.indaut.Objects;
import cd4017be.indaut.render.gui.InventoryPlacement;
import cd4017be.indaut.item.ItemItemUpgrade;
import cd4017be.indaut.item.ItemMachineSynchronizer;
import cd4017be.indaut.item.ItemPlacement;
import cd4017be.indaut.item.PipeUpgradeItem;
import cd4017be.lib.Gui.DataContainer;
import cd4017be.lib.Gui.DataContainer.IGuiData;
import cd4017be.lib.Gui.SlotHolo;
import cd4017be.lib.Gui.SlotItemType;
import cd4017be.lib.Gui.TileContainer;
import cd4017be.lib.Gui.TileContainer.ISlotClickHandler;
import cd4017be.lib.templates.AutomatedTile;
import cd4017be.lib.templates.Inventory.IAccessHandler;
import cd4017be.lib.util.CachedChunkProtection;
import cd4017be.lib.util.ItemFluidUtil;
import cd4017be.lib.util.Utils;
import cd4017be.lib.util.Utils.ItemType;
import cd4017be.lib.templates.Inventory;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author CD4017BE
 */
public class Farm extends AutomatedTile implements IOperatingArea, IGuiData, IAccessHandler, ISlotClickHandler {

	public static int Umax = 1200;
	public static float Energy = 25000F;
	public static float resistor = 20F;
	public static float eScale = (float)Math.sqrt(1D - 1D / resistor);
	private static final GameProfile defaultUser = new GameProfile(new UUID(0, 0), "#Farm");
	private int[] area = new int[6];
	private int px;
	private int py;
	private int pz;
	private float storage;
	private final Random random = new Random();
	private boolean invFull;
	private PipeUpgradeItem harvestFilter;
	private IOperatingArea slave = null;
	private GameProfile lastUser = defaultUser;
	private CachedChunkProtection prot;

	public Farm() {
		inventory = new Inventory(38, 2, this).group(0, 20, 32, Utils.OUT).group(1, 8, 20, Utils.ACC);
		energy = new PipeEnergy(Umax, Config.Rcond[1]);
	}
	
	@Override
	public void onPlaced(EntityLivingBase entity, ItemStack item)  
	{
		lastUser = entity instanceof EntityPlayer ? ((EntityPlayer)entity).getGameProfile() : new GameProfile(new UUID(0, 0), entity.getName());
		prot = null;
	}
	
	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack item, EnumFacing s, float X, float Y, float Z) 
	{
		lastUser = player.getGameProfile();
		prot = null;
		return super.onActivated(player, hand, item, s, X, Y, Z);
	}
	
	@Override
	public void update() 
	{
		super.update();
		if (world.isRemote) return;
		if (slave == null || ((TileEntity)slave).isInvalid()) slave = ItemMachineSynchronizer.getLink(inventory.items[37], this);
		if (storage < Energy)
		{
			storage += energy.getEnergy(0, resistor);
			energy.Ucap *= eScale;
		}
		if (checkBlock())
		{
			py--;
			if (py < area[1] || py >= area[4]) {
				py = area[4] - 1;
				px++;
			}
			if (px >= area[3] || px < area[0])
			{
				px = area[0];
				pz++;
			}
			if (pz >= area[5] || pz < area[2])
			{
				pz = area[2];
			}
		}
	}
	
	private boolean checkBlock()
	{
		BlockPos pos;
		while (!world.isBlockLoaded(pos = new BlockPos(px, py, pz)) || world.isAirBlock(pos)) {
			if (py <= area[1]) return true;
			py--;
		}
		if (prot == null || !prot.equalPos(pos)) prot = CachedChunkProtection.get(lastUser, world, pos);
		if (!prot.allow) return true;
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int m = block.getMetaFromState(state);
		byte Z;
		//harvesting
		if ((Z = this.doHarvest(pos, state, block, m)) >= 0) return Z != 0;
		//planting
		return this.doPlant(pos, state, block, m) != 0;
	}
	
	/** @return -1 = nothing, 0 = block, 1 = next */
	private byte doHarvest(BlockPos pos, IBlockState state, Block block, int m) {
		boolean hasSlave = this.checkSlave();
		if (!(state.getMaterial().isToolNotRequired() || hasSlave) || harvestFilter == null) return -1;
		if (!this.ckeckFilter(harvestFilter, state, block, m)) return -1;
		if (hasSlave) return slave.remoteOperation(pos) ? (byte)1 : 0;
		if (invFull && invFull()) return 0;
		invFull = false;
		if (storage < Energy) return 0;
		storage -= Energy;
		world.setBlockToAir(pos);
		List<ItemStack> list = block.getDrops(world, pos, state, 0);
		for (ItemStack list1 : list) add(list1);
		return 1;
	}
	
	/** @return -1 = nothing, 0 = block, 1 = next */
	private byte doPlant(BlockPos pos, IBlockState state, Block block, int m) {
		boolean air = world.isAirBlock(pos.up());
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		int n = 0;
		for (int i = 0; i < 8; i++) {
			if (inventory.items[i] == null) continue;
			Object plant = inventory.items[i].getItem() instanceof ItemBlock ? ((ItemBlock)inventory.items[i].getItem()).block : inventory.items[i].getItem();
			if (air && plant instanceof IPlantable && block.canSustainPlant(state, world, pos, EnumFacing.UP, (IPlantable)plant)) {
				list.add(new Object[]{i, plant, n, n += inventory.items[i].getCount()});
			} else if (plant instanceof ItemPlacement) {
				InventoryPlacement inv = new InventoryPlacement(inventory.items[i]);
				if (this.equalsBlock(inv.inventory[0], state, pos, m, !inv.useDamage(0))) {
					list.add(new Object[]{i, inv, n, n += inventory.items[i].getCount()});
				}
			}
		}
		if (n == 0) return -1;
		if (storage < Energy) return 0;
		int o = random.nextInt(n);
		for (Object[] obj : list) {
			if (o >= (Integer)obj[2] && o < (Integer)obj[3]) {
				return this.plantItem(inventory.items[(Integer)obj[0]], obj[1], pos) ? (byte)1 : 0;
			}
		}
		return -1;
	}
	
	private boolean checkSlave()
	{
		return slave != null && !((TileEntity)slave).isInvalid() && slave instanceof Miner && 
				(slave.getSlave() == null || slave.getSlave().getSlave() == null);
	}
	
	private boolean plantItem(ItemStack item, Object plant, BlockPos pos)
	{
		if (plant instanceof IPlantable) {
			if (!remove(item, 1)) return true;
			BlockPos pos1 = pos.up();
			IBlockState state = ((IPlantable)plant).getPlant(world, pos1);
			state = state.getBlock().onBlockPlaced(world, pos1, EnumFacing.DOWN, 0.5F, 0F, 0.5F, item.getItem().getMetadata(item.getItemDamage()), FakePlayerFactory.get((WorldServer)world, lastUser));
			world.setBlockState(pos.up(), state, 0x3);
			storage -= Energy;
			return true;
		} else {
			InventoryPlacement inv = (InventoryPlacement)plant;
			ItemStack[] items = new ItemStack[7];
			boolean canDo = true;
			int n;
			for (n = 0; n < inv.inventory.length - 1 && inv.inventory[n + 1] != null; n++) {
				items[n] = this.remove(inv.inventory[n + 1], inv.useDamage(n + 1));
				if (items[n] == null) {
					canDo = false;
					break;
				}
			}
			if (n == 0) return true;
			if (canDo) {
				storage -= Energy;
				EntityPlayer player = FakePlayerFactory.get((WorldServer)world, lastUser);
				for (int i = 0; i < n; i++)
					items[i] = inv.doPlacement(world, player, pos, i + 1, items[i]);
			}
			for (int i = 0; i < n; i++) 
				if (items[i] != null)
					ItemHandlerHelper.insertItemStacked(inventory.new Access(1), items[i], false);
			return true;
		}
	}
	
	private boolean ckeckFilter(PipeUpgradeItem blockFilter, IBlockState state, Block block, int m) {
		if ((blockFilter.mode&64) != 0 && (world.isBlockIndirectlyGettingPowered(getPos()) > 0 ^ (blockFilter.mode&128) == 0)) return false;
		ItemType filter = blockFilter.getFilter();
		List<ItemStack> list = block.getDrops(world, pos, state, 0);
		if ((blockFilter.mode&2) != 0) list.add(new ItemStack(block));
		boolean harvest = false;
		int match;
		for (ItemStack item : list) {
			if ((match = filter.getMatch(item)) >= 0) {
				if ((blockFilter.mode&32) != 0 && blockFilter.list[match].getCount() <= 16 && (blockFilter.list[match].getCount() & 15) != m)
					continue;
				harvest = true;
				break;
			}
		}
		return harvest ^ (blockFilter.mode&1) != 0;
	}
	
	private boolean equalsBlock(ItemStack item, IBlockState state, BlockPos pos, int m, boolean drop)
	{
		if (item == null) return false;
		else if (item.getItem() instanceof ItemItemUpgrade) {
			return this.ckeckFilter(PipeUpgradeItem.load(item.getTagCompound()), state, state.getBlock(), m);
		} else if (drop) {
			List<ItemStack> list = state.getBlock().getDrops(world, pos, state, 0);
			for (ItemStack stack : list)
				if (Utils.itemsEqual(item, stack)) return true;
			return false;
		} else if (item.getItem() instanceof ItemBlock) {
			ItemBlock ib = (ItemBlock)item.getItem();
			return ib.block == state.getBlock() && ib.getMetadata(item.getItemDamage()) == m;
		} else return false;
	}
	
	private boolean invFull()
	{
		for (int i = 20; i < 32; i++)
			if (inventory.items[i] == null) return false;
		return true;
	}
	
	private ItemStack remove(ItemStack item, boolean meta)
	{
		for (int i = 8; i < 20; i++) {
			if (inventory.items[i] != null && inventory.items[i].getItem() == item.getItem() && (!meta || inventory.items[i].getItemDamage() == item.getItemDamage()))
				return inventory.extractItem(i, 1, false);
		}
		return null;
	}
	
	private boolean remove(ItemStack item, int n)
	{
		for (int i = 8; i < 20 && n > 0; i++)
		{
			if (inventory.items[i] != null && inventory.items[i].isItemEqual(item))
			{
				if (inventory.items[i].getCount() > n)
				{
					inventory.items[i].shrink(n);
					return true;
				} else
				{
					n -= inventory.items[i].getCount();
					inventory.items[i] = null;
				}
			}
		}
		return n <= 0;
	}
	
	private void add(ItemStack item)
	{
		if (item == null) return;
		int p = 20;
		for (int i = 0; i < 4; i++)
		{
			if (inventory.items[i] != null && Utils.itemsEqual(inventory.items[i], item))
			{
				p = 8;
				break;
			}
		}
		int[] s = new int[32 - p];
		for (int i = 0; i < s.length; i++) s[i] = p + i;
		item = ItemFluidUtil.putInSlots(inventory, item, s);
		if (item != null) {
			world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, item));
			invFull = true;
		}
	}

	@Override
	public int[] getOperatingArea() 
	{
		return area;
	}

	@Override
	public void updateArea(int[] area) 
	{
		boolean b = false;
		for (int i = 0; i < this.area.length && !b;i++){b = area[i] != this.area[i];}
		if (b) {
			this.area = area;
		}
		this.markUpdate();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		area = nbt.getIntArray("area");
		if (area.length != 6) area = new int[6];
		px = nbt.getInteger("px");
		py = nbt.getInteger("py");
		pz = nbt.getInteger("pz");
		storage = nbt.getFloat("storage");
		invFull = invFull();
		try {lastUser = new GameProfile(new UUID(nbt.getLong("lastUserID0"), nbt.getLong("lastUserID1")), nbt.getString("lastUser"));
		} catch (Exception e) {lastUser = defaultUser;}
		prot = null;
		this.setSlot(-1, 37, inventory.items[37]);
		this.setSlot(-1, 35, inventory.items[35]);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setIntArray("area", area);
		nbt.setInteger("px", px);
		nbt.setInteger("py", py);
		nbt.setInteger("pz", pz);
		nbt.setFloat("storage", storage);
		nbt.setString("lastUser", lastUser.getName());
		nbt.setLong("lastUserID0", lastUser.getId().getMostSignificantBits());
		nbt.setLong("lastUserID1", lastUser.getId().getLeastSignificantBits());
		return super.writeToNBT(nbt);
	}
		
	@Override
	public void initContainer(DataContainer cont) {
		TileContainer container = (TileContainer)cont;
		container.clickHandler = this;
		for (int i = 0; i < 8; i++)
			container.addItemSlot(new SlotHolo(inventory, i, 8 + 18 * i, 16, false, true));
		
		for (int k = 0; k < 2; k++)
			for (int j = 0; j < 3; j++)
				for (int i = 0; i < 4; i++)
					container.addItemSlot(new SlotItemHandler(inventory, 8 + i + j * 4 + k * 12, 8 + 18 * (i + 5 * k), 34 + 18 * j));
		
		container.addItemSlot(new SlotItemType(inventory, 37, 152, 16, new ItemStack(Objects.itemUpgrade)));
		
		container.addPlayerInventory(8, 104);
	}

	@Override
	public boolean transferStack(ItemStack item, int s, TileContainer container) {
		if (s < container.invPlayerS) container.mergeItemStack(item, container.invPlayerS, container.invPlayerE, false);
		else if (item.getItem() instanceof ItemItemUpgrade) container.mergeItemStack(item, 32, 33, false);
		else {
			int n = 20;
			for (int i = 0; i < 8; i++) {
				ItemStack st = inventory.items[i];
				if (st != null && st.isItemEqual(item)) {
					n = 8;
					break;
				}
			}
			container.mergeItemStack(item, n, 32, false);
		}
		return true;
	}

	@Override
	public int[] getBaseDimensions() 
	{
		return new int[]{12, 16, 12, 8, Config.Umax[1]};
	}

	@Override
	public boolean remoteOperation(BlockPos pos) {
		return true;
	}

	@Override
	public IOperatingArea getSlave() {
		return slave;
	}

	@Override
	public void setSlot(int g, int s, ItemStack item) {
		inventory.items[s] = item;
		if (s == 37) harvestFilter = item == null || !(item.getItem() instanceof ItemItemUpgrade) || item.getTagCompound() == null ? null : PipeUpgradeItem.load(item.getTagCompound());
		if (s == 32) this.markUpdate();
		else if (s == 35) {
			float u = energy.Ucap;
			energy = new PipeEnergy(Handler.Umax(this), Config.Rcond[1]);
			energy.Ucap = u;
		} else if (s == 36) {
			slave = ItemMachineSynchronizer.getLink(item, this);
		} else if (s == 33 || s == 34) {
			Handler.setCorrectArea(this, area, true);
		}
	}

	@Override
	public IItemHandler getUpgradeSlots() {
		return inventory.new SlotAccess(32, 5);
	}

}
