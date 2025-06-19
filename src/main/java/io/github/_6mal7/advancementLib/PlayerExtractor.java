package io.github._6mal7.advancementLib;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import java.util.function.Function;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Functional interface for extracting a {@link Player} from a Bukkit {@link Event}.
 *
 * <p>Used by {@link AdvancementAPI} and {@link AdvancementRegisterBuilder} to determine which
 * player is associated with a given event instance. For default supported event types see {@link
 * #getDefaultPlayerExtractor(Class, Logger)}
 *
 * @param <E> the event type
 * @author 6mal7
 * @version 0.2.1
 * @since 0.1.0
 * @see AdvancementAPI
 * @see AdvancementRegisterBuilder
 */
@FunctionalInterface
public interface PlayerExtractor<E extends Event> extends Function<E, Player> {

  /**
   * Provides a default {@link PlayerExtractor} for common Bukkit events.
   *
   * <p>Supported events:
   *
   * <ul>
   *   <li>{@link BlockBreakEvent}
   *   <li>{@link BlockPlaceEvent}
   *   <li>{@link PlayerInteractEvent}
   *   <li>{@link PlayerJumpEvent}
   * </ul>
   *
   * If no suitable extractor is found for the given event type, an informational message is logged
   * and the returned extractor will always return {@code null}.
   *
   * @param eventType the event class
   * @param logger the logger to use for informational messages
   * @param <E> the event type
   * @return a {@link PlayerExtractor} for the given event type, or {@code null}-extractor if
   *     unsupported
   * @since 0.1.0
   */
  static <E extends Event> PlayerExtractor<E> getDefaultPlayerExtractor(
      Class<E> eventType, Logger logger) {
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

    logger.warning(
        "[AdvancementLib] No player extractor found for event type: " + eventType.getName());
    return event -> null;
  }
}
