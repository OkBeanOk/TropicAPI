package com.okbeanok.tropicapi.api.event;

import java.util.function.Consumer;

/**
 * Very light internal event bus for Tropic ecosystem.
 */
public interface TropicEventBus {

	<T> void registerListener(Class<T> eventType, Consumer<T> listener);

	<T> void unregisterListener(Class<T> eventType, Consumer<T> listener);

	void post(Object event);
}