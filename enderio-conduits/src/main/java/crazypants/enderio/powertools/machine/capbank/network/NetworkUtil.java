package crazypants.enderio.powertools.machine.capbank.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class NetworkUtil {

  private static AtomicInteger nextID = new AtomicInteger((int) (Math.random() * 1000));

  /**
   * Ensures a capacitor bank has a network attached to it
   * 
   * @param cap
   *          The cap bank to ensure
   * @return true if a new network was created
   */
  public static boolean ensureValidNetwork(TileCapBank cap) {
    World world = cap.getWorld();
    Collection<TileCapBank> neighbours = getNeigbours(cap);
    if (reuseNetwork(cap, neighbours, world)) {
      return false;
    }
    CapBankNetwork network = new CapBankNetwork(nextID.getAndIncrement());
    network.init(cap, neighbours, world);
    return true;
  }

  public static Collection<TileCapBank> getNeigbours(TileCapBank cap) {
    if (!cap.getType().isMultiblock()) {
      return Collections.emptyList();
    }
    Collection<TileCapBank> res = new ArrayList<TileCapBank>();
    getNeigbours(cap, res);
    return res;
  }

  public static void getNeigbours(TileCapBank cap, Collection<TileCapBank> res) {
    for (EnumFacing dir : EnumFacing.values()) {
      TileEntity te = cap.getWorld().getTileEntity(cap.getPos().offset(NullHelper.notnullJ(dir, "Enum.values()")));
      if (te instanceof TileCapBank) {
        TileCapBank neighbour = (TileCapBank) te;
        if (neighbour.canConnectTo(cap)) {
          res.add(neighbour);
        }
      }
    }
  }

  private static boolean reuseNetwork(TileCapBank cap, Collection<TileCapBank> neighbours, World world) {
    ICapBankNetwork network = null;
    for (TileCapBank conduit : neighbours) {
      if (network == null) {
        network = conduit.getNetwork();
      } else if (network != conduit.getNetwork()) {
        return false;
      }
    }
    if (network == null) {
      return false;
    }
    if (cap.setNetwork(network)) {
      network.addMember(cap);
      // network.notifyNetworkOfUpdate();
      return true;
    }
    return false;
  }

}
