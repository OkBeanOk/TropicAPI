package com.okbeanok.tropicapi.api.event;

import java.util.function.Consumer;

public final class TropicEvents {

	private static TropicEventBus bus;

	private TropicEvents() {
	}

	public static void init(TropicEventBus eventBus) {
		bus = eventBus;
	}

	private static TropicEventBus s() {
		if (bus == null) {
			throw new IllegalStateException("TropicEvents not initialized. Is TropicAPI enabled?");
		}
		return bus;
	}

	public static <T> void listen(Class<T> type, Consumer<T> listener) {
		s().registerListener(type, listener);
	}

	public static <T> void unlisten(Class<T> type, Consumer<T> listener) {
		s().unregisterListener(type, listener);
	}

	public static void post(Object event) {
		s().post(event);
	}
}