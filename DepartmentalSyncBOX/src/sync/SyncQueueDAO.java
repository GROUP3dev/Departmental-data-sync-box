package sync;

import java.util.List;
import java.util.Optional;

/**
 * this java design pattern that separate database logic
 * DAO (data access object )
 *
 */
public interface SyncQueueDAO  {
    //
    int enqueue(SyncQueue item) throws Exception;
    List<SyncQueue> getPending(int limit ) throws  Exception;
    Optional<SyncQueue> findById(int syncId) throws Exception;
    boolean markProcessed(int syncId ) throws Exception;
    boolean updateError(int syncId , String errorMessage) throws Exception;
    boolean incrementRetry (int syncId) throws Exception;
    boolean delete(int syncId) throws Exception;

}


/**
 * | **Method**                                     | **Purpose**                                            | **What it does in DB**                                       |
 * | ---------------------------------------------- | ------------------------------------------------------ | ------------------------------------------------------------ |
 * | `enqueue(SyncQueue item)`                      | Adds a new sync task to the queue                      | `INSERT INTO sync_queue (...) VALUES (...)`                  |
 * | `getPending(int limit)`                        | Gets unprocessed sync tasks                            | `SELECT * FROM sync_queue WHERE processed_flag = 0`          |
 * | `findById(int syncId)`                         | Finds one specific queue entry by ID                   | `SELECT * FROM sync_queue WHERE sync_id = ?`                 |
 * | `markProcessed(int syncId)`                    | Marks a sync task as successfully processed            | `UPDATE sync_queue SET processed_flag=1, processed_at=NOW()` |
 * | `updateError(int syncId, String errorMessage)` | Records an error message if a sync fails               | `UPDATE sync_queue SET error_message=?`                      |
 * | `incrementRetry(int syncId)`                   | Increments retry count after failed attempt            | `UPDATE sync_queue SET retry_count = retry_count + 1`        |
 * | `delete(int syncId)`                           | Deletes a queue entry (e.g., after success or cleanup) | `DELETE FROM sync_queue WHERE sync_id=?`                     |
 *
 *
 *
 *
 */
