package io.github._6mal7.advancementLib;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * API for registering and managing custom or vanilla advancements in a Paper plugin.
 * Allows you to define custom triggers and conditions for granting advancements.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * AdvancementAPI api = new AdvancementAPI(plugin);
 * api.registerAdvancement(
 *     "minecraft:adventure/trade",
 *     EntityDeathEvent.class,
 *     (player, event) -> {
 *         if (!(event.getEntity() instanceof Villager)) return false;
 *         return (player != null);
 *     },
 *     1,
 *     event -> event.getEntity().getKiller()
 * );
 * }</pre>
 * </p>
 *
 * @author 6mal7
 * @version 0.1.0
 * @since 0.1.0
 * @see PlayerExtractor
 */
public final class AdvancementAPI {
  private static final String ADVANCEMENT_API_KEY = "advancement_api";
  private final Plugin plugin;

  /**
   * Constructs a new AdvancementAPI for the given plugin.
   *
   * @param plugin your plugin instance (not null)
   * @throws NullPointerException if plugin is null
   * @since 0.1.0
   */
  public AdvancementAPI(Plugin plugin) {
    this.plugin = Objects.requireNonNull(plugin);
  }

  /**
   * Registers an advancement trigger for the given event type and player extractor.
   *
   * @param advancementName the namespaced key of the advancement (e.g. "minecraft:adventure/trade")
   * @param eventType the Bukkit event class to listen for
   * @param condition a predicate that determines if the advancement should progress.
   *                  <p>
   *                  The predicate receives the Player and the event instance as arguments.
   *                  Return {@code true} to count this event towards the advancement, or {@code false} to ignore it.
   *                  <br>
   *                  <b>Example:</b>
   *                  <pre>{@code
   *  // Only count if the killed entity is a Villager and the player is not null
   *  (player, event) -> {
   *      if (!(event.getEntity() instanceof Villager)) return false;
   *      return (player != null);
   *  }
   *                  }</pre>
   * @param targetValue the number of times the condition must be met before granting the advancement
   * @param <E> the event type
   *
   * @since 0.1.0
   */
  public <E extends Event> void registerAdvancement(
      String advancementName,
      Class<E> eventType,
      BiPredicate<Player, E> condition,
      int targetValue) {
    this.registerAdvancement(
        advancementName,
        eventType,
        condition,
        targetValue,
        PlayerExtractor.getDefaultPlayerExtractor(eventType));
  }

  /**
   * Registers an advancement trigger for the given event type and player extractor.
   * <p>
   * The <b>condition</b> parameter is a predicate that determines if the advancement should progress.
   * It receives the Player and the event instance as arguments. Return {@code true} to count this event
   * towards the advancement, or {@code false} to ignore it.
   * <br><br>
   * <b>Example condition:</b>
   * <pre>{@code
   * // Only count if the killed entity is a Villager and the player is not null
   * (player, event) -> {
   *     if (!(event.getEntity() instanceof Villager)) return false;
   *     return (player != null);
   * }
   * }</pre>
   * <br>
   * The <b>playerExtractor</b> parameter is a function to extract the Player from the event.
   * Use this if the event does not directly provide a Player, or if you need custom logic.
   * <br><br>
   * <b>Example playerExtractor:</b>
   * <pre>{@code
   * // Get the killer of the entity (the player who killed the villager)
   * event -> event.getEntity().getKiller()
   * }</pre>
   * <br>
   * For a list of events with built-in support for player extraction, see
   * {@link PlayerExtractor#getDefaultPlayerExtractor(Class)}.
   *
   * @param advancementName the namespaced key of the advancement (e.g. "minecraft:adventure/trade")
   * @param eventType the Bukkit event class to listen for
   * @param condition a predicate that determines if the advancement should progress
   * @param targetValue the number of times the condition must be met before granting the advancement
   * @param playerExtractor a function to extract the Player from the event
   * @param <E> the event type
   *
   * @since 0.1.0
   */
  public <E extends Event> void registerAdvancement(
      String advancementName,
      Class<E> eventType,
      BiPredicate<Player, E> condition,
      int targetValue,
      Function<E, Player> playerExtractor) {
    Bukkit.getPluginManager()
        .registerEvent(
            eventType,
            new Listener() {},
            EventPriority.NORMAL,
            ((listener, event) -> {
              if (eventType.isInstance(event)) {
                E e = eventType.cast(event);
                Player player =
                    (playerExtractor != null)
                        ? playerExtractor.apply(e)
                        : PlayerExtractor.getDefaultPlayerExtractor(eventType).apply(e);
                if (player == null || !condition.test(player, e)) return;
                NamespacedKey namespacedKey =
                    new NamespacedKey(ADVANCEMENT_API_KEY, advancementName.replaceFirst(":", "."));
                int value =
                    player
                            .getPersistentDataContainer()
                            .getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0)
                        + 1;
                player
                    .getPersistentDataContainer()
                    .set(namespacedKey, PersistentDataType.INTEGER, value);
                if (value == targetValue) {
                  grantAdvancement(player, advancementName);
                }
              }
            }),
            plugin);
  }

  /**
   * Gets the current progress towards an advancement for a specific player.
   *
   * @param advancementName the namespaced key of the advancement
   * @param player the player to check progress for
   * @return the number of times the condition has been met
   *
   * @since 0.1.0
   */
  public int getProgress(String advancementName, Player player) {
    NamespacedKey namespacedKey =
        new NamespacedKey(ADVANCEMENT_API_KEY, advancementName.replaceFirst(":", "."));
    return player
        .getPersistentDataContainer()
        .getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0);
  }

  /**
   * Grants the specified advancement to the player by awarding all remaining criteria.
   *
   * @param player the player to grant the advancement to
   * @param advancementName the namespaced key of the advancement
   *
   * @since 0.1.0
   */
  private void grantAdvancement(Player player, String advancementName) {
    NamespacedKey namespacedKey = NamespacedKey.fromString(advancementName);
    if (namespacedKey == null) {
      plugin.getLogger().warning("Invalid advancement key: " + advancementName);
      return;
    }
    Advancement advancement = Bukkit.getAdvancement(namespacedKey);
    if (advancement == null) {
      plugin.getLogger().warning("Advancement not found: " + advancementName);
      return;
    }
    AdvancementProgress progress = player.getAdvancementProgress(advancement);
    for (String criterion : progress.getRemainingCriteria()) {
      progress.awardCriteria(criterion);
    }
  }
}
