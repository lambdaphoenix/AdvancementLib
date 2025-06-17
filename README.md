# AdvancementLib

A Java library for Paper plugins to easily register and manage custom or vanilla advancements in Minecraft.  
**AdvancementLib** lets you programmatically grant advancements to players based on any Bukkit event or custom condition.

---

## ✨ Features
- **Register custom triggers:** Listen for any Bukkit event (e.g., block break, entity death, player action) as an advancement trigger.
- **Custom conditions:** Define your own logic to decide when an advancement should progress or be granted—perfect for unique gameplay challenges.
- **Support for vanilla & custom advancements:** Grant both built-in Minecraft advancements and your own custom ones.
- **Progress tracking:** Easily set multi-step advancements (e.g., "break 100 blocks") with automatic progress tracking per player.

---

## 🚀 Getting Started

### 1. Add the Library

Add **AdvancementLib** as a dependency in your project using your preferred build tool or project setup method.


### 2. Register an Advancement Trigger

Here's how to grant the vanilla **"What a Deal!"** advancement (`minecraft:adventure/trade`) when a player kills a villager:

```
  advancementAPI.registerAdvancement(
    "minecraft:adventure/trade", // Advancement key (namespace:path)
    EntityDeathEvent.class, // Bukkit event to listen for
    (player, event) -> {
      if (!(event.getEntity() instanceof Villager)) return false;
      return (player != null);
    }, // Condition: villager killed by player
    1, // How many times to trigger before granting
    event -> event.getEntity().getKiller() // How to extract the Player from the event
  );
```

---

## 📚 API Reference

Before using the API, you need to create an instance of the `AdvancementAPI` object.  
This object is the entry point for registering and managing advancements in your plugin.

### Creating the API Object

```
  AdvancementAPI advancementAPI = new AdvancementAPI(plugin); // 'plugin' is your JavaPlugin instance
```

### Registering an Advancement

```
  <E extends Event> void registerAdvancement(
    String advancementKey, // e.g. "myplugin:my_advancement"
    Class<E> eventType, // Bukkit event class to listen for
    BiPredicate<Player, E> condition, // Condition to check before progressing
    int targetValue, // Number of times condition must be met
    Function<E, Player> playerExtractor // How to get the Player from the event
  )
```
