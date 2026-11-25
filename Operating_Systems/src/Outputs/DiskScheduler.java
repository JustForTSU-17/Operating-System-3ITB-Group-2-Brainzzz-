
package shared;

import java.util.List;

public interface DiskScheduler {
    DiskResult schedule(List<Integer> requests, int head, int diskSize, boolean right);
}
