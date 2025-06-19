package io.github._6mal7.advancementLib;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Fluent Builder for registering custom advancements in your plugin.
 *
 * <p>Allows you to specify all the details for your advancement, including the event, conditions,
 * progress targets, and how criteria are granted.
 *
 * <h2>Required parameters:</h2>
 *
 * <ul>
 *   <li>{@code advancementKey} (unique string identifier)
 *   <li>{@code eventType} (class of the Bukkit event)
 * </ul>
 *
 * These must be set before calling {@link #build()}.
 *
 * <h2>Optional parameters:</h2>
 *
 * <ul>
 *   <li>{@code condition} - predicate for event acceptance (defaults to always true)
 *   <li>{@code playerExtractor} - function to extract the player from the event (defaults to {@link
 *       PlayerExtractor#getDefaultPlayerExtractor(Class, Logger)} if available)
 *   <li>{@code increment} - function to determine progress increment (if {@code null}, defaults to
 *       1 per event; otherwise, uses the provided function)
 *   <li>{@code targetValue} - required progress to complete (default: 1, must be at least 1)
 *   <li>{@code grantMode} - how the advancement is granted (default: {@link GrantMode#ALL_AT_ONCE})
 * </ul>
 *
 * <p><b>Note:</b> The {@link PlayerExtractor} utility provides default extractors for common Bukkit
 * events. For custom events, you may supply your own extractor.
 *
 * @param <E> the event type
 * @author 6mal7
 * @version 0.2.1
 * @since 0.2.0
 * @see AdvancementAPI
 * @see PlayerExtractor
 * @see GrantMode
 */
public class AdvancementRegisterBuilder<E extends Event> {
  private final AdvancementAPI api;
  private String advancementKey;
  private Class<E> eventType;
  private BiPredicate<Player, E> condition;
  private int targetValue = 1;
  private Function<E, Player> playerExtractor;
  private GrantMode grantMode = GrantMode.ALL_AT_ONCE;
  private ToIntFunction<E> increment;

  /**
   * Start building a new advancement.
   *
   * @param api the AdvancementAPI instance; must not be {@code null}
   * @throws NullPointerException if {@code api} is {@code null}
   * @since 0.2.0
   * @see AdvancementAPI
   */
  public AdvancementRegisterBuilder(AdvancementAPI api) {
    this.api = Objects.requireNonNull(api, "AdvancementAPI cannot be null");
  }

  /**
   * Start building a new advancement with a specific key.
   *
   * @param api the AdvancementAPI instance; must not be {@code null}
   * @param advancementKey the unique name for this advancement; must not be {@code null}
   * @since 0.2.0
   * @see AdvancementAPI
   */
  public AdvancementRegisterBuilder(AdvancementAPI api, String advancementKey) {
    this(api);
    this.advancementKey(advancementKey);
  }

  /**
   * Start building a new advancement with a specific key and event type.
   *
   * @param api the AdvancementAPI instance; must not be {@code null}
   * @param advancementKey the unique name for this advancement; must not be {@code null}
   * @param eventType the event type that triggers this advancement; must not be {@code null}
   * @since 0.2.0
   * @see AdvancementAPI
   */
  public AdvancementRegisterBuilder(AdvancementAPI api, String advancementKey, Class<E> eventType) {
    this(api);
    this.advancementKey(advancementKey);
    this.eventType(eventType);
  }

  /**
   * Sets the unique identifier for this advancement.
   *
   * @param advancementKey the advancement name; must not be {@code null}
   * @return this builder
   * @throws NullPointerException if {@code advancementKey} is {@code null}
   * @since 0.2.0
   */
  public AdvancementRegisterBuilder<E> advancementKey(String advancementKey) {
    this.advancementKey = Objects.requireNonNull(advancementKey);
    return this;
  }

  /**
   * Sets the event type that triggers this advancement.
   *
   * @param eventType the event class; must not be {@code null}
   * @return this builder
   * @throws NullPointerException if {@code eventType} is {@code null}
   * @since 0.2.0
   * @see Event
   */
  public AdvancementRegisterBuilder<E> eventType(Class<E> eventType) {
    this.eventType = Objects.requireNonNull(eventType);
    return this;
  }

  /**
   * Sets the condition that must be true for the advancement to progress.
   *
   * <p>If not set, defaults to always {@code true}.
   *
   * @param condition a predicate {@code (player, event) -> true} if this event should count
   * @return this builder
   * @since 0.2.0
   * @see BiPredicate
   */
  public AdvancementRegisterBuilder<E> condition(BiPredicate<Player, E> condition) {
    this.condition = condition;
    return this;
  }

  /**
   * Sets how many times the event must occur before the advancement is granted.
   *
   * <p>Must be at least 1. Default is 1.
   *
   * @param targetValue the number of times the event must occur
   * @return this builder
   * @since 0.2.0
   */
  public AdvancementRegisterBuilder<E> targetValue(int targetValue) {
    this.targetValue = targetValue;
    return this;
  }

  /**
   * Sets a custom function to extract the player from the event.
   *
   * <p>If not set, a default extractor will be used if available for the event type (see {@link
   * PlayerExtractor#getDefaultPlayerExtractor(Class, Logger)}).
   *
   * @param playerExtractor function to extract the player from the event
   * @return this builder
   * @since 0.2.0
   * @see PlayerExtractor
   * @see Function
   */
  public AdvancementRegisterBuilder<E> playerExtractor(Function<E, Player> playerExtractor) {
    this.playerExtractor = playerExtractor;
    return this;
  }

  /**
   * Sets how criteria are granted to the player
   *
   * <p>Defaults to {@link GrantMode#ALL_AT_ONCE}.
   *
   * @param grantMode the grant mode; must not be {@code null}
   * @return this builder
   * @throws NullPointerException if {@code grantMode} is {@code null}
   * @since 0.2.0
   * @see GrantMode
   */
  public AdvancementRegisterBuilder<E> grantMode(GrantMode grantMode) {
    this.grantMode = Objects.requireNonNull(grantMode, "GrantMode cannot be null");
    return this;
  }

  /**
   * Sets how much progress each event should add.
   *
   * <p>If not set, each event increases progress by 1.
   *
   * @param increment function to determine progress increment
   * @return this builder
   * @since 0.2.0
   * @see ToIntFunction
   */
  public AdvancementRegisterBuilder<E> increment(ToIntFunction<E> increment) {
    this.increment = increment;
    return this;
  }

  /**
   * Registers the advancement with the specified configuration.
   *
   * <p>You must set both {@code advancementKey} and {@code eventType} before calling this.
   *
   * @throws IllegalArgumentException if required fields are missing or invalid
   * @since 0.2.0
   * @see AdvancementAPI#registerAdvancement(String, Class, BiPredicate, int, Function, GrantMode,
   *     ToIntFunction)
   */
  public void build() {
    if (this.advancementKey == null)
      throw new IllegalArgumentException("Advancement name must be set");
    if (this.eventType == null) throw new IllegalArgumentException("Event type must be set");
    if (this.targetValue < 1) throw new IllegalArgumentException("Target value must be at least 1");

    if (this.condition == null) {
      this.condition = (player, event) -> true;
    }
    if (this.playerExtractor == null) {
      this.playerExtractor =
          PlayerExtractor.getDefaultPlayerExtractor(this.eventType, api.getLogger());
    }

    this.api.registerAdvancement(
        this.advancementKey,
        this.eventType,
        this.condition,
        this.targetValue,
        this.playerExtractor,
        this.grantMode,
        this.increment);
  }
}
