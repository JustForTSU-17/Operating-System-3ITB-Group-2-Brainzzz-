
package shared;

import java.util.List;

public record CPUResult(
        List<ProcessResult> processes,
        List<Segment> timeline,
        double averageWaiting,
        double averageTurnaround,
        double averageResponse,
        double utilization,
        double throughput
) {
    public record ProcessResult(
            CPUProcess process,
            long completion,
            long turnaround,
            long waiting,
            long response
    ) {}

    public record Segment(int pid, long start, long end) {}
}
