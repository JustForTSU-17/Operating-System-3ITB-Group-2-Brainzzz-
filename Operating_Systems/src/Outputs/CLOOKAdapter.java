
package Outputs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import shared.DiskResult;
import shared.DiskScheduler;

public class CLOOKAdapter implements DiskScheduler {

    @Override
    public DiskResult schedule(List<Integer> requests, int head,
                               int diskSize, boolean right) {

        if (diskSize <= 0 || head < 0 || head >= diskSize ||
            requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Invalid disk inputs.");
        }

        ArrayList<Integer> queue = new ArrayList<>();

        for (Integer track : requests) {
            if (track == null || track < 0 || track >= diskSize) {
                throw new IllegalArgumentException("Invalid track number.");
            }
            queue.add(track);
        }

        // Same sorting logic as the original C-LOOK.
        Collections.sort(queue);

        if (!right) {
            Collections.reverse(queue);
        }

        ArrayList<Integer> order = new ArrayList<>();
        order.add(head);

        // Service requests in the selected direction.
        for (int track : queue) {
            if ((right && track >= head) ||
                (!right && track <= head)) {
                order.add(track);
            }
        }

        // Jump to the other side for remaining requests.
        for (int track : queue) {
            if ((right && track < head) ||
                (!right && track > head)) {
                order.add(track);
            }
        }

        ArrayList<Integer> movements = new ArrayList<>();
        long total = 0;

        for (int i = 1; i < order.size(); i++) {
            int distance = (int) Math.abs(
                (long) order.get(i) - order.get(i - 1));

            movements.add(distance);
            total += distance;
        }

        return new DiskResult(
            head,
            order,
            movements,
            total,
            (double) total / queue.size()
        );
    }
}
