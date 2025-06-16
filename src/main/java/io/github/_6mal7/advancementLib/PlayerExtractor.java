package io.github._6mal7.advancementLib;

import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@FunctionalInterface
public interface PlayerExtractor<E extends Event> extends Function<E, Player> {
  static <E extends Event> PlayerExtractor<E> getDefaultPlayerExtractor(Class<E> eventType) {
    if (PlayerJoinEvent.class.isAssignableFrom(eventType)) {
      return event -> ((PlayerJoinEvent) event).getPlayer();
    }
    if (BlockBreakEvent.class.isAssignableFrom(eventType)) {
      return event -> ((BlockBreakEvent) event).getPlayer();
    }
    if (PlayerInteractEvent.class.isAssignableFrom(eventType)) {
      return event -> ((PlayerInteractEvent) event).getPlayer();
    }

    return event -> null;
  }
}
