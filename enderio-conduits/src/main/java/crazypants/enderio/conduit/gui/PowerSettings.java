package crazypants.enderio.conduit.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.init.ConduitObject;
import crazypants.enderio.conduit.lang.Lang;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.power.IPowerConduit;
import net.minecraft.client.gui.GuiButton;

public class PowerSettings extends BaseSettingsPanel {

  private static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();

  private IPowerConduit conduit;
  private RedstoneModeButton rsB;
  private ColorButton colorB;

  public PowerSettings(@Nonnull final GuiExternalConnection gui, @Nonnull IClientConduit con) {
    super(IconEIO.WRENCH_OVERLAY_POWER, ConduitObject.item_power_conduit.getUnlocalisedName(), gui, con, "power_settings");
    conduit = (IPowerConduit) con;

    int x = rightColumn;
    int y = customTop;

    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(@Nonnull RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        conduit.setExtractionRedstoneMode(mode, gui.getDir());
        if (curMode != mode) {
          PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
        }

      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return conduit.getExtractionRedstoneMode(gui.getDir());
      }

      @Override
      public boolean getRedstoneControlStatus() {
        return false;
      }
    });

    x += rsB.getWidth() + gap;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setToolTipHeading(Lang.GUI_SIGNAL_COLOR.get());
    colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());

  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON) {
      conduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
    }
  }

  @Override
  protected void initCustomOptions() {
    super.initCustomOptions();
    rsB.onGuiInit();
    colorB.onGuiInit();
  }

  @Override
  public void deactivate() {
    super.deactivate();
    rsB.detach();
    colorB.detach();
  }
}
