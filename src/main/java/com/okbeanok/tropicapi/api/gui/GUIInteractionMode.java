package com.okbeanok.tropicapi.api.gui;

public enum GUIInteractionMode {
	/**
	 * Players cannot move items in or out of the GUI.
	 * Best for menu-style GUIs.
	 */
	LOCKED,

	/**
	 * Players can add/remove/move items.
	 * Best for storage, item input, or editor GUIs.
	 */
	EDITABLE
}