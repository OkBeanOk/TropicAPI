# TropicAPI

<div align="center">

![Version](https://img.shields.io/badge/version-1.2--Development-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.21.x-green.svg)
![Java](https://img.shields.io/badge/java-21-orange.svg)
![Build](https://img.shields.io/badge/build-Maven-orange.svg)

**Shared core API for the Tropic plugin ecosystem – color utilities, messages, GUIs, tasks, configs, and more.**

</div>

---

## 📚 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
  - [Color API](#-color-api)
  - [Centered Messages](#-centered-messages)
  - [GUI System](#-gui-system)
  - [Messages API](#-messages-api)
  - [Task Scheduler](#-task-scheduler)
  - [Config API](#-config-api)
  - [Player Profiles & Context](#-player-profiles--context)
  - [Tropic Integration & Events](#-tropic-integration--events)
  - [Utility Helpers](#-utility-helpers)
- [Getting Started](#-getting-started)
  - [Requirements](#-requirements)
  - [Installation (Maven)](#-installation-maven)
  - [Installation (Gradle Groovy)](#-installation-gradle-groovy)
  - [Installation (Gradle Kotlin DSL)](#-installation-gradle-kotlin-dsl)
- [Usage Examples](#-usage-examples)
  - [ColorAPI](#colorapi)
  - [CenteredMessagesAPI](#centeredmessagesapi)
  - [GUI / Confirm / Pagination / ItemBuilder](#gui--confirm--pagination--itembuilder)
  - [Messages](#messages)
  - [Tasks](#tasks)
  - [Configs](#configs)
  - [Player Profiles & PlayerContext](#player-profiles--playercontext)
  - [TropicPluginsAPI & Events](#tropicpluginsapi--events)
  - [Utility Helpers (Cooldowns / Durations / Perms / TropicLog)](#utility-helpers-cooldowns--durations--perms--tropiclog)

---

## 📦 Overview

**TropicAPI** is the shared foundation for plugins in the **Tropic** ecosystem.

Instead of every plugin shipping its own copies of color utilities, GUI helpers, task wrappers, config loaders, and small abstractions, they all depend on TropicAPI and build on a consistent set of tools.

### What it provides

- Color parsing and formatting (`&` + hex)
- Centered messages for chat
- High-level inventory GUI framework
- Messaging facade with info / success / error styles
- Task scheduler facade (sync / async / later / repeating)
- Config abstraction and loader
- Player profiles and simple shared per-player context
- Tropic ecosystem integration helpers and lightweight event bus
- Small but handy utility helpers: cooldowns, formatted durations, permission checks, logging

---

## ✨ Features

### 🎨 Color API

**Packages:** `com.okbeanok.tropicapi.api.color`

Core color utilities (via `ColorService` and a static facade `ColorAPI`):

- Legacy `&` codes (`&a`, `&b`, `&l`, etc.)
- Hex codes:
    - `#RRGGBB`
    - `&#RRGGBB`
- **Gradient tags**:
    - `<gradient:#ff0000:#00ff00:#0000ff>Your text here</gradient>`
    - Any number of color stops are supported; colors are smoothly interpolated across the text.
- Strip color codes
- Send colored messages to `CommandSender` / `Player`

---

## 🧪 Usage Examples

> Examples assume TropicAPI is present and initialized.  
> Import statements are shown as comments to keep the snippets focused.

### 📐 Centered Messages

**Packages:** `com.okbeanok.tropicapi.api.message` (centered-related types)

Key class: `CenteredMessagesAPI`

- Compute pixel width of a string
- Generate chat lines that appear visually centered
- Respect color codes and formatting
- Methods:
  - `CenteredMessagesAPI.send(CommandSender, String)`
  - `CenteredMessagesAPI.send(Player, String)`
  - `CenteredMessagesAPI.center(String)` (returns a centered line)

---

### 🧩 GUI System

**Packages:** `com.okbeanok.tropicapi.api.gui`

Main building blocks:

- `GUI` – High-level base class for chest-style menus.
- `GUIAPI` – Low-level static facade (create/open inventories, register handlers).
- `GUIClickEvent` – Wrapper around inventory click events.
- `GUIService` – Internal service abstraction.
- **Extras:**
  - `ConfirmGUI` – Yes/No confirmation GUI.
  - `PaginatedGUI<T>` – Base for paginated menus.
  - `ItemBuilder` – Fluent API for building `ItemStack`s.

Designed so Tropic plugins can share a consistent GUI pattern while still allowing low-level customization where needed.

---

### 💬 Messages API

**Packages:** `com.okbeanok.tropicapi.api.message`

Message layer on top of color utilities:

- `MessageService` – Service interface (locale-aware hooks, formatting).
- `Messages` – Static facade for common operations.

Capabilities:

- Simple placeholder formatting: `"Hello, {player}"` with a `Map<String, Object>`.
- Standardized prefixed messages:
  - `Messages.info(sender, "...")`
  - `Messages.success(sender, "...")`
  - `Messages.error(sender, "...")`
- Locale-aware `getRaw(...)` and `resolve(...)` hooks — ready for your own localization backend.

---

### ⏱ Task Scheduler

**Packages:** `com.okbeanok.tropicapi.api.task`

- `TaskService` – Abstraction over Bukkit scheduler.
- `Tasks` – Static facade for quick use.

Features:

- Run on main thread: `Tasks.runSync(...)`
- Run async: `Tasks.runAsync(...)`
- Schedule once later: `Tasks.runLaterSync(task, delayTicks)`
- Schedule repeating: `Tasks.runTimerSync(task, delayTicks, periodTicks)`
- Async tasks with results: `Tasks.supplyAsync(Supplier<T>)`

---

### 📝 Config API

**Packages:** `com.okbeanok.tropicapi.api.config`

- `ConfigService` – Service for loading config files.
- `Configs` – Static facade.
- `ConfigHandle` – Wrapper around `FileConfiguration`.

Use cases:

- Load main `config.yml` or custom YAML files.
- Save / reload via a consistent interface.
- Keep config management uniform across all Tropic plugins.

---

### 👤 Player Profiles & Context

**Packages:** `com.okbeanok.tropicapi.api.player`

- `PlayerProfile` – Simple read-only snapshot of player info (UUID, name, first/last join, banned/muted flags, etc.).
- `PlayerProfileService` – Access profiles:
  - From online players
  - From offline players
  - Async by UUID
- `PlayerContext` – Minimal per-player key/value store shared across Tropic plugins.

Good for sharing session-level state (current GUI, active flow, etc.) without each plugin reinventing the wheel.

---

### 🌐 Tropic Integration & Events

**Packages:**

- Integration: `com.okbeanok.tropicapi.api.integration`
- Events: `com.okbeanok.tropicapi.api.event`

**Integration:**

- `TropicPluginsAPI` + `TropicPlugins` + `TropicPluginInfo`
  - Check if other Tropic plugins are present.
  - Get name, version, enabled state.
  - List all loaded Tropic plugins.

**Events:**

- `TropicEvents` + `TropicEventBus`
  - Simple internal event bus for Tropic ecosystem.
  - Register listeners by type, post arbitrary POJO events without tying into Bukkit’s event system directly.

---

### 🧰 Utility Helpers

**Packages:** `com.okbeanok.tropicapi.api.util`

Includes:

- `Cooldowns` – Per-player cooldowns for arbitrary keys.
- `Durations` – Human-readable formatting of durations (e.g. `2m 3s`).
- `Perms` – Small permission helper (require/ifHas).
- `TropicLog` – Logger wrapper with module tags.

These are small but reduce a lot of repetitive boilerplate in individual plugins.

---

## 🚀 Getting Started

### ✅ Requirements

- **Java:** 21+
- **Server:** Paper or Spigot 1.21.x (Paper recommended)
- **Build tool:** Maven or Gradle
- **Plugin type:** Bukkit/Spigot/Paper

### 📦 Installation (Maven)

Add the repository and dependency:

```
xml jitpack.io https://jitpack.io
com.okbeanok TropicAPI 0.5.2-Development provided```

> Use `scope` `provided` if TropicAPI is installed as its own plugin on the server.  
> Use `compile`/`implementation` and **relocate** the package if you plan to shade TropicAPI into your own plugin.

### 📦 Installation (Gradle Groovy)
```

groovy repositories { maven { url 'https://jitpack.io' } // other repositories... }
dependencies { compileOnly 'com.okbeanok:TropicAPI:0.5.2-Development' // or implementation + shading if you want it bundled }``` 

### 📦 Installation (Gradle Kotlin DSL)
```

kotlin repositories { maven("https://jitpack.io") // other repositories... }
dependencies { compileOnly("com.okbeanok:TropicAPI:0.5.2-Development") // or implementation + shading if you want it bundled }```

---

## 🧪 Usage Examples

> Examples assume TropicAPI is present and initialized.  
> Import statements are shown as comments to keep the snippets focused.

### ColorAPI
```

java // import com.okbeanok.tropicapi.api.color.ColorAPI; import org.bukkit.entity.Player;
public void sendColored(Player player) { String raw = "&aWelcome &bto &6Tropic &fAPI! &7(#ff8800 example)"; String colored = ColorAPI.color(raw); // converts & + hex to server formatting player.sendMessage(colored); }``` 

---

### CenteredMessagesAPI
```

java // import com.okbeanok.tropicapi.api.message.CenteredMessagesAPI; import org.bukkit.command.CommandSender;
public void sendCentered(CommandSender sender) { String message = "&b&lTropicAPI &7- &fAll-in-one utilities!"; CenteredMessagesAPI.send(sender, message); }```

Only getting the centered line:
```

java String centered = CenteredMessagesAPI.center("&eCentered text!"); // send with your own messaging system``` 

---

### GUI / Confirm / Pagination / ItemBuilder

#### Basic GUI (using `GUI`)
```

java // import com.okbeanok.tropicapi.api.gui.GUI; // import com.okbeanok.tropicapi.api.gui.GUIClickEvent; import org.bukkit.entity.Player; import org.bukkit.Material; import org.bukkit.inventory.ItemStack;
public class ExampleGUI extends GUI {
public ExampleGUI(Player player) {
super(player, "&aExample &2Menu", 3 * 9); // 3 rows
}

@Override
public void build() {
ItemStack closeItem = new ItemStack(Material.BARRIER);
setItem(13, closeItem); // center slot in 3x9
}

@Override
public void onClick(GUIClickEvent event) {
if (event.getSlot() == 13) {
event.getPlayer().closeInventory();
event.setCancelled(true);
}
}
}```

Opening it:
```

java public void openExample(Player player) { new ExampleGUI(player).open(); }``` 

#### Confirmation GUI (`ConfirmGUI`)
```

java // import com.okbeanok.tropicapi.api.gui.ConfirmGUI; import org.bukkit.entity.Player;
public void deleteSomething(Player player) { ConfirmGUI.open( player, "&cConfirm deletion", () -> { // on confirm }, () -> { // on cancel } ); }```

#### Paginated GUI (`PaginatedGUI<T>`)

You extend `PaginatedGUI` and implement `rebuild()` to draw items for each page.
```

java // import com.okbeanok.tropicapi.api.gui.PaginatedGUI; // import com.okbeanok.tropicapi.api.gui.GUIClickEvent; import org.bukkit.entity.Player; import org.bukkit.Material; import org.bukkit.inventory.ItemStack;
import java.util.List;
public class ExamplePaginatedGUI extends PaginatedGUI{
public ExamplePaginatedGUI(Player player, List<String> entries) {
	super(player, "&bExample Paged Menu", 6 * 9, 45); // 5 rows for items, 1 for controls
	setItems(entries);
}

@Override
protected void rebuild() {
	// Clear and redraw items based on getItemsOnPage()
	getInventory().clear();

	int slot = 0;
	for (String entry : getItemsOnPage()) {
		ItemStack item = new ItemStack(Material.PAPER);
		// set name / lore...
		setItem(slot++, item);
	}

	// simple next/prev controls (e.g., last row)
}

@Override
public void onClick(GUIClickEvent event) {
	// interpret clicks as next/prev/etc.
}
}``` 

#### ItemBuilder
```

java // import com.okbeanok.tropicapi.api.gui.ItemBuilder; import org.bukkit.Material; import org.bukkit.inventory.ItemStack;
ItemStack item = ItemBuilder.of(Material.DIAMOND_SWORD) .name("§bCool Sword") .lore("§7Line 1", "§7Line 2") .unbreakable(true) .build();```

---

### Messages

Using the static facade:
```

java // import com.okbeanok.tropicapi.api.message.Messages; import org.bukkit.command.CommandSender;
public void notifySave(CommandSender sender, boolean success) { if (success) { Messages.success(sender, "&aYour settings have been saved."); } else { Messages.error(sender, "&cFailed to save your settings."); } }``` 

Placeholders:
```

java // import com.okbeanok.tropicapi.api.message.Messages; import org.bukkit.command.CommandSender;
import java.util.Map;
public void greet(CommandSender sender) { String template = "&bHello, {player}&7! You have &e{coins} &7coins."; String formatted = Messages.format(template, Map.of); Messages.info(sender, formatted); }```

Via `MessageService` from the plugin instance:
```

java // import com.okbeanok.tropicapi.TropicAPI; // import com.okbeanok.tropicapi.api.message.MessageService; import org.bukkit.command.CommandSender;
public void sendFromService(CommandSender sender) { MessageService ms = TropicAPI.getInstance().getMessageService(); ms.sendInfo(sender, "&7This came from MessageService directly."); }``` 

---

### Tasks
```

java // import com.okbeanok.tropicapi.api.task.Tasks;
// Sync Tasks.runSync(() -> { // modify world, players, etc. });
// Async Tasks.runAsync(() -> { // heavy DB / HTTP / computations });```

Later / repeating:
```

java // 20 ticks = 1 second
Tasks.runLaterSync(() -> { // runs once, 5 seconds later }, 20L * 5);
Tasks.runTimerSync(() -> { // runs every second, after initial 2-sec delay }, 20L * 2, 20L);``` 

Async with result:
```

java import java.util.concurrent.CompletableFuture; // import com.okbeanok.tropicapi.api.task.Tasks;
public void loadSomethingAsync() { CompletableFuturefuture = Tasks.supplyAsync(() -> { // heavy computation return "result"; });
future.thenAccept(result -> {
Tasks.runSync(() -> {
// use result safely on main thread
});
});
}```

---

### Configs
```

java // import com.okbeanok.tropicapi.api.config.Configs; // import com.okbeanok.tropicapi.api.config.ConfigHandle; import org.bukkit.configuration.file.FileConfiguration; import org.bukkit.plugin.java.JavaPlugin;
public class ExamplePlugin extends JavaPlugin {
@Override
public void onEnable() {
	ConfigHandle handle = Configs.mainConfig(this);
	FileConfiguration config = handle.getConfiguration();

	String message = config.getString("messages.welcome", "&aWelcome!");
	getLogger().info("Welcome message: " + message);
}
}``` 

Custom file:
```

java ConfigHandle messages = Configs.load(this, "messages.yml"); String prefix = messages.getConfiguration().getString("prefix", "&7[&bTropic&7] "); messages.getConfiguration().set("prefix", "&7[&a✓&7] "); messages.save();```

---

### Player Profiles & PlayerContext
```

java // import com.okbeanok.tropicapi.TropicAPI; // import com.okbeanok.tropicapi.api.player.PlayerProfileService; // import com.okbeanok.tropicapi.api.player.PlayerProfile; import org.bukkit.entity.Player;
public void logProfile(Player player) { PlayerProfileService service = TropicAPI.getInstance().getPlayerProfileService(); PlayerProfile profile = service.getProfile(player);
getLogger().info("Player " + profile.getLastKnownName() +
		" joined first at " + profile.getFirstJoin() +
		" and last at " + profile.getLastJoin());
}``` 

Async load:
```

java // import com.okbeanok.tropicapi.api.player.PlayerProfileService; // import com.okbeanok.tropicapi.api.task.Tasks; import java.util.UUID;
public void loadProfileAsync(UUID uuid) { PlayerProfileService service = TropicAPI.getInstance().getPlayerProfileService(); service.loadProfile(uuid).thenAccept(profile -> { if (profile == null) return; Tasks.runSync(() -> { // use profile safely on main thread }); }); }```

`PlayerContext`:
```

java // import com.okbeanok.tropicapi.api.player.PlayerContext; import org.bukkit.entity.Player;
public void setCurrentMenu(Player player, String menuId) { PlayerContext.set(player, "current-menu", menuId); }
public String getCurrentMenu(Player player) { return PlayerContext.get(player, "current-menu", String.class); }``` 

---

### TropicPluginsAPI & Events

Check other Tropic plugins:
```

java // import com.okbeanok.tropicapi.api.integration.TropicPluginsAPI;
boolean hasTropicChat = TropicPluginsAPI.isPresent("TropicChatCore"); TropicPluginsAPI.getInfo("TropicChatCore").ifPresent(info -> { getLogger().info("TropicChatCore version: " + info.getVersion()); });```

Use the internal event bus:
```

java // import com.okbeanok.tropicapi.api.event.TropicEvents;
public record AuctionCreatedEvent(String auctionId) {}
// Register a listener TropicEvents.listen(AuctionCreatedEvent.class, event -> { getLogger().info("Auction created: " + event.auctionId()); });
// Post event somewhere TropicEvents.post(new AuctionCreatedEvent("abc123"));``` 

---

### Utility Helpers (Cooldowns / Durations / Perms / TropicLog)

Cooldowns:
```

java // import com.okbeanok.tropicapi.api.util.Cooldowns; import org.bukkit.entity.Player;
import java.util.concurrent.TimeUnit;
public boolean tryUseCommand(Player player) { boolean allowed = Cooldowns.checkAndApply("myplugin:mycommand", player, 10, TimeUnit.SECONDS); return allowed; }```

Durations:
```

java // import com.okbeanok.tropicapi.api.util.Durations;
String pretty = Durations.formatSeconds(123); // "2m 3s"``` 

Permissions:
```

java // import com.okbeanok.tropicapi.api.util.Perms; import org.bukkit.command.CommandSender;
public void execute(CommandSender sender) { Perms.require(sender, "myplugin.use", () -> { // allowed }, () -> sender.sendMessage("No permission.") ); }```

Logging with module name:
```

java // import com.okbeanok.tropicapi.api.util.TropicLog;
TropicLog.info("auctions", "Loaded 10 auctions."); TropicLog.warn("moderation", "No default rules configured."); TropicLog.error("chat", "Failed to connect to chat backend.");``` 

