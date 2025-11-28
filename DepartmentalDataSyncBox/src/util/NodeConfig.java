package util;

import java.net.InetAddress;
import java.util.UUID;

public class NodeConfig {
    // Unique ID for this node (machine). In production, this should be persisted in a config file.
    // For now, we generate a random one per session if not fixed, but to ensure consistency across restarts
    // without a config file, we can derive it from the hostname or MAC, or just use a fixed ID for testing.
    // User requested "paste and run", so let's try to be stable.
    public static final String NODE_ID;

    static {
        String id;
        try {
            id = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            id = UUID.randomUUID().toString();
        }
        NODE_ID = id;
    }
}