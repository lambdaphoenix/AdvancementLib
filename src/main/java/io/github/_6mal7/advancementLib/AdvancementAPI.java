package io.github._6mal7.advancementLib;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;
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
 * API for registering and tracking custom or vanilla advancements in your plugin.
 *
 * <p>Use this API to define custom triggers and conditions for granting advancements to players.
 * All registration must go through the builder pattern.
 *
 * <h2>Example usage:</h2>
 *
 * <pre>{@code
 * AdvancementAPI api = new AdvancementAPI(plugin);
 * api.register(PlayerInteractEvent.class)
 *  .advancementKey("minecraft:adventure/trade")
 *  .condition((player, event) -> event.getAction() == Action.RIGHT_CLICK_BLOCK)
 *  .targetValue(5)
 *  .playerExtractor(event -> event.getPlayer())
 *  .build();
 * }</pre>
 *
 * @author 6mal7
 * @version 0.2.1
 * @since 0.1.0
 * @see AdvancementRegisterBuilder
 * @see PlayerExtractor
 * @see GrantMode
 */
public final class AdvancementAPI {
  private static final String ADVANCEMENT_API_KEY = "advancement_api";
  private final Plugin plugin;

  /**
   * Creates a new AdvancementAPI instance for your plugin.
   *
   * @param plugin your plugin instance (not {@code null})
   * @throws NullPointerException if plugin is {@code null}
   * @since 0.1.0
   * @see Plugin
   */
  public AdvancementAPI(Plugin plugin) {
    this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
  }

  /**
   * Starts a new {@link AdvancementRegisterBuilder} for the given event type.
   *
   * <p>This is the only supported way to register advancements.
   *
   * @param eventType the Bukkit event class to listen for
   * @param <E> the event type
   * @return a new builder instance
   * @since 0.2.0
   * @see AdvancementRegisterBuilder
   */
  public <E extends Event> AdvancementRegisterBuilder<E> register(Class<E> eventType) {
    return new AdvancementRegisterBuilder<E>(this).eventType(eventType);
  }

  /**
   * Registers an advancement trigger for the given event type.
   *
   * <p>This method is package-private and should only be called by {@link
   * AdvancementRegisterBuilder}.
   *
   * @param advancementKey the unique advancement key (e.g. "minecraft:adventure/trade")
   * @param eventType the Bukkit event class to listen for
   * @param condition a predicate that determines if the advancement should progress
   * @param targetValue the number of times the condition must be met before granting the
   *     advancement
   * @param playerExtractor a function to extract the Player from the event
   * @param grantMode how the advancement is granted (all at once or step by step)
   * @param increment function to compute progress increment from the event; may be {@code null}
   * @param <E> the event type
   * @throws IllegalArgumentException if any required parameter is null, targetValue < 1, or
   *     advancement does not exist
   * @since 0.1.0
   * @see GrantMode
   * @see PlayerExtractor
   * @see AdvancementRegisterBuilder
   */
  <E extends Event> void registerAdvancement(
      String advancementKey,
      Class<E> eventType,
      BiPredicate<Player, E> condition,
      int targetValue,
      Function<E, Player> playerExtractor,
      GrantMode grantMode,
      ToIntFunction<E> increment) {

    if (advancementKey == null
        || condition == null
        || targetValue < 1
        || playerExtractor == null
        || grantMode == null)
      throw new IllegalArgumentException("Invalid arguments for registering advancement.");
    if (getAdvancement(advancementKey) == null)
      throw new IllegalArgumentException("Advancement not found: " + advancementKey);
    Bukkit.getPluginManager()
        .registerEvent(
            eventType,
            new Listener() {},
            EventPriority.NORMAL,
            (listener, event) -> {
              if (!eventType.isInstance(event)) return;
              E e = eventType.cast(event);
              Player player = playerExtractor.apply(e);
              if (player == null || !condition.test(player, e)) return;
              NamespacedKey namespacedKey =
                  new NamespacedKey(ADVANCEMENT_API_KEY, advancementKey.replaceFirst(":", "."));
              int incrementValue = increment != null ? increment.applyAsInt(e) : 1;
              if (incrementValue <= 0) return;
              int value =
                  player
                          .getPersistentDataContainer()
                          .getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0)
                      + incrementValue;
              if (value == 0) return;
              switch (grantMode) {
                case ALL_AT_ONCE -> {
                  if (value >= targetValue) {
                    value = -1;
                    grantAdvancement(player, advancementKey);
                  }
                }
                case STEP_BY_STEP -> {
                  if (value >= targetValue) {
                    value -= targetValue;
                    grantCriterion(player, advancementKey);
                  }
                }
              }

              player
                  .getPersistentDataContainer()
                  .set(namespacedKey, PersistentDataType.INTEGER, value);
            },
            plugin);
  }

  /**
   * Returns the logger associated with the plugin using this API.
   * <p>
   * All log messages produced by this API or its components should use this logger,
   * ensuring they are properly prefixed and integrated with the implementing plugin's
   * logging system.
   * </p>
   *
   * @return the {@link java.util.logging.Logger} instance of the implementing plugin
   * @since 0.2.1
   */
  Logger getLogger() {
    return this.plugin.getLogger();
  }

  /**
   * Gets the current progress towards an advancement for a specific player.
   *
   * @param advancementKey the namespaced key of the advancement
   * @param player the player to check progress for
   * @return the number of times the condition has been met
   * @since 0.1.0
   */
  public int getProgress(String advancementKey, Player player) {
    NamespacedKey namespacedKey =
        new NamespacedKey(ADVANCEMENT_API_KEY, advancementKey.replaceFirst(":", "."));
    return player
        .getPersistentDataContainer()
        .getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0);
  }

  /**
   * Looks up a Bukkit Advancement by key.
   *
   * @param advancementKey the string key (e.g. "minecraft:adventure/trade")
   * @return the Advancement, or null if not found or key is invalid
   * @since 0.2.0
   * @see Advancement
   */
  private Advancement getAdvancement(String advancementKey) {
    NamespacedKey namespacedKey = NamespacedKey.fromString(advancementKey);
    if (namespacedKey == null) {
      plugin.getLogger().warning("Invalid advancement key: " + advancementKey);
      return null;
    }
    Advancement advancement = Bukkit.getAdvancement(namespacedKey);
    if (advancement == null) {
      plugin.getLogger().warning("Advancement not found: " + advancementKey);
    }
    return advancement;
  }

  /**
   * Grants all remaining criteria for the specified advancement to the player.
   *
   * @param player the player to grant the advancement to
   * @param advancementKey the namespaced key of the advancement
   * @since 0.1.0
   * @see Advancement
   * @see AdvancementProgress
   */
  private void grantAdvancement(Player player, String advancementKey) {
    Advancement advancement = getAdvancement(advancementKey);
    if (advancement == null) return;

    AdvancementProgress progress = player.getAdvancementProgress(advancement);
    for (String criterion : progress.getRemainingCriteria()) {
      progress.awardCriteria(criterion);
    }
  }

  /**
   * Grants a single criterion for the specified advancement to the player.
   *
   * @param player the player to grant the criterion to
   * @param advancementKey the namespaced key of the advancement
   * @since 0.2.0
   * @see Advancement
   * @see AdvancementProgress
   */
  private void grantCriterion(Player player, String advancementKey) {
    Advancement advancement = getAdvancement(advancementKey);
    if (advancement == null) return;

    AdvancementProgress progress = player.getAdvancementProgress(advancement);
    progress.getRemainingCriteria().stream().findFirst().ifPresent(progress::awardCriteria);
  }
}
