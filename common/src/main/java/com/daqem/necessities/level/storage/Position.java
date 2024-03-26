package com.daqem.necessities.level.storage;

public class Position {

    public static final Position ZERO = new Position(0, 0, 0, 0, 0);

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public Position(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
