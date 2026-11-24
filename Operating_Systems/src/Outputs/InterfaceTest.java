
package Outputs;

import java.util.List;
import shared.CPUScheduler;
import shared.CPUProcess;
import shared.CPUResult;

public class InterfaceTest {

    public static void main(String[] args) {

        // Same inputs for both algorithms.
        List<CPUProcess> jobs = List.of(
            new CPUProcess(1, 0, 5, 3),
            new CPUProcess(2, 2, 2, 1),
            new CPUProcess(3, 3, 1, 2)
        );

        // Both use the SAME shared interface.
        CPUScheduler priority = new PriorityAdapter();
        CPUScheduler sjf = new SJFAdapter();

        CPUResult p = priority.schedule(jobs);
        CPUResult s = sjf.schedule(jobs);

        System.out.println("PRIORITY PREEMPTIVE");
        System.out.println("Timeline: " + p.timeline());
        System.out.printf("Average WT: %.2f%n", p.averageWaiting());

        System.out.println("\nSJF NON-PREEMPTIVE");
        System.out.println("Timeline: " + s.timeline());
        System.out.printf("Average WT: %.2f%n", s.averageWaiting());

        // Verify the expected scheduling sequences.
        boolean priorityCorrect = p.timeline().equals(List.of(
            new CPUResult.Segment(1, 0, 2),
            new CPUResult.Segment(2, 2, 4),
            new CPUResult.Segment(3, 4, 5),
            new CPUResult.Segment(1, 5, 8)
        ));

        boolean sjfCorrect = s.timeline().equals(List.of(
            new CPUResult.Segment(1, 0, 5),
            new CPUResult.Segment(3, 5, 6),
            new CPUResult.Segment(2, 6, 8)
        ));

        if (!priorityCorrect || !sjfCorrect) {
            throw new AssertionError("Scheduling result mismatch.");
        }

        System.out.println("\nPASS: Both algorithms work through CPUScheduler.");
    }
}
