package com.okbeanok.tropicapi.api.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@UtilityClass
public class ReflectionUtil {

	/**
	 * Cache for fields to avoid expensive reflection calls every time
	 * <p>
	 * Class -> (Field Name -> Field)
	 */
	private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();
	private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
	private static String version;

	public static <T> boolean setFieldValue(final @NotNull Object object, final @NotNull String fieldName, final @Nullable T value) {
		Class<?> clazz = object.getClass();
		Field field = getCachedField(clazz, fieldName);
		if (field != null) {
			try {
				field.set(object, value);
				return true;
			} catch (IllegalAccessException ignored) {
				// The field is not accessible*
			}
		}
		return false;
	}

	@Nullable
	public static <T> T getFieldValue(final @NotNull Object object, final @NotNull String fieldName) {
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		Field field = getCachedField(clazz, fieldName);
		if (field != null) {
			try {
				return (T) field.get(object);
			} catch (IllegalAccessException ignored) {
				// The field is not accessible
			}
		}
		return null;
	}

	public static Field findField(Class<?> clazz, Class<?> type) {
		if (clazz == null) return null;

		Field[] methods = clazz.getDeclaredFields();
		for (Field method : methods) {
			if (!method.getType().equals(type)) continue;

			method.setAccessible(true);
			return method;
		}
		return null;
	}

	@Nullable
	private static Field getCachedField(final @NotNull Class<?> clazz, final @NotNull String fieldName) {
		Map<String, Field> classFields = FIELD_CACHE.get(clazz);
		if (classFields == null) {
			classFields = new ConcurrentHashMap<>();
			Map<String, Field> existingFields = FIELD_CACHE.putIfAbsent(clazz, classFields);
			if (existingFields != null) {
				classFields = existingFields; // Another thread already added the map
			}
		}

		Field field = classFields.get(fieldName);
		if (field == null) {
			try {
				field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				classFields.put(fieldName, field);
			} catch (NoSuchFieldException ignored) {
				// Field does not exist
			}
		}
		return field;
	}

	public static String getVersion() {
		if (version == null) {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		}
		return version;
	}

	@Nullable
	public static Class<?> getClass(final @NotNull String classPath) {
		try {
			return Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, String.format("Failed to get class: %s", classPath), e);
			return null;
		}
	}

	@Nullable
	public static Class<?> getNMClass(final @NotNull String classPath) {
		try {
			return Class.forName("net.minecraft." + classPath);
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, String.format("Failed to get net.minecraft class: %s", classPath), e);
			return null;
		}
	}

	@Nullable
	public static Class<?> getNMSClass(final @NotNull String classPath) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + classPath);
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, String.format("Failed to get net.minecraft.server class: %s", classPath), e);
			return null;
		}
	}

	@Nullable
	public static Class<?> getObcClass(final @NotNull String classPath) {
		try {
			return Class.forName(CRAFTBUKKIT_PACKAGE + "." + classPath);
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, String.format("Failed to get org.bukkit.craftbukkit class: %s", classPath), e);
			return null;
		}
	}

	public static boolean checkClassExists(final @NotNull String classPath) {
		try {
			Class.forName(classPath);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}