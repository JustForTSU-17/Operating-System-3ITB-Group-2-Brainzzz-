package Outputs;


import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


/**
 * Algorithm: Priority Preemptive CPU Scheduling
 * Owner: Jared Maqkenzy Leonardo
 * Rules: Lower priority number wins; ties use earlier arrival, then lower PID.
 * Preemption happens only when a strictly higher-priority process arrives.
 */


public class PriorityPreemptive extends JPanel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Priority Preemptive Scheduling");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(new PriorityPreemptive());
            window.setSize(1050, 650);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
    
    
    // The algo.

    static final class PriorityPreemptiveScheduler implements CPUScheduler {
        @Override
        public ScheduleResult schedule(List<Job> jobs) {
            if (jobs == null || jobs.isEmpty() || jobs.size() > 100) {
                throw new IllegalArgumentException("Enter 1 to 100 processes.");
            }
            Set<Integer> usedPids = new HashSet<>();
            for (int i = 0; i < jobs.size(); i++) {
            	Job job = jobs.get(i);
                if (job == null || job.pid() <= 0 || job.arrival() < 0 || job.burst() <= 0 || job.priority() < 0 || !usedPids.add(job.pid())) {
                    throw new IllegalArgumentException("Invalid or duplicate process.");
                }
            }
            int count = jobs.size();
            int[] remaining = new int[count];
            long[] first = new long[count];
            long[] completed = new long[count];
            Arrays.fill(first, -1); // -1 means hindi pa nagrurun.
            for (int i = 0; i < count; i++) remaining[i] = jobs.get(i).burst();
            List<Segment> timeline = new ArrayList<>();
            long time = 0;
            int finished = 0;
            while (finished < count) {
                int chosen = findReadyProcess(jobs, remaining, time); // Highest-priority ready process.
                long nextArrival = findNextArrival(jobs, remaining, time);
                long nextTime;
                if (chosen == -1) {
                    nextTime = nextArrival; // Walang ready? Idle until next arrival. so in a case where natapos ung process and it took like 5 seconds idle time sya
                } else {
                    nextTime = Math.min(time + remaining[chosen], nextArrival); // Finish or next arrival.
                }
                if (nextTime <= time) throw new IllegalStateException("Scheduling did not advance.");
                recordSegment(timeline, chosen, time, nextTime);
                if (chosen != -1) {
                    if (first[chosen] == -1) first[chosen] = time;
                    completed[chosen] = nextTime;
                    remaining[chosen] -= (int) (nextTime - time);
                    if (remaining[chosen] == 0) finished++;
                }
                time = nextTime;
            }
            return new ScheduleResult(List.copyOf(jobs), List.copyOf(timeline), first, completed);
        }
        private int findReadyProcess(List<Job> jobs, int[] remaining, long time) {
            int chosen = -1;
            for (int i = 0; i < jobs.size(); i++) {
                if (remaining[i] == 0 || jobs.get(i).arrival() > time) continue;
                if (chosen == -1 || higherPriority(jobs.get(i), jobs.get(chosen))) chosen = i;
            }
            return chosen;
        }
        private long findNextArrival(List<Job> jobs, int[] remaining, long time) {
            long next = Long.MAX_VALUE;
            for (int i = 0; i < jobs.size(); i++) {
                if (remaining[i] > 0 && jobs.get(i).arrival() > time) {
                    next = Math.min(next, jobs.get(i).arrival());
                }
            }
            return next;
        }
        private void recordSegment(List<Segment> timeline, int index, long start, long end) {
            int last = timeline.size() - 1;
            if (last >= 0 && timeline.get(last).jobIndex() == index) { // Same process? Merge blocks.
                Segment old = timeline.get(last);
                timeline.set(last, new Segment(index, old.start(), end));
            } else {
                timeline.add(new Segment(index, start, end));
            }
        }
        private boolean higherPriority(Job candidate, Job current) {
            if (candidate.priority() != current.priority()) {
                return candidate.priority() < current.priority(); // Lower priority number wins.
            }
            if (candidate.arrival() != current.arrival()) {
                return candidate.arrival() < current.arrival(); // Tie? Earlier AT wins.
            }
            return candidate.pid() < current.pid(); // Same AT? Lower PID wins.
        }
    }
    // the gui
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"PID", "Arrival", "Burst", "Priority"}, 0);
    private final JTable inputTable = new JTable(tableModel);
    private final JTextField processCount = new JTextField("10", 4);
    private final JTextArea output = new JTextArea();
    private final GanttChart gantt = new GanttChart();
    private ScheduleResult result;

    public PriorityPreemptive() {
        setLayout(new BorderLayout(8, 8));
        add(new JLabel("Priority Preemptive (lower number = higher priority)"), BorderLayout.NORTH);
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setPreferredSize(new Dimension(350, 500));
        inputPanel.add(new JScrollPane(inputTable), BorderLayout.CENTER);
        JPanel controls = new JPanel(new GridLayout(3, 2, 5, 5));
        JButton generate = new JButton("Generate");
        JButton sample = new JButton("Load Sample Data");
        JButton run = new JButton("Run");
        JButton reset = new JButton("Reset/Clear");
        controls.add(new JLabel("Number of processes:"));
        controls.add(processCount);
        controls.add(generate);
        controls.add(sample);
        controls.add(run);
        controls.add(reset);
        inputPanel.add(controls, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.WEST);
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane chartScroll = new JScrollPane(gantt);
        chartScroll.setPreferredSize(new Dimension(500, 130));
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(chartScroll, BorderLayout.NORTH);
        resultsPanel.add(new JScrollPane(output), BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.CENTER);
        generate.addActionListener(e -> generateRows());
        sample.addActionListener(e -> loadSample());
        run.addActionListener(e -> runSimulation());
        reset.addActionListener(e -> reset());
        generateRows();
    }

    private void generateRows() {
        try {
            int count = Integer.parseInt(processCount.getText().trim());
            if (count < 1 || count > 100) {
                throw new IllegalArgumentException("Enter 1 to 100 processes.");
            }
            tableModel.setRowCount(0);
            for (int pid = 1; pid <= count; pid++) {
                tableModel.addRow(new Object[]{pid, 0, 1, 1});
            }
            clearResults();
        } catch (NumberFormatException e) {
            showError("Whole numbers only.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void loadSample() {
        // Demo sample
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{1, 0, 8, 4});
        tableModel.addRow(new Object[]{2, 1, 4, 2});
        tableModel.addRow(new Object[]{3, 2, 2, 1});
        tableModel.addRow(new Object[]{4, 3, 6, 5});
        tableModel.addRow(new Object[]{5, 4, 3, 3});
        tableModel.addRow(new Object[]{6, 6, 5, 2});
        tableModel.addRow(new Object[]{7, 8, 1, 1});
        tableModel.addRow(new Object[]{8, 10, 7, 4});
        tableModel.addRow(new Object[]{9, 12, 2, 3});
        tableModel.addRow(new Object[]{10, 14, 4, 2});
        processCount.setText("10");
        clearResults();
    }

    private void reset() {
        processCount.setText("10");
        generateRows();
    }

    private void clearResults() {
        result = null;
        output.setText("Enter processes and click Run.");
        gantt.refresh();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private int readNumber(int row, int column, int min, int max) {
        Object cell = tableModel.getValueAt(row, column);
        String text = cell == null ? "" : cell.toString().trim();
        if (text.isEmpty()) throw new IllegalArgumentException("Please fill in all fields.");

        try {
            long number = Long.parseLong(text);
            if (number < 0) throw new IllegalArgumentException("Negative numbers are not allowed.");
            if (number < min) throw new IllegalArgumentException("PID and Burst must be positive.");
            if (number > max) throw new IllegalArgumentException("Number is too large.");
            return (int) number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Whole numbers only.");
        }
    }

    private void runSimulation() {
        if (inputTable.isEditing() && !inputTable.getCellEditor().stopCellEditing()) {
            return; // Include ung last value na tina-type.
        }
        clearResults(); // Hide ung old results bago mag-run ulit.
        try {
            List<Job> jobs = new ArrayList<>();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                int pid = readNumber(row, 0, 1, Integer.MAX_VALUE);
                int arrival = readNumber(row, 1, 0, 10000);
                int burst = readNumber(row, 2, 1, 10000);
                int priority = readNumber(row, 3, 0, Integer.MAX_VALUE);
                jobs.add(new Job(pid, arrival, burst, priority));
            }
            result = new PriorityPreemptiveScheduler().schedule(jobs); // Algorithm is separate sa GUI.
            showResults();
            gantt.refresh();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void showResults() {
        StringBuilder text = new StringBuilder();
        text.append(String.format("%-6s %5s %5s %5s %5s %5s %5s %5s%n",
                "PID", "AT", "BT", "PRI", "CT", "TAT", "WT", "RT"));
        for (int i = 0; i < result.jobs().size(); i++) {
            Job job = result.jobs().get(i);
            text.append(String.format("%-6s %5d %5d %5d %5d %5d %5d %5d%n",
                    "P" + job.pid(), job.arrival(), job.burst(), job.priority(),
                    result.completion(i), result.turnaround(i), result.waiting(i), result.response(i)));
        }
        text.append(String.format(Locale.US,
                "%nAverage TAT: %.2f%nAverage WT: %.2f%nAverage RT: %.2f%n"
                + "CPU utilization: %.2f%%%nThroughput: %.4f processes/time unit%n",
                result.averageTurnaround(), result.averageWaiting(), result.averageResponse(),
                result.utilization(), result.throughput()));
        output.setText(text.toString());
    }

    private final class GanttChart extends JPanel {
        private static final int SCALE = 32;
        private static final int MAX_WIDTH = 30000;
        private double pixelsPerUnit() {
            if (result == null) return SCALE;
            return Math.min(SCALE, (MAX_WIDTH - 36.0) / result.totalTime()); // Compress pag mahaba.
        }
        void refresh() {
            long total = result == null ? 0 : result.totalTime();
            int width = (int) Math.ceil(36 + total * pixelsPerUnit());
            setPreferredSize(new Dimension(Math.max(600, width), 100));
            revalidate();
            repaint();
        }
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (result == null) return;
            Graphics2D pen = (Graphics2D) graphics.create();
            double scale = pixelsPerUnit();
            int lastLabelEnd = -1;
            for (Segment segment : result.segments()) {
                int left = 18 + (int) Math.round(segment.start() * scale);
                int right = 18 + (int) Math.round(segment.end() * scale);
                int width = Math.max(1, right - left); // Time-scaled ung bawat block.
                boolean idle = segment.jobIndex() == -1;
                pen.setColor(idle ? Color.LIGHT_GRAY : Color.WHITE);
                pen.fillRect(left, 20, width, 40);
                pen.setColor(Color.BLACK);
                pen.drawRect(left, 20, width, 40);
                String name = idle ? "IDLE" : "P" + result.jobs().get(segment.jobIndex()).pid();
                if (pen.getFontMetrics().stringWidth(name) + 10 <= width) {
                    pen.drawString(name, left + 5, 45);
                }
                String time = Long.toString(segment.start());
                if (left > lastLabelEnd + 5) { // Skip labels pag mag-ooverlap.
                    pen.drawString(time, left, 80);
                    lastLabelEnd = left + pen.getFontMetrics().stringWidth(time);
                }
            }
            String end = Long.toString(result.totalTime());
            int endX = 18 + (int) Math.round(result.totalTime() * scale)
                    - pen.getFontMetrics().stringWidth(end);
            if (endX > lastLabelEnd + 5) pen.drawString(end, endX, 80);
            pen.dispose();
        }
    }
}
record Job(int pid, int arrival, int burst, int priority) { }
record Segment(int jobIndex, long start, long end) { } // -1 = IDLE.
interface CPUScheduler {
    ScheduleResult schedule(List<Job> jobs);
}
// Result calculations
record ScheduleResult(List<Job> jobs, List<Segment> segments, long[] firstStart, long[] completed) {
    long completion(int i) { return completed[i]; }
    long turnaround(int i) { return completion(i) - jobs.get(i).arrival(); } // TAT = CT - AT.
    long waiting(int i) { return turnaround(i) - jobs.get(i).burst(); } // WT = TAT - BT.
    long response(int i) { return firstStart[i] - jobs.get(i).arrival(); } // RT = first start - AT.
    long totalTime() { return segments.get(segments.size() - 1).end(); }
    long busyTime() {
        long total = 0;
        for (Job job : jobs) total += job.burst();
        return total;
    }
    double utilization() { return 100.0 * busyTime() / totalTime(); }
    double throughput() { return (double) jobs.size() / totalTime(); }
    double averageTurnaround() {
        long total = 0;
        for (int i = 0; i < jobs.size(); i++) total += turnaround(i);
        return (double) total / jobs.size();
    }
    double averageWaiting() {
        long total = 0;
        for (int i = 0; i < jobs.size(); i++) total += waiting(i);
        return (double) total / jobs.size();
    }
    double averageResponse() {
        long total = 0;
        for (int i = 0; i < jobs.size(); i++) total += response(i);
        return (double) total / jobs.size();
    }
}
