package io.github._6mal7.advancementLib;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Functional interface for extracting a {@link Player} from a Bukkit {@link Event}.
 *
 * <p>Used by {@link AdvancementAPI} and {@link AdvancementRegisterBuilder} to determine which player is associated
 * with a given event instance.
 * <p><b>Supported Events in {@code getDefaultPlayerExtractor}:</b>
 *
 * <ul>
 *   <li>{@link BlockBreakEvent}
 *   <li>{@link BlockPlaceEvent}
 *   <li>{@link PlayerInteractEvent}
 *   <li>{@link PlayerJumpEvent}
 * </ul>
 *
 * For other event types, you may provide your own extractor or extend this method. The returned extractor will return {@code null} if the event type is unsupported.
 *
 * @param <E> the event type
 * @author 6mal7
 * @version 0.2.0
 * @since 0.1.0
 * @see AdvancementAPI
 * @see AdvancementRegisterBuilder
 */
@FunctionalInterface
public interface PlayerExtractor<E extends Event> extends Function<E, Player> {

  /**
   * Returns a default {@link PlayerExtractor} for common Bukkit events.
   *
   * <p>Currently supported:
   *
   * <ul>
   *   <li>{@link BlockBreakEvent}
   *   <li>{@link BlockPlaceEvent}
   *   <li>{@link PlayerInteractEvent}
   *   <li>{@link PlayerJumpEvent}
   * </ul>
   *
   * For other event types, this method returns an extractor that always returns {@code null}. You
   * may implement your own extractor for custom events.
   *
   * @param eventType the event class
   * @param <E> the event type
   * @return a {@link PlayerExtractor} for the given event type, or {@code null}-extractor if
   *     unsupported
   * @since 0.1.0
   */
  static <E extends Event> PlayerExtractor<E> getDefaultPlayerExtractor(Class<E> eventType) {
    if (BlockBreakEvent.class.isAssignableFrom(eventType)) {
      return event -> ((BlockBreakEvent) event).getPlayer();
    }
    if (BlockPlaceEvent.class.isAssignableFrom(eventType)) {
      return event -> ((BlockPlaceEvent) event).getPlayer();
    }
    if (PlayerInteractEvent.class.isAssignableFrom(eventType)) {
      return event -> ((PlayerInteractEvent) event).getPlayer();
    }
    if (PlayerJumpEvent.class.isAssignableFrom(eventType)) {
      return event -> ((PlayerJumpEvent) event).getPlayer();
    }

    return event -> null;
  }
}
