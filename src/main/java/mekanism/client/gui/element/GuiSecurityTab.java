package mekanism.client.gui.element;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketSecurityMode.SecurityModeMessage;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.security.SecurityFrequency;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.SecurityUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSecurityTab extends GuiElement
{
	public boolean isItem;
	
	public TileEntity tileEntity;

	public GuiSecurityTab(IGuiWrapper gui, TileEntity tile, ResourceLocation def)
	{
		super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiSecurityTab.png"), gui, def);

		tileEntity = tile;
	}
	
	public GuiSecurityTab(IGuiWrapper gui, ResourceLocation def)
	{
		super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiSecurityTab.png"), gui, def);
		
		isItem = true;
	}
	
	@Override
	public Rectangle4i getBounds(int guiWidth, int guiHeight)
	{
		return new Rectangle4i(guiWidth + 176, guiHeight + 32, 26, 26);
	}

	@Override
	public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight)
	{
		mc.renderEngine.bindTexture(RESOURCE);

		guiObj.drawTexturedRect(guiWidth + 176, guiHeight + 32, 0, 0, 26, 26);

		SecurityMode mode = getSecurity();
		SecurityFrequency freq = getFrequency();
		
		if(freq != null && freq.override)
		{
			mode = freq.securityMode;
		}
		
		int renderX = 26 + (18*mode.ordinal());

		if(getOwner() != null && getOwner().equals(mc.thePlayer.getName()) &&
				(getFrequency() == null || !getFrequency().override))
		{
			if(xAxis >= 179 && xAxis <= 197 && yAxis >= 36 && yAxis <= 54)
			{
				guiObj.drawTexturedRect(guiWidth + 179, guiHeight + 36, renderX, 0, 18, 18);
			}
			else {
				guiObj.drawTexturedRect(guiWidth + 179, guiHeight + 36, renderX, 18, 18, 18);
			}
		}
		else {
			guiObj.drawTexturedRect(guiWidth + 179, guiHeight + 36, renderX, 36, 18, 18);
		}

		mc.renderEngine.bindTexture(defaultLocation);
	}

	@Override
	public void renderForeground(int xAxis, int yAxis)
	{
		mc.renderEngine.bindTexture(RESOURCE);

		if(xAxis >= 179 && xAxis <= 197 && yAxis >= 36 && yAxis <= 54)
		{
			String securityDisplay = isItem ? SecurityUtils.getSecurityDisplay(getItem()) : SecurityUtils.getSecurityDisplay(tileEntity);
			String securityText = EnumColor.GREY + LangUtils.localize("gui.security") + ": " + securityDisplay;
			String ownerText = SecurityUtils.getOwnerDisplay(mc.thePlayer.getName(), getOwner());
			String overrideText = EnumColor.RED + "(" + LangUtils.localize("gui.overridden") + ")";
			
			if(isItem ? SecurityUtils.isOverridden(getItem()) : SecurityUtils.isOverridden(tileEntity))
			{
				displayTooltips(ListUtils.asList(securityText, ownerText, overrideText), xAxis, yAxis); 
			}
			else {
				displayTooltips(ListUtils.asList(securityText, ownerText), xAxis, yAxis); 
			}
		}

		mc.renderEngine.bindTexture(defaultLocation);
	}
	
	private SecurityFrequency getFrequency()
	{
		if(isItem)
		{
			if(getItem() == null || !(getItem().getItem() instanceof ISecurityItem))
			{
				mc.thePlayer.closeScreen();
				return null;
			}
			
			return SecurityUtils.getFrequency(getOwner());
		}
		else {
			return ((ISecurityTile)tileEntity).getSecurity().getFrequency();
		}
	}
	
	private SecurityMode getSecurity()
	{
		if(isItem)
		{
			if(getItem() == null || !(getItem().getItem() instanceof ISecurityItem))
			{
				mc.thePlayer.closeScreen();
				return SecurityMode.PUBLIC;
			}
			
			return ((ISecurityItem)getItem().getItem()).getSecurity(getItem());
		}
		else {
			return ((ISecurityTile)tileEntity).getSecurity().getMode();
		}
	}
	
	private String getOwner()
	{
		if(isItem)
		{
			if(getItem() == null || !(getItem().getItem() instanceof ISecurityItem))
			{
				mc.thePlayer.closeScreen();
				return null;
			}
			
			return ((ISecurityItem)getItem().getItem()).getOwner(getItem());
		}
		else {
			return ((ISecurityTile)tileEntity).getSecurity().getOwner();
		}
	}
	
	private ItemStack getItem()
	{
		return mc.thePlayer.getCurrentEquippedItem();
	}

	@Override
	public void preMouseClicked(int xAxis, int yAxis, int button) {}

	@Override
	public void mouseClicked(int xAxis, int yAxis, int button)
	{
		if(button == 0)
		{
			if(getOwner() != null && mc.thePlayer.getName().equals(getOwner()))
			{
				if(xAxis >= 179 && xAxis <= 197 && yAxis >= 36 && yAxis <= 54)
				{
					SecurityMode current = getSecurity();
					int ordinalToSet = current.ordinal() < (SecurityMode.values().length-1) ? current.ordinal()+1 : 0;
	
					SoundHandler.playSound("gui.button.press");
					
					if(isItem)
					{
						Mekanism.packetHandler.sendToServer(new SecurityModeMessage(SecurityMode.values()[ordinalToSet]));
					}
					else {
						Mekanism.packetHandler.sendToServer(new SecurityModeMessage(Coord4D.get(tileEntity), SecurityMode.values()[ordinalToSet]));
					}
				}
			}
		}
	}
}