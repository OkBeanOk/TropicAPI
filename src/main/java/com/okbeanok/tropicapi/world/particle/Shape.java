package com.okbeanok.tropicapi.world.particle;

import org.bukkit.Location;

import java.util.List;

public interface Shape {
    List<Location> points(Location origin, long tick, Particles context);
}