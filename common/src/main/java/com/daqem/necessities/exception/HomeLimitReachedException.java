package com.daqem.necessities.exception;

public class HomeLimitReachedException extends Exception {

    public HomeLimitReachedException() {
        super("The home limit has been reached.");
    }
}
