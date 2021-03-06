package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.filter.filters.ModItemFilter;
import crazypants.enderio.base.filter.network.PacketFilterUpdate;
import crazypants.enderio.base.filter.network.PacketModItemFilter;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ModItemFilterGui extends AbstractGuiItemFilter {

  private static final int MOD_NAME_COLOR = ColorUtil.getRGB(Color.white);

  private final ModItemFilter filter;

  private final Rectangle[] inputBounds;

  private final IconButton[] deleteButs;

  private final IconButton whiteListB;

  private final int inputOffsetX;
  private final int tfWidth;

  public ModItemFilterGui(@Nonnull InventoryPlayer playerInv, @Nonnull ContainerFilter filterContainer, TileEntity te) {
    super(playerInv, filterContainer, te, "mod_item_filter");

    filter = (ModItemFilter) filterContainer.getItemFilter();
    inputOffsetX = 50;
    tfWidth = 96;

    inputBounds = new Rectangle[] { new Rectangle(inputOffsetX, 46, 16, 16), new Rectangle(inputOffsetX, 68, 16, 16), new Rectangle(inputOffsetX, 90, 16, 16) };

    deleteButs = new IconButton[inputBounds.length];
    for (int i = 0; i < deleteButs.length; i++) {
      Rectangle r = inputBounds[i];
      IconButton but = new IconButton(this, FilterGuiUtil.nextButtonId(), r.x + 19, r.y, IconEIO.MINUS);
      deleteButs[i] = but;
    }

    whiteListB = new IconButton(this, -1, inputOffsetX - 19, 90, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
  }

  @Override
  public void updateButtons() {
    super.updateButtons();
    for (IconButton but : deleteButs) {
      but.onGuiInit();
    }

    whiteListB.onGuiInit();
    if (filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_BLACKLIST.get());
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(Lang.GUI_ITEM_FILTER_WHITELIST.get());
    }
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    for (int i = 0; i < deleteButs.length; i++) {
      IconButton but = deleteButs[i];
      if (but.id == guiButton.id) {
        setMod(i, ItemStack.EMPTY);
        return;
      }
    }
    if (guiButton == whiteListB) {
      filter.setBlacklist(!filter.isBlacklist());
      sendFilterChange();
    }
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    for (int i = 0; i < inputBounds.length; i++) {
      String mod = filter.getModAt(i);
      if (mod != null) {
        Rectangle r = inputBounds[i];
        mod = fr.trimStringToWidth(mod, tfWidth - 6);
        fr.drawStringWithShadow(mod, getGuiLeft() + r.x + 41, getGuiTop() + r.y + 4, MOD_NAME_COLOR);
      }
    }
  }

  @Override
  public void mouseClicked(int x, int y, int par3) throws IOException {
    super.mouseClicked(x, y, par3);
    ItemStack st = Minecraft.getMinecraft().player.inventory.getItemStack();
    if (st.isEmpty()) {
      return;
    }

    int xOffset = getGuiLeft();
    int yOffset = getGuiTop();

    for (int i = 0; i < inputBounds.length; i++) {
      Rectangle bound = inputBounds[i];
      if (bound.contains(x - xOffset, y - yOffset)) {
        setMod(i, st);
      }
    }
  }

  private void setMod(int i, @Nonnull ItemStack st) {
    String mod = filter.setMod(i, st);
    PacketHandler.INSTANCE
        .sendToServer(new PacketModItemFilter(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1(), i, mod));

  }

  private void sendFilterChange() {
    updateButtons();
    PacketHandler.INSTANCE
        .sendToServer(new PacketFilterUpdate(filterContainer.getTileEntity(), filter, filterContainer.filterIndex, filterContainer.getParam1()));
  }

}
