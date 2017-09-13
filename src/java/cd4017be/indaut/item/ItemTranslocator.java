package cd4017be.indaut.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import cd4017be.lib.DefaultItem;
import cd4017be.lib.MovedBlock;

public class ItemTranslocator extends DefaultItem {

	public ItemTranslocator(String id) {
		super(id);
		this.setMaxStackSize(1);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		if (item.getTagCompound() == null) {
			return super.getItemStackDisplayName(item) + " (Empty)";
		} else {
			Block block = Block.getBlockById(item.getTagCompound().getShort("id"));
			return super.getItemStackDisplayName(item) + " (" + block.getLocalizedName() + ")";
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing s, float X, float Y, float Z) {
		ItemStack item = player.getHeldItem(hand);
		if (item.getTagCompound() == null) {
			if (item.getCount() == 0) return EnumActionResult.FAIL;
			else if (!player.canPlayerEdit(pos, s, item)) return EnumActionResult.FAIL;
			else if (world.isRemote) return EnumActionResult.SUCCESS;
			MovedBlock pickup = MovedBlock.get(world, pos);
			if ((new MovedBlock(Blocks.AIR.getDefaultState(), null)).set(world, pos)) {
				//world.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), pickup.block.getBlock().getStepSound().getPlaceSound(), null, (pickup.block.getBlock().getStepSound().getVolume() + 1.0F) / 2.0F, pickup.block.getBlock().getStepSound().getPitch() * 0.8F, bFull3D);
				item.setTagCompound(new NBTTagCompound());
				item.getTagCompound().setShort("id", (short)Block.getStateId(pickup.block));
				if (pickup.nbt != null) item.getTagCompound().setTag("data", pickup.nbt);
			}
			return EnumActionResult.SUCCESS;
		} else {
			IBlockState obj = Block.getStateById(item.getTagCompound().getShort("id"));
			if (obj == Blocks.AIR) return EnumActionResult.FAIL;
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (state == Blocks.SNOW_LAYER.getDefaultState()) s = EnumFacing.UP;
			else if (block != Blocks.VINE && block != Blocks.TALLGRASS && block != Blocks.DEADBUSH && !block.isReplaceable(world, pos)) {
				pos = pos.offset(s);
			}
			if (item.getCount() == 0) return EnumActionResult.FAIL;
			else if (!player.canPlayerEdit(pos, s, item)) return EnumActionResult.FAIL;
			else if (pos.getY() == 255 && obj.getMaterial().isSolid()) return EnumActionResult.FAIL;
			else if (world.canBlockBePlaced(obj.getBlock(), pos, false, s, player, item)) {
				MovedBlock placement = new MovedBlock(obj, item.getTagCompound().hasKey("data") ? item.getTagCompound().getCompoundTag("data") : null);
				if (placement.set(world, pos)) {
					//world.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), obj.getBlock().stepSound.getPlaceSound(), (obj.getBlock().stepSound.getVolume() + 1.0F) / 2.0F, obj.getBlock().stepSound.getFrequency() * 0.8F);
					item.setTagCompound(null);
				}
				return EnumActionResult.SUCCESS;
			} else return EnumActionResult.FAIL;
		}
	}

}
