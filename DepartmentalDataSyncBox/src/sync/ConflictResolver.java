package sync;

import util.NodeConfig;
import java.util.logging.Logger;

/**
 * Implements the timestamp-based conflict resolution logic.
 * Strategy: Last-Write-Wins, with Node ID (lexicographical) as tie-breaker.
 */
public class ConflictResolver {
    private static final Logger LOGGER = Logger.getLogger(ConflictResolver.class.getName());

    /**
     * @param localTimestamp  The LAST_MODIFIED_TS of the local record.
     * @param incomingPayload The incoming SyncPayload.
     * @return true if the incoming payload wins and should be applied, false
     *         otherwise.
     */
    public boolean resolveConflict(long localTimestamp, SyncPayload incomingPayload) {
        long incomingTimestamp = incomingPayload.getLastModifiedTs();
        String localNodeId = NodeConfig.NODE_ID;
        String incomingNodeId = incomingPayload.getSourceActorId();

        if (incomingTimestamp > localTimestamp) {
            // Incoming is newer (Last-Write-Wins)
            return true;
        } else if (incomingTimestamp < localTimestamp) {
            // Local is newer
            return false;
        } else {
            // Timestamps are equal: Use Node ID as a tie-breaker
            if (incomingNodeId.compareTo(localNodeId) > 0) {
                LOGGER.info(
                        "Timestamp tie resolved: Incoming Node " + incomingNodeId + " wins over local " + localNodeId);
                return true;
            } else {
                LOGGER.info(
                        "Timestamp tie resolved: Local Node " + localNodeId + " wins over incoming " + incomingNodeId);
                return false;
            }
        }
    }
}