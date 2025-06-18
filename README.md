# AdvancementLib

A Java library for Paper plugins to easily register and manage custom or vanilla advancements in Minecraft.  
**AdvancementLib** lets you programmatically grant advancements to players based on any Bukkit event and custom condition.

---

## ✨ Features
- **Register custom triggers:** Listen for any Bukkit event (e.g., block break, entity death, player action) as an advancement trigger.
- **Custom conditions:** Define your own logic to decide when an advancement should progress or be granted—perfect for unique gameplay challenges.
- **Support for vanilla & custom advancements:** Grant both built-in Minecraft advancements and your own custom ones.
- **Progress tracking:** Easily set multi-step advancements (e.g., "break 100 blocks") with automatic progress tracking per player.
- **Builder API:** Use a fluent builder to configure triggers, conditions, and progress logic.
- **Custom progress increments:** Determine how much progress each event grants (default is 1, but can be customized).

---

## 🚀 Getting Started

### 1. Add the Library

Add **AdvancementLib** as a dependency in your project using your preferred build tool or project setup method.

### 2. Initialize the API

Before using the API, you need to create an instance of the `AdvancementAPI` object.  
This object is the entry point for registering and managing advancements in your plugin.

#### Creating the API Object

```java
  AdvancementAPI advancementAPI = new AdvancementAPI(plugin); // 'plugin' is your JavaPlugin instance
```

### 3. Register an Advancement
Here's how to grant the vanilla **"What a Deal!"** advancement (`minecraft:adventure/trade`) when a player kills a villager:

#### Using the Builder API (Recommended)
```java
  api.newRegisterBuilder(EntityDeathEvent.class)
    .advancementName("minecraft:adventure/trade")
    .condition((player, event) -> event.getEntity() instanceof Villager && player != null)
    .playerExtractor(event -> event.getEntity().getKiller())
    .register();
```

#### Using the Direct API
```java
  api.registerAdvancement(
    "minecraft:adventure/trade",
    EntityDeathEvent.class,
    (player, event) -> event.getEntity() instanceof Villager && player != null,
    1,
    event -> event.getEntity().getKiller(),
    GrantMode.ALL_AT_ONCE,
    null
  );
```

## 📚 API Reference

### Builder API Attributes
```java
advancementKey(String advancementKey)          // The namespaced key of the advancement (e.g. "minecraft:adventure/trade")
eventType(Class<E> eventType)                  // The Bukkit event class to listen for
condition(BiPredicate<Player, E> condition)    // Predicate to check if the event should count for progress
targetValue(int targetValue)                   // Number of times the condition must be met before granting the advancement
playerExtractor(Function<E, Player> extractor) // Function to extract the Player from the event
grantMode(GrantMode grantMode)                 // How the advancement is granted: ALL_AT_ONCE or STEP_BY_STEP
increment(ToIntFunction<E> increment)          // (Optional) Function to determine progress per event. Defaults to 1 if null
```

### Direct API Parameters
```java
  <E extends Event> void registerAdvancement(
    String advancementKey,                     // e.g. "myplugin:my_advancement"
    Class<E> eventType,                        // Bukkit event class to listen for
    BiPredicate<Player, E> condition,          // Condition to check before progressing
    int targetValue,                           // Number of times condition must be met
    Function<E, Player> playerExtractor,       // How to get the Player from the event
    GrantMode grantMode,                       //How the advancement is granted: ALL_AT_ONCE or STEP_BY_STEP
    ToIntFunction<E> increment                 // Function to determine progress per event. Defaults to 1 if null
  )
```
