package com.okbeanok.tropicapi.api.color;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Represents available chat colors players can select.
 */
public enum ChatColor {
	WHITE("&f", "White", Material.WHITE_WOOL, null),
	GRAY("&7", "Gray", Material.GRAY_WOOL, "tropicchat.color.gray"),
	DARK_GRAY("&8", "Dark Gray", Material.LIGHT_GRAY_WOOL, "tropicchat.color.darkgray"),
	BLACK("&0", "Black", Material.BLACK_WOOL, "tropicchat.color.black"),

	RED("&c", "Red", Material.RED_WOOL, "tropicchat.color.red"),
	DARK_RED("&4", "Dark Red", Material.RED_TERRACOTTA, "tropicchat.color.darkred"),
	GOLD("&6", "Gold", Material.ORANGE_WOOL, "tropicchat.color.gold"),
	YELLOW("&e", "Yellow", Material.YELLOW_WOOL, "tropicchat.color.yellow"),

	GREEN("&a", "Green", Material.LIME_WOOL, "tropicchat.color.green"),
	DARK_GREEN("&2", "Dark Green", Material.GREEN_WOOL, "tropicchat.color.darkgreen"),
	AQUA("&b", "Aqua", Material.CYAN_WOOL, "tropicchat.color.aqua"),
	DARK_AQUA("&3", "Dark Aqua", Material.LIGHT_BLUE_WOOL, "tropicchat.color.darkaqua"),

	BLUE("&9", "Blue", Material.BLUE_WOOL, "tropicchat.color.blue"),
	DARK_BLUE("&1", "Dark Blue", Material.BLUE_TERRACOTTA, "tropicchat.color.darkblue"),
	LIGHT_PURPLE("&d", "Light Purple", Material.PINK_WOOL, "tropicchat.color.lightpurple"),
	DARK_PURPLE("&5", "Dark Purple", Material.PURPLE_WOOL, "tropicchat.color.darkpurple"),

	// Premium colors (require permissions)
	RAINBOW("<gradient:red:orange:yellow:green:blue:purple>", "Rainbow", Material.NETHER_STAR, "tropicchat.color.rainbow"),
	GRADIENT_FIRE("<gradient:red:gold>", "Fire Gradient", Material.FIRE_CHARGE, "tropicchat.color.gradient.fire"),
	GRADIENT_OCEAN("<gradient:blue:aqua>", "Ocean Gradient", Material.HEART_OF_THE_SEA, "tropicchat.color.gradient.ocean"),
	GRADIENT_FOREST("<gradient:dark_green:green:lime>", "Forest Gradient", Material.OAK_LEAVES, "tropicchat.color.gradient.forest");

	private final String colorCode;
	private final String displayName;
	private final Material icon;
	private final String permission;

	ChatColor(@NotNull String colorCode, @NotNull String displayName,
			  @NotNull Material icon, String permission) {
		this.colorCode = colorCode;
		this.displayName = displayName;
		this.icon = icon;
		this.permission = permission;
	}

	@NotNull
	public String getColorCode() {
		return colorCode;
	}

	@NotNull
	public String getDisplayName() {
		return displayName;
	}

	@NotNull
	public Material getIcon() {
		return icon;
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasPermission() {
		return permission != null && !permission.isEmpty();
	}

	public boolean canUse(@NotNull org.bukkit.entity.Player player) {
		return !hasPermission() || player.hasPermission(permission);
	}

	@NotNull
	public static ChatColor fromString(@NotNull String name) {
		try {
			return ChatColor.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			return WHITE; // Default
		}
	}
}