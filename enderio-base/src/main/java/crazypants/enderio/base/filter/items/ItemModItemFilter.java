package crazypants.enderio.base.filter.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.filters.ModItemFilter;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.gui.ModItemFilterGui;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  public static ItemModItemFilter create(@Nonnull IModObject modObject) {
    return new ItemModItemFilter(modObject);
  }

  protected ItemModItemFilter(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IItemFilter createFilterFromStack(@Nonnull ItemStack stack) {
    IItemFilter filter = new ModItemFilter();
    if (NbtValue.FILTER.hasTag(stack)) {
      filter.readFromNBT(NbtValue.FILTER.getTag(stack));
    }
    return filter;
  }

  // @Override
  // @SideOnly(Side.CLIENT)
  // public void registerIcons(IIconRegister IIconRegister) {
  // itemIcon = IIconRegister.registerIcon("enderio:modItemFilter");
  // }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (FilterRegistry.isFilterSet(stack)) {
      if (SpecialTooltipHandler.showAdvancedTooltips()) {
        tooltip.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.configured"));
        tooltip.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));
      }
    }
  }

  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return new ModItemFilterGui(player.inventory, new ContainerFilter(player.inventory, param1, (TileEntityBase) world.getTileEntity(pos), facing),
        world.getTileEntity(pos));
  }

}
