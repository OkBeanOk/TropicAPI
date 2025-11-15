package com.okbeanok.tropicapi.api.task;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class Tasks {

	private static TaskService service;

	private Tasks() {
	}

	public static void init(TaskService taskService) {
		service = taskService;
	}

	private static TaskService s() {
		if (service == null) {
			throw new IllegalStateException("Tasks not initialized. Is TropicAPI enabled?");
		}
		return service;
	}

	public static void runSync(Runnable task) {
		s().runSync(task);
	}

	public static void runAsync(Runnable task) {
		s().runAsync(task);
	}

	public static void runLaterSync(Runnable task, long delayTicks) {
		s().runLaterSync(task, delayTicks);
	}

	public static void runTimerSync(Runnable task, long delayTicks, long periodTicks) {
		s().runTimerSync(task, delayTicks, periodTicks);
	}

	public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
		return s().supplyAsync(supplier);
	}
}