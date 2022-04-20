package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldguard.bukkit.event.Handleable;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class BlockListenerWG implements Listener
{
	@Getter private final WorldGuardExtraFlagsPlugin plugin;

	//TODO: Figure out something better for this
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockPlaceEvent(PlaceBlockEvent event){
    handleBlockEvent(event, event.getCause().getRootCause(), event.getEffectiveMaterial(), event.getBlocks(),Flags.ALLOW_BLOCK_PLACE,Flags.DENY_BLOCK_PLACE);
	}
  
  public void handleBlockEvent(Handleable event, Object cause, Material effectiveMaterial, List<Block> affectedBlocks, Flag allowFlag, Flag denyFlag){
    if(cause instanceof Player){
			Player player = (Player)cause;

			for(Block block : affectedBlocks){
				Material type = block.getType();
				if (type == Material.AIR)	type = effectiveMaterial;  //Workaround for https://github.com/aromaa/WorldGuardExtraFlagsPlugin/issues/12

				ApplicableRegionSet regions = plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

				Set<Material> allowBlockPlaceMaterials = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.ALLOW_BLOCK_PLACE);
        if(allowBlockPlaceMaterials !=null && !allowBlockPlaceMaterials.contains(type)){ //If there is a list of allowed materials, and it doesn't contain this block, deny the placement
          event.setResult(Event.Result.DENY);
        }
        
        Set<Material> denyBlockPlaceMaterials = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.DENY_BLOCK_PLACE); //If there is a list of denied materials, and it includes this block, deny the placement
        if (denyBlockPlaceMaterials != null && denyBlockPlaceMaterials.contains(type)){
          event.setResult(Event.Result.DENY);
          return;
        }
			}
		}
  }

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockBreakEvent(BreakBlockEvent event){
    handleBlockEvent(event, event.getCause().getRootCause(), event.getEffectiveMaterial(), event.getBlocks(),Flags.ALLOW_BLOCK_BREAK,Flags.DENY_BLOCK_BREAK);
	}
  
}
