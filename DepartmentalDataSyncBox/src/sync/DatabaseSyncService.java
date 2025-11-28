package sync;

import dao.UserDAO;
import dao.RecordDAO;
import model.User;
import model.Record;
import util.SerializationUtil;
import util.NodeConfig;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the application of incoming SyncPayloads to the local database
 * (INBOUND).
 */
public class DatabaseSyncService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseSyncService.class.getName());
    private final ConflictResolver conflictResolver = new ConflictResolver();

    private final UserDAO userDAO = new UserDAO();
    private final RecordDAO recordDAO = new RecordDAO();

    /**
     * Applies a single incoming payload to the local database.
     */
    public boolean applyPayload(SyncPayload payload) {
        // Idempotency check: Ignore if source is self
        if (payload.getSourceActorId().equals(NodeConfig.NODE_ID)) {
            return true;
        }

        try {
            switch (payload.getEntityName()) {
                case "User":
                    return processUserSync(payload);
                case "Record":
                    return processRecordSync(payload);
                default:
                    LOGGER.warning("Unknown entity type: " + payload.getEntityName());
                    return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error applying sync payload for " + payload.getSyncUUID(), e);
            return false;
        }
    }

    private boolean processUserSync(SyncPayload payload) throws SQLException {
        User incomingUser = SerializationUtil.deserialize(payload.getPayloadJson(), User.class);
        User localUser = userDAO.findBySyncUUID(payload.getSyncUUID());

        if (localUser == null) {
            LOGGER.info("Applying INBOUND CREATE for User: " + incomingUser.getUsername());
            return userDAO.applyIncomingSync(incomingUser);
        } else {
            if (conflictResolver.resolveConflict(localUser.getLastModifiedTs(), payload)) {
                LOGGER.info("Applying INBOUND UPDATE (Winner) for User: " + incomingUser.getUsername());
                return userDAO.applyIncomingSync(incomingUser);
            } else {
                LOGGER.info("Ignoring INBOUND UPDATE (Loser) for User: " + incomingUser.getUsername());
                return true;
            }
        }
    }

    private boolean processRecordSync(SyncPayload payload) throws SQLException {
        Record incomingRecord = SerializationUtil.deserialize(payload.getPayloadJson(), Record.class);
        Record localRecord = recordDAO.findBySyncUUID(payload.getSyncUUID());

        if (localRecord == null) {
            LOGGER.info("Applying INBOUND CREATE for Record: " + incomingRecord.getStudentIdNo());
            return recordDAO.applyIncomingSync(incomingRecord);
        } else {
            if (conflictResolver.resolveConflict(localRecord.getLastModifiedTs(), payload)) {
                LOGGER.info("Applying INBOUND UPDATE (Winner) for Record: " + incomingRecord.getStudentIdNo());
                return recordDAO.applyIncomingSync(incomingRecord);
            } else {
                LOGGER.info("Ignoring INBOUND UPDATE (Loser) for Record: " + incomingRecord.getStudentIdNo());
                return true;
            }
        }
    }
}