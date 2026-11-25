package shared;

import java.util.List;



public record DiskResult(
        int initialHead,
        List<Integer> seekSequence,
        List<Integer> headMovements,
        long totalMovement,
        double averageSeekDistance
) {
    public DiskResult {
        seekSequence = List.copyOf(seekSequence);
        headMovements = List.copyOf(headMovements);
    }
}
