package cd4017be.indaut.item;

import java.util.List;

import cd4017be.lib.DefaultItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 *
 * @author CD4017BE
 */
public class ItemTeleporterCoords extends DefaultItem {

	public ItemTeleporterCoords(String id) {
		super(id);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		if (item.getTagCompound() == null) return super.getItemStackDisplayName(item);
		else return super.getItemStackDisplayName(item) + " " + item.getTagCompound().getString("name");
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean b) {
		if (item.getTagCompound() != null) {
			list.add("x = " + item.getTagCompound().getInteger("x"));
			list.add("y = " + item.getTagCompound().getInteger("y"));
			list.add("z = " + item.getTagCompound().getInteger("z"));
		}
		super.addInformation(item, player, list, b);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack item = player.getHeldItem(hand);
		this.onItemUse(player, world, new BlockPos(player.posX, player.posY, player.posZ), hand, EnumFacing.DOWN, 0, 0, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float X, float Y, float Z) {
		ItemStack item = player.getHeldItem(hand);
		if (world.isRemote) return EnumActionResult.SUCCESS;
		if (item.getTagCompound() == null) item.setTagCompound(new NBTTagCompound());
		if (!player.isSneaking()) {
			item.getTagCompound().setInteger("pos.getX()", pos.getX());
			item.getTagCompound().setInteger("pos.getY()", pos.getY());
			item.getTagCompound().setInteger("pos.getZ()", pos.getZ());
			player.sendMessage(new TextComponentString("Target set to: pX=" + pos.getX() + " pY=" + pos.getY() + " pZ=" + pos.getZ()));
		} else {
			item.getTagCompound().setInteger("pos.getX()", pos.getX() - item.getTagCompound().getInteger("x"));
			item.getTagCompound().setInteger("pos.getY()", pos.getY() - item.getTagCompound().getInteger("y"));
			item.getTagCompound().setInteger("pos.getZ()", pos.getZ() - item.getTagCompound().getInteger("z"));
			player.sendMessage(new TextComponentString("Translation set to: dX=" + pos.getX() + " dY=" + pos.getY() + " dZ=" + pos.getZ()));
		}
		return EnumActionResult.SUCCESS;
	}

}
