
package Outputs;

import java.util.*;
import shared.CPUProcess;
import shared.CPUResult;

// Connect ung existing SJF sa shared CPU interface.
public class SJFAdapter implements shared.CPUScheduler {

    @Override
    public CPUResult schedule(List<CPUProcess> processes) {

        if (processes == null || processes.isEmpty() || processes.size() > 20) {
            throw new IllegalArgumentException("Enter 1 to 20 processes.");
        }

        List<CPUProcess> sorted = new ArrayList<>(processes);
        Set<Integer> used = new HashSet<>();

        for (CPUProcess p : sorted) {
            if (p == null || p.pid() <= 0 || !used.add(p.pid())) {
                throw new IllegalArgumentException("Invalid or duplicate PID.");
            }
        }

        // Sort by PID para tama ung tie-breaking.
        sorted.sort(Comparator.comparingInt(CPUProcess::pid));

        int n = sorted.size();
        int[] at = new int[n];
        int[] bt = new int[n];
        Map<Integer, Integer> positions = new HashMap<>();

        for (int i = 0; i < n; i++) {
            CPUProcess p = sorted.get(i);
            at[i] = p.arrival();
            bt[i] = p.burst();
            positions.put(p.pid(), i);
        }

        // Gamitin ung original SJF algorithm.
        SJFResult answer = SJFAlgorithm.calculate(at, bt);

        List<CPUResult.ProcessResult> rows = new ArrayList<>();

        for (CPUProcess p : processes) {
            int i = positions.get(p.pid());

            rows.add(new CPUResult.ProcessResult(
                p, answer.ct[i], answer.tat[i],
                answer.wt[i], answer.rt[i]
            ));
        }

        // Convert Gantt blocks into shared format.
        List<CPUResult.Segment> chart = new ArrayList<>();

        for (SJFSegment step : answer.timeline) {
            int pid = step.process.equals("Idle") ? -1
                : sorted.get(Integer.parseInt(step.process.substring(1)) - 1).pid();

            chart.add(new CPUResult.Segment(pid, step.start, step.end));
        }

        return new CPUResult(
            rows, chart,
            answer.avgWT,
            answer.avgTAT,
            answer.avgRT,
            answer.utilization,
            answer.throughput
        );
    }
}
