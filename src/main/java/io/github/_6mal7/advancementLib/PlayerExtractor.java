package io.github._6mal7.advancementLib;

import java.util.function.BiPredicate;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Functional interface for extracting a {@link Player} from a Bukkit {@link Event}.
 * <p>
 * Used by {@link AdvancementAPI#registerAdvancement} to determine which player is associated with a given event.
 *
 * <p>
 * <b>Supported Events in {@code getDefaultPlayerExtractor}:</b>
 * <ul>
 *   <li>{@link PlayerJoinEvent} - returns the joining player</li>
 *   <li>{@link BlockBreakEvent} - returns the player breaking the block</li>
 *   <li>{@link PlayerInteractEvent} - returns the interacting player</li>
 * </ul>
 * For other event types, you may provide your own extractor or extend this method.
 * </p>
 *
 * @param <E> the event type
 *
 * @see AdvancementAPI#registerAdvancement(String, Class, BiPredicate, int)
 * @see AdvancementAPI#registerAdvancement(String, Class, BiPredicate, int, Function) 
 * @see PlayerExtractor#getDefaultPlayerExtractor(Class)
 *
 * @author 6mal7
 * @version 0.1.0
 * @since 0.1.0
 * @see AdvancementAPI
 */
@FunctionalInterface
public interface PlayerExtractor<E extends Event> extends Function<E, Player> {

  /**
   * Returns a default {@link PlayerExtractor} for common Bukkit events.
   * <p>
   * Currently supported:
   * <ul>
   *   <li>{@link PlayerJoinEvent}</li>
   *   <li>{@link BlockBreakEvent}</li>
   *   <li>{@link PlayerInteractEvent}</li>
   * </ul>
   * For other event types, this method returns an extractor that always returns {@code null}.
   * You may implement your own extractor for custom events.
   *
   * @param eventType the event class
   * @param <E> the event type
   * @return a {@link PlayerExtractor} for the given event type, or {@code null}-extractor if unsupported
   *
   * @since 0.1.0
   */
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
