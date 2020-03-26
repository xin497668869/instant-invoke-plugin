package com.xin.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class NetUtils {
    private static final int RND_PORT_START = 30000;

    private static final int RND_PORT_RANGE = 10000;

    public static int getRandomPort() {
        return RND_PORT_START + ThreadLocalRandom.current()
                                                 .nextInt(RND_PORT_RANGE);
    }

    public static int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
