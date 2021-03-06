package mekanism.client.gui;

import mekanism.client.gui.element.GuiContainerEditMode;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.inventory.container.ContainerFluidTank;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFluidTank extends GuiMekanismTile<TileEntityFluidTank> {

    public GuiFluidTank(InventoryPlayer inventory, TileEntityFluidTank tile) {
        super(tile, new ContainerFluidTank(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiContainerEditMode(this, tileEntity, resource));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiFluidGauge(() -> tileEntity.fluidTank, GuiFluidGauge.Type.WIDE, this, resource, 48, 18));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 145, 18).with(SlotOverlay.INPUT));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 145, 50).with(SlotOverlay.OUTPUT));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer
              .drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2),
                    6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, ySize - 96 + 2, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(getGuiLocation());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    }
}