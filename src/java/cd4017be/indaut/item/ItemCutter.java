package cd4017be.indaut.item;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import cd4017be.api.energy.EnergyAutomation.EnergyItem;
import cd4017be.lib.BlockItemRegistry;

/**
 *
 * @author CD4017BE
 */
public class ItemCutter extends ItemEnergyCell {

	private final int energyUsage;
	private final float damageVsEntity;
	private final float breakSpeed;
	private final float dmgDelay;

	public ItemCutter(String id, int es, int eu, float ed, float bs, float dd) {
		super(id, es);
		this.energyUsage = eu;
		this.damageVsEntity = ed;
		this.breakSpeed = bs;
		this.dmgDelay = 20F / dd;
		this.setMaxStackSize(1);
		ItemStack item = new ItemStack(this);
		item.addEnchantment(Enchantments.SILK_TOUCH, 1);
		BlockItemRegistry.registerItemStack(item, getUnlocalizedName());
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(BlockItemRegistry.stack(getUnlocalizedName(), 1));
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack item) {
		return (state.getMaterial().isToolNotRequired() || state.getMaterial() == Material.WEB) && new EnergyItem(item, this, -1).getStorageI() >= this.energyUsage;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack item, World world, IBlockState b, BlockPos pos, EntityLivingBase entityLiving) {
		new EnergyItem(item, this, -1).addEnergyI(-this.energyUsage);
		return true;
	}

	@Override
	public float getStrVsBlock(ItemStack item, IBlockState state) {
		float str = this.canHarvestBlock(state, item) ? breakSpeed : 1F;
		return str;
	}

	@Override
	public boolean hitEntity(ItemStack item, EntityLivingBase entityLivingHit, EntityLivingBase par3EntityLiving) {
		EnergyItem energy = new EnergyItem(item, this, -1);
		if (energy.getStorageI() >= this.energyUsage) {
			entityLivingHit.attackEntityFrom(DamageSource.causeMobDamage(par3EntityLiving), this.damageVsEntity);
			entityLivingHit.hurtResistantTime = (int)((float)entityLivingHit.maxHurtResistantTime / 2F + this.dmgDelay);
			energy.addEnergyI(-this.energyUsage);
		}
		return true;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack item, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
		EnergyItem energy = new EnergyItem(item, this, -1);
		if (entity.world.isRemote || energy.getStorageI() < this.energyUsage) return false;
		if (entity instanceof IShearable) {
			IShearable target = (IShearable)entity;
			if (target.isShearable(item, entity.world, new BlockPos(entity.posX, entity.posY, entity.posZ))) {
				List<ItemStack> drops = target.onSheared(item, entity.world, new BlockPos(entity.posX, entity.posY, entity.posZ), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, item));
				Random rand = new Random();
				for(ItemStack stack : drops) {
					EntityItem ent = entity.entityDropItem(stack, 1.0F);
					ent.motionY += rand.nextFloat() * 0.05F;
					ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
				}
				energy.addEnergyI(-this.energyUsage);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack item, BlockPos pos, EntityPlayer player) {
		if (player.world.isRemote) return false;
		IBlockState state = player.world.getBlockState(pos);
		if (state.getBlock() instanceof IShearable && !state.getBlock().canSilkHarvest(player.world, pos, state, player)) {
			IShearable target = (IShearable)state.getBlock();
			if (target.isShearable(item, player.world, pos)) {
				List<ItemStack> drops = target.onSheared(item, player.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, item));
				Random rand = new Random();
				for(ItemStack stack : drops) {
					float f = 0.7F;
					double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.world, (double)pos.getX() + d, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
					entityitem.setNoPickupDelay();
					player.world.spawnEntity(entityitem);
				}
				new EnergyItem(item, this, -1).addEnergyI(-this.energyUsage);
			}
		}
		return false;
	}

}
