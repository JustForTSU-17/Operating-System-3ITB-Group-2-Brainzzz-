
package Outputs;

import java.util.ArrayList;
import java.util.List;
import shared.CPUProcess;
import shared.CPUResult;

// Connect ung Priority Preemptive sa shared interface.
public class PriorityAdapter implements shared.CPUScheduler {

    @Override
    public CPUResult schedule(List<CPUProcess> processes) {

        List<Job> jobs = new ArrayList<>();

        // Convert shared inputs to our Job format.
        for (CPUProcess p : processes) {
            jobs.add(new Job(p.pid(), p.arrival(), p.burst(), p.priority()));
        }

        // Call ung existing Priority Preemptive algorithm.
        ScheduleResult answer =
            new PriorityPreemptive.PriorityPreemptiveScheduler().schedule(jobs);

        // Convert the results to shared format.
        List<CPUResult.ProcessResult> rows = new ArrayList<>();

        for (int i = 0; i < jobs.size(); i++) {
            rows.add(new CPUResult.ProcessResult(
                processes.get(i),
                answer.completion(i),
                answer.turnaround(i),
                answer.waiting(i),
                answer.response(i)
            ));
        }

        // Convert Gantt timeline; -1 means IDLE.
        List<CPUResult.Segment> chart = new ArrayList<>();

        for (Segment s : answer.segments()) {
            int pid = s.jobIndex() == -1
                ? -1 : jobs.get(s.jobIndex()).pid();

            chart.add(new CPUResult.Segment(
                pid, s.start(), s.end()
            ));
        }

        return new CPUResult(
            rows, chart,
            answer.averageWaiting(),
            answer.averageTurnaround(),
            answer.averageResponse(),
            answer.utilization(),
            answer.throughput()
        );
    }
}
