package io.github._6mal7.advancementLib;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Builder for registering custom advancements in your plugin.
 *
 * <p>This builder allows you to fluently configure and register advancements that are triggered by
 * Bukkit events.
 *
 * <h3>Required parameters:</h3>
 *
 * <ul>
 *   <li>{@code advancementKey} (unique string identifier)
 *   <li>{@code eventType} (class of the Bukkit event)
 * </ul>
 *
 * These must be set before calling {@link #register()}.
 *
 * <h3>Optional parameters:</h3>
 *
 * <ul>
 *   <li>{@code condition} - predicate for event acceptance (defaults to always true)
 *   <li>{@code playerExtractor} - function to extract the player from the event (defaults to {@link
 *       PlayerExtractor#getDefaultPlayerExtractor(Class)} if available)
 *   <li>{@code increment} - function to determine progress increment (if {@code null}, defaults to
 *       1 per event; otherwise, uses the provided function)
 *   <li>{@code targetValue} - required progress to complete (default: 1, must be at least 1)
 *   <li>{@code grantMode} - how the advancement is granted (default: {@link GrantMode#ALL_AT_ONCE})
 * </ul>
 *
 * <p><b>Note:</b> The {@link PlayerExtractor} utility provides default extractors for common Bukkit
 * events. For custom events, you may supply your own extractor.
 *
 * @author 6mal7
 * @version 0.2.0
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
   * Create a new builder for registering advancements.
   *
   * @param api the AdvancementAPI instance; must not be {@code null}
   * @throws NullPointerException if {@code api} is {@code null}
   * @since 0.2.0
   * @see AdvancementAPI
   */
  public AdvancementRegisterBuilder(AdvancementAPI api) {
    this.api = Objects.requireNonNull(api);
  }

  /**
   * Create a new builder with a specified advancement name.
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
   * Create a new builder with a specified advancement name and event type.
   *
   * @param api the AdvancementAPI instance; must not be {@code null}
   * @param advancementKey the unique name for this advancement; must not be {@code null}
   * @param eventType the event type that triggers this advancement; must not be {@code null}
   * @since 0.2.0
   * @see AdvancementAPI
   * @see Event
   */
  public AdvancementRegisterBuilder(
      AdvancementAPI api, String advancementKey, Class<E> eventType) {
    this(api);
    this.advancementKey(advancementKey);
    this.eventType(eventType);
  }

  /**
   * Sets the unique name for this advancement.
   *
   * @param advancementKey the advancement name; must not be {@code null}
   * @return this builder for chaining
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
   * @return this builder for chaining
   * @throws NullPointerException if {@code eventType} is {@code null}
   * @since 0.2.0
   * @see Event
   */
  public AdvancementRegisterBuilder<E> eventType(Class<E> eventType) {
    this.eventType = Objects.requireNonNull(eventType);
    return this;
  }

  /**
   * Sets the condition that must be met for the advancement to progress.
   *
   * <p>If not set, defaults to always {@code true}.
   *
   * @param condition a predicate taking the player and event; may be {@code null}
   * @return this builder for chaining
   * @since 0.2.0
   * @see BiPredicate
   */
  public AdvancementRegisterBuilder<E> condition(BiPredicate<Player, E> condition) {
    this.condition = condition;
    return this;
  }

  /**
   * Sets the target value required to complete the advancement.
   *
   * <p>Must be at least 1. Default is 1.
   *
   * @param targetValue the required progress value
   * @return this builder for chaining
   * @since 0.2.0
   */
  public AdvancementRegisterBuilder<E> targetValue(int targetValue) {
    this.targetValue = targetValue;
    return this;
  }

  /**
   * Sets the function to extract the player from the event.
   *
   * <p>If not set, a default extractor will be used if available for the event type (see {@link
   * PlayerExtractor#getDefaultPlayerExtractor(Class)}).
   *
   * @param playerExtractor function to extract the player from the event; may be {@code null}
   * @return this builder for chaining
   * @since 0.2.0
   * @see PlayerExtractor
   * @see Function
   */
  public AdvancementRegisterBuilder<E> playerExtractor(Function<E, Player> playerExtractor) {
    this.playerExtractor = playerExtractor;
    return this;
  }

  /**
   * Sets the grant mode for this advancement.
   *
   * <p>Defaults to {@link GrantMode#ALL_AT_ONCE}.
   *
   * @param grantMode the grant mode; must not be {@code null}
   * @return this builder for chaining
   * @throws NullPointerException if {@code grantMode} is {@code null}
   * @since 0.2.0
   * @see GrantMode
   */
  public AdvancementRegisterBuilder<E> grantMode(GrantMode grantMode) {
    this.grantMode = Objects.requireNonNull(grantMode, "GrantMode cannot be null");
    return this;
  }

  /**
   * Sets the function that determines how much progress is made per event.
   *
   * <p>This is optional and may be {@code null}. If {@code null}, the progress increment defaults
   * to 1 per event. Otherwise, the provided function determines the increment based on the event.
   *
   * @param increment function to compute progress increment from the event; may be {@code null}
   * @return this builder for chaining
   * @since 0.2.0
   * @see ToIntFunction
   */
  public AdvancementRegisterBuilder<E> increment(ToIntFunction<E> increment) {
    this.increment = increment;
    return this;
  }

  /**
   * Registers the advancement with the configured parameters.
   *
   * <p><b>Required:</b> {@code advancementKey} and {@code eventType} must be set before calling
   * this method. <br>
   * If {@code condition} is not set, defaults to always {@code true}. <br>
   * If {@code playerExtractor} is not set, a default extractor will be used if available (see
   * {@link PlayerExtractor#getDefaultPlayerExtractor(Class)}). <br>
   * If {@code increment} is not set, the progress increment defaults to 1 per event.
   *
   * @throws IllegalArgumentException if required fields are missing or invalid
   * @since 0.2.0
   * @see AdvancementAPI#registerAdvancement(String, Class, BiPredicate, int, Function, GrantMode,
   *     ToIntFunction)
   */
  public void register() {
    if (this.advancementKey == null)
      throw new IllegalArgumentException("Advancement name must be set");
    if (this.eventType == null) throw new IllegalArgumentException("Event type must be set");
    if (this.targetValue < 1) throw new IllegalArgumentException("Target value must be at least 1");

    if (this.condition == null) {
      this.condition = (player, event) -> true;
    }
    if (this.playerExtractor == null) {
      this.playerExtractor = PlayerExtractor.getDefaultPlayerExtractor(this.eventType);
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
