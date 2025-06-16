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

public final class AdvancementAPI {
  private static final String ADVANCEMENT_API_KEY = "advancement_api";
  private final Plugin plugin;

  public AdvancementAPI(Plugin plugin) {
    this.plugin = plugin;
  }

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
                if (player != null && condition.test(player, e)) {
                  NamespacedKey namespacedKey =
                      new NamespacedKey(
                          ADVANCEMENT_API_KEY, advancementName.replaceFirst(":", "."));
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
              }
            }),
            plugin);
  }

  public int getProgress(String advancementName, Player player) {
    NamespacedKey namespacedKey =
        new NamespacedKey(ADVANCEMENT_API_KEY, advancementName.replaceFirst(":", "."));
    return player
        .getPersistentDataContainer()
        .getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0);
  }

  private void grantAdvancement(Player player, String advancementName) {
    Advancement advancement =
        Bukkit.getAdvancement(Objects.requireNonNull(NamespacedKey.fromString(advancementName)));
    if (advancement == null) return;
    AdvancementProgress progress = player.getAdvancementProgress(advancement);
    for (String criterion : progress.getRemainingCriteria()) {
      progress.awardCriteria(criterion);
    }
  }
}
