package mekanism.generators.client.gui;

import java.util.Arrays;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiGauge.Type;
import mekanism.client.gui.element.GuiHeatInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.config.MekanismConfig.general;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.generators.common.inventory.container.ContainerHeatGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiHeatGenerator extends GuiMekanismTile<TileEntityHeatGenerator> {

    public GuiHeatGenerator(InventoryPlayer inventory, TileEntityHeatGenerator tile) {
        super(tile, new ContainerHeatGenerator(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> Arrays.asList(
              LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.producingEnergy)
                    + "/t",
              LangUtils.localize("gui.maxOutput") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxOutput())
                    + "/t"), this, resource));
        addGuiElement(new GuiFluidGauge(() -> tileEntity.lavaTank, Type.WIDE, this, resource, 55, 18));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 16, 34));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 142, 34).with(SlotOverlay.POWER));
        addGuiElement(new GuiHeatInfo(() -> {
            TemperatureUnit unit = TemperatureUnit.values()[general.tempUnit.ordinal()];
            String transfer = UnitDisplayUtils.getDisplayShort(tileEntity.lastTransferLoss, false, unit);
            String environment = UnitDisplayUtils.getDisplayShort(tileEntity.lastEnvironmentLoss, false, unit);
            return Arrays.asList(LangUtils.localize("gui.transferred") + ": " + transfer + "/t",
                  LangUtils.localize("gui.dissipated") + ": " + environment + "/t");
        }, this, resource));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), 45, 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
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
        return MekanismUtils.getResource(ResourceType.GUI, "GuiHeatGenerator.png");
    }
}