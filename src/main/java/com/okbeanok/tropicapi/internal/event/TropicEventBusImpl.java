package com.okbeanok.tropicapi.internal.event;

import com.okbeanok.tropicapi.api.event.TropicEventBus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TropicEventBusImpl implements TropicEventBus {

	private final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

	@Override
	public <T> void registerListener(Class<T> eventType, Consumer<T> listener) {
		listeners.computeIfAbsent(eventType, c -> new java.util.concurrent.CopyOnWriteArrayList<>())
				.add(listener);
	}

	@Override
	public <T> void unregisterListener(Class<T> eventType, Consumer<T> listener) {
		List<Consumer<?>> list = listeners.get(eventType);
		if (list != null) {
			list.remove(listener);
		}
	}

	@Override
	public void post(Object event) {
		if (event == null) return;
		Class<?> type = event.getClass();
		List<Consumer<?>> list = listeners.get(type);
		if (list == null || list.isEmpty()) return;

		for (Consumer<?> consumer : list) {
			@SuppressWarnings("unchecked")
			Consumer<Object> c = (Consumer<Object>) consumer;
			c.accept(event);
		}
	}
}