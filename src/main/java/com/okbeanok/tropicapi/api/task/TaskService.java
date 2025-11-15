package com.okbeanok.tropicapi.api.task;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface TaskService {

	void runSync(Runnable task);

	void runAsync(Runnable task);

	void runLaterSync(Runnable task, long delayTicks);

	void runTimerSync(Runnable task, long delayTicks, long periodTicks);

	<T> CompletableFuture<T> supplyAsync(Supplier<T> supplier);

	/**
	 * Access to the owning plugin, if needed.
	 */
	Plugin getOwner();
}