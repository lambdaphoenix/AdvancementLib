package io.github._6mal7.advancementLib;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;
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
 * API for registering and managing custom or vanilla advancements in a Paper plugin. Allows you to
 * define custom triggers and conditions for granting advancements.
 *
 * <h3>Recommended usage (with builder):</h3>
 *
 * <pre>{@code
 * AdvancementAPI api = new AdvancementAPI(plugin);
 * api.newRegisterBuilder(PlayerInteractEvent.class)
 *    .advancementName("minecraft:adventure/trade")
 *    .condition((player, event) -> event.getAction() == Action.RIGHT_CLICK_BLOCK)
 *    .targetValue(5)
 *    .playerExtractor(event -> event.getPlayer())
 *    .register();
 * }</pre>
 *
 * <h3>Direct usage (not recommended):</h3>
 *
 * <pre>{@code
 * api.registerAdvancement(
 *     "minecraft:adventure/trade",
 *     PlayerInteractEvent.class,
 *     (player, event) -> event.getAction() == Action.RIGHT_CLICK_BLOCK,
 *     5,
 *     event -> event.getPlayer(),
 *     GrantMode.ALL_AT_ONCE,
 *     null
 * );
 * }</pre>
 *
 * @author 6mal7
 * @version 0.2.0
 * @since 0.1.0
 * @see AdvancementRegisterBuilder
 * @see PlayerExtractor
 * @see GrantMode
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
   * @see Plugin
   */
  public AdvancementAPI(Plugin plugin) {
    this.plugin = Objects.requireNonNull(plugin);
  }

  /**
   * Starts a new {@link AdvancementRegisterBuilder} for the given event type.
   *
   * <p>This is the recommended way to register advancements.
   *
   * @param eventType the Bukkit event class to listen for
   * @param <E> the event type
   * @return a new builder instance
   * @since 0.2.0
   * @see AdvancementRegisterBuilder
   */
  public <E extends Event> AdvancementRegisterBuilder<E> newRegisterBuilder(Class<E> eventType) {
    return new AdvancementRegisterBuilder<E>(this).eventType(eventType);
  }

  /**
   * Registers an advancement trigger for the given event type and player extractor.
   *
   * <p>Most users should use {@link #newRegisterBuilder(Class)} and {@link
   * AdvancementRegisterBuilder} instead. <br>
   * The <b>condition</b> parameter is a predicate that determines if the advancement should
   * progress. It receives the Player and the event instance as arguments. <br>
   * The <b>playerExtractor</b> parameter is a function to extract the Player from the event. For a
   * list of events with built-in support for player extraction, see {@link
   * PlayerExtractor#getDefaultPlayerExtractor(Class)}. <br>
   * The <b>increment</b> parameter determines how much progress is made per event. If {@code null},
   * progress increments by 1 per event.
   *
   * @param advancementKey the namespaced key of the advancement (e.g. "minecraft:adventure/trade")
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
  public <E extends Event> void registerAdvancement(
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
        || grantMode == null) throw new IllegalArgumentException();
    if (getAdvancement(advancementKey) == null) throw new IllegalArgumentException();
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
   * Gets the Bukkit Advancement object for the given key.
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
   * Grants the specified advancement to the player by awarding all remaining criteria.
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
