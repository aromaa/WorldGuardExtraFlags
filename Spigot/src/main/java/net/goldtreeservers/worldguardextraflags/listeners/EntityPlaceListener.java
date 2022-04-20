package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

@RequiredArgsConstructor
public class EntityPlaceListener implements Listener
{
	@Getter private final WorldGuardExtraFlagsPlugin plugin;
  
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
  public void onEntityPlacement(EntityPlaceEvent e){
    handleEntityEvent(e.getPlayer(),e,e.getEntity());
  }
  
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
  public void onEntityHang(HangingPlaceEvent e){
    handleEntityEvent(e.getPlayer(),e, e.getEntity());
  }
  
  private void handleEntityEvent(Player playerWhoPlaced, Cancellable event, Entity entity){
    EntityType entityType= entity.getType();
    ApplicableRegionSet regions = plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(entity.getLocation());
    
    Set<EntityType> allowedEntityPlacements =  WorldGuardUtils.queryValue(playerWhoPlaced, entity.getWorld(), regions.getRegions(), Flags.ALLOW_ENTITY_PLACE);
    if(allowedEntityPlacements !=null && !allowedEntityPlacements.contains(entity.getType())){
      event.setCancelled(true);
      playerWhoPlaced.updateInventory();
    }
    
    Set<EntityType> deniedEntityPlacements = WorldGuardUtils.queryValue(playerWhoPlaced, entity.getWorld(), regions.getRegions(), Flags.DENY_ENTITY_PLACE); //If there is a list of denied materials, and it includes this block, deny the placement
    if (deniedEntityPlacements != null && deniedEntityPlacements.contains(entity.getType())){
      event.setCancelled(true);
      playerWhoPlaced.updateInventory();
    }
  }
  
}
