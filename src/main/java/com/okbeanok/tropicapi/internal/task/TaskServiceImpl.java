package com.okbeanok.tropicapi.internal.task;

import com.okbeanok.tropicapi.api.task.TaskService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TaskServiceImpl implements TaskService {

	private final Plugin owner;

	public TaskServiceImpl(Plugin owner) {
		this.owner = owner;
	}

	@Override
	public void runSync(Runnable task) {
		Bukkit.getScheduler().runTask(owner, task);
	}

	@Override
	public void runAsync(Runnable task) {
		Bukkit.getScheduler().runTaskAsynchronously(owner, task);
	}

	@Override
	public void runLaterSync(Runnable task, long delayTicks) {
		Bukkit.getScheduler().runTaskLater(owner, task, delayTicks);
	}

	@Override
	public void runTimerSync(Runnable task, long delayTicks, long periodTicks) {
		Bukkit.getScheduler().runTaskTimer(owner, task, delayTicks, periodTicks);
	}

	@Override
	public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, r -> runAsync(r));
	}

	@Override
	public Plugin getOwner() {
		return owner;
	}
}
