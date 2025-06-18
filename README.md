# AdvancementLib
[![Javadoc](https://img.shields.io/badge/docs-javadoc-blue.svg)](https://6mal7.github.io/AdvancementLib/javadoc/)
[![License](https://img.shields.io/github/license/6mal7/AdvancementLib)](https://github.com/6mal7/AdvancementLib/blob/main/LICENSE)
[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://jdk.java.net/)
[![PaperMC](https://img.shields.io/badge/PaperMC-1.21.4-green)](https://papermc.io/)

>**AdvancementLib** is a modern, fluent Java library for creating custom advancements in [PaperMC](https://papermc.io/) Minecraft plugins.  
It lets you easily register and manage advancements that respond to any Bukkit event, using a powerful builder-based API.
---

## ✨ Features
- **Register custom triggers:** Listen for any Bukkit event (e.g., block break, entity death, player action) as an advancement trigger.
- **Custom conditions:** Define your own logic to decide when an advancement should progress or be granted—perfect for unique gameplay challenges.
- **Support for vanilla & custom advancements:** Grant both built-in Minecraft advancements and your own custom ones.
- **Progress tracking:** Easily set multistep advancements (e.g., "break 100 blocks") with automatic progress tracking per player.
- **Builder API:** Use a fluent builder to configure triggers, conditions, and progress logic.
- **Custom progress increments:** Determine how much progress each event grants (default is 1, but can be customized).

---

## 🚀 Getting Started

### 1. Add as a Dependency

Add **AdvancementLib** as a dependency in your project using your preferred build tool or project setup method.

>Note: Add the PaperMC repository if not already present.

### 2. Basic Usage

#### Register an Advancement

```java
import io.github._6mal7.advancementLib.AdvancementAPI;

AdvancementAPI api = new AdvancementAPI(plugin);

api.register(BlockBreakEvent.class)
    .advancementKey("myplugin:break_10_stone")
    .condition((player, event) -> event.getBlock().getType() == Material.STONE)
    .targetValue(10)
    .grantMode(GrantMode.ALL_AT_ONCE)
    .build();
```

#### Grant Modes

- `ALL_AT_ONCE`: Grants the advancement when the target is reached.
- `STEP_BY_STEP`: Grants one criterion at a time (shows progress bar in-game).

#### Custom Player Extractor

```java
api.register(CustomEvent.class)
    .advancementKey("myplugin:custom_event")
    .playerExtractor(event -> event.getWhoDidIt())
    .build();
```

## 📚 API Highlights & Documentation

- **AdvancementAPI** – Main entry point for registering advancements.
- **AdvancementRegisterBuilder** – Fluent builder for all advancement options.
- **GrantMode** – Enum for grant behavior: all at once or step-by-step.
- **PlayerExtractor** – Interface for mapping any event to a Player.

The latest Javadoc is automatically published to GitHub Pages:

➡️ **[View the Javadoc here](https://6mal7.github.io/AdvancementLib/javadoc/)**
