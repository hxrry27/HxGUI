## HxGUI README

# HxGUI

Advanced GUI library for Paper/Bukkit plugins with fluent builders, pagination, and templates.

## Overview

HxGUI attempts to simplify creating interactive inventory menus in Minecraft paper plugins, providing a modern API with pre-built components and themes (albeit originally designed for ValeSMP).

## Tech Stack

- **Dependencies**: HxCore, Paper API
- **Text**: MiniMessage formatting with Adventure API
- **Patterns**: Visual slot mapping system
- **Serialization**: YAML-based menu configuration

## Features

- Fluent builder API for creating menus
- Shared and per-player inventory modes
- Automatic pagination for large item lists
- Confirmation dialogs and pre-built templates
- Visual pattern system for complex layouts
- Full MiniMessage support with gradients/rainbow
- YAML serialization for menu persistence
- ValeSMP theme with consistent styling

## Usage

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        HxGUI.init(this);
    }
    
    @Override
    public void onDisable() {
        HxGUI.shutdown();
    }
    
    public void openMenu(Player player) {
        GUIBuilder.chest()
            .title("<gradient:#00ff88:#0088ff>Menu</gradient>")
            .rows(3)
            .item(13, ItemBuilder.of(Material.DIAMOND)
                .name("<aqua>Click me!")
                .build(),
                event -> player.sendMessage("Clicked!"))
            .open(player);
    }
}
```

## Installation

```xml
<dependency>
    <groupId>dev.hxrry</groupId>
    <artifactId>hxgui</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```






