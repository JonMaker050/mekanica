package mekanism.common.item;

import mekanism.api.Chunk3D;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSeismicReader extends ItemEnergized
{
	public static final double ENERGY_USAGE = 250;
	
	public ItemSeismicReader(int id)
	{
		super(id, 12000);
	}
	
	@Override
	public boolean canSend(ItemStack itemStack)
	{
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		Chunk3D chunk = new Chunk3D(entityplayer);
		
		if(!world.isRemote)
		{
			if(getEnergy(itemstack) < ENERGY_USAGE)
			{
				entityplayer.addChatMessage(EnumColor.DARK_BLUE + "[Mekanism] " + EnumColor.RED + MekanismUtils.localize("tooltip.seismicReader.needsEnergy"));
			}
			else if(!MekanismUtils.isChunkVibrated(chunk))
			{
				entityplayer.addChatMessage(EnumColor.DARK_BLUE + "[Mekanism] " + EnumColor.RED + MekanismUtils.localize("tooltip.seismicReader.noVibrations"));
			}
			
			if(!entityplayer.capabilities.isCreativeMode)
			{
				setEnergy(itemstack, getEnergy(itemstack)-ENERGY_USAGE);
			}
		}
		
		entityplayer.openGui(Mekanism.instance, 38, world, (int)entityplayer.posX, (int)entityplayer.posY, (int)entityplayer.posZ);

		return itemstack;
	}
}