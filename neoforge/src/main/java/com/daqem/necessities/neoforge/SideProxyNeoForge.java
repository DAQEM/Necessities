package com.daqem.necessities.neoforge;

import com.daqem.necessities.Necessities;

public class SideProxyNeoForge {

    public SideProxyNeoForge() {
        Necessities.init();
    }

    public static class Client extends SideProxyNeoForge {

        public Client() {
            //Run client code
        }

    }

    public static class Server extends SideProxyNeoForge {

        public Server() {
            //Run server code
        }
    }
}
