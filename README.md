# Hytale Server Unpacked

## Overview
This repository contains the unpacked source code of the Hytale server. It is a resource for understanding the historical implementation of the server to aid in plugin development and migrating plugins during version updates.

> **Official Modding Documentation**:  
> [https://github.com/HytaleModding/site/tree/main/content/docs/en](https://github.com/HytaleModding/site/tree/main/content/docs/en)

---

## 🏛️ Architecture & Codebase Map

### Core Engine
- **Entry Point**: `com.hypixel.hytale.Main` (Delegates to `LateMain`)
- **Server Loop**: `com.hypixel.hytale.server.core.HytaleServer`
- **ECS Framework**: `com.hypixel.hytale.component` (The Entity Component System core)

### World & Universe
- **World Management**: `com.hypixel.hytale.server.core.universe.world`
  - **Key Class**: `World` (Represents a dimension/world instance).
- **World Generation**: 
  - `com.hypixel.hytale.server.worldgen`: Core generation logic.
  - `com.hypixel.hytale.builtin.worldgen`: Content-specific generation implementation.
- **Blocks & Regions**: `com.hypixel.hytale.protocol` (Definitions) and `com.hypixel.hytale.builtin.adventure`.

### Entities & NPCs
- **NPC Logic**: `com.hypixel.hytale.server.npc`
  - **Base Class**: `com.hypixel.hytale.server.npc.entities.NPCEntity`.
  - **Plugin**: `com.hypixel.hytale.server.npc.NPCPlugin`.
- **Entities**: `com.hypixel.hytale.server.core.entity`
  - Base entity classes and management.

### Gameplay Systems
- **Crafting & Recipes**: `com.hypixel.hytale.builtin.crafting`
- **Commands**: `com.hypixel.hytale.server.core.command`
  - **Implementation**: See `commands.utility` for simple examples like `NotifyCommand`.
- **Items & Blocks**: `com.hypixel.hytale.builtin` root packages.

### Client Interaction
- **UI & Interfaces**: `com.hypixel.hytale.server.core.ui`
  - **Builder**: `com.hypixel.hytale.server.core.ui.builder.UICommandBuilder` (Used to construct UI packets).

-------------

## 🧑‍💻 Developer Cheatsheet (Concrete Examples)

### Commands
Commands typically extend `CommandBase`.
- **Location**: `com.hypixel.hytale.server.core.command.commands`
- **Example**: `utility.NotifyCommand` shows how to parse args and send messages.
- **Registration**: Look for the `CommandRegistry` in `server.core.command`.

### UI Interaction
To manipulate Client UI, you often use the `UICommandBuilder` to queue changes.
- **Class**: `com.hypixel.hytale.server.core.ui.builder.UICommandBuilder`
- **Methods**: `.set(selector, value)`, `.append(selector, xmlPath)`, `.remove(selector)`.
- **Usage**: Construct a command chain to modify the client's HTML/XML DOM.

### NPCs
NPCs are Entities with specific behaviors (Roles).
- **Class**: `com.hypixel.hytale.server.npc.entities.NPCEntity`.
- **Spawning**: `NPCPlugin.get().getIndex(roleName)` identifies roles.
- **Management**: `server.npc.NPCPlugin` manages the lifecycle of NPC systems.

### World Access
- **Class**: `com.hypixel.hytale.server.core.universe.world.World`.
- **Context**: Often passed into Entity constructors or available via `HytaleServer.get().getUniverse()`.

---

## 🤖 Guide to Key Systems (for AI Agents)

| System | Primary Package | Context |
| :--- | :--- | :--- |
| **Generators** | `server.worldgen` | Logic for creating chunks/dungeons. |
| **Map/Worlds** | `server.core.universe` | Managing loaded worlds/dimensions. |
| **ECS** | `component` | The underlying data engine. |
| **Networking** | `protocol` | Packet definitions (Shared). |
| **UI** | `server.core.ui` | Interfaces created by the server. |
| **Commands** | `server.core.command` | Chat commands. |
| **Crafting** | `builtin.crafting` | Recipes. |
| **NPCs** | `server.npc` | AI behaviors. |

**Heuristic**:
- If you need **Definitions** (IDs, Enums, Packets) -> Go to `protocol` or `builtin`.
- If you need **Logic** (How it ticks, How it runs) -> Go to `server` or `server.core`.
