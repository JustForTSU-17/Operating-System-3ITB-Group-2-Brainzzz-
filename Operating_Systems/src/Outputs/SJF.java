package Outputs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Algorithm: Shortest Job First (Non-Preemptive)
 * Owner: Love Margaret Leonado
 * Conventions:
 * Shortest burst time will go first.
 * If the BT are the same, earlier AT will go first.
 * If the BT and AT are the same, lower PID will go first.
 * If there are no available processes, the CPU will remain idle.
 * Context switch time is 0.
 */

public class SJF {

    static JFrame frame = new JFrame("SJF Simulator");
    static JTextField number = new JTextField("10", 5);

    // Columns ng input at results table.
    static DefaultTableModel input = new DefaultTableModel(
        new String[]{"Process", "AT", "BT"}, 0) {

        // AT at BT lang ang pwedeng i-edit.
        @Override
        public boolean isCellEditable(int row, int column) {
            return column != 0;
        }
    };

    static DefaultTableModel result = new DefaultTableModel(
        new String[]{"Process", "AT", "BT", "CT", "TAT", "WT", "RT"}, 0);

    static JTable inputTable = new JTable(input);
    static JTable resultTable = new JTable(result);

    static JLabel avgCT = new JLabel("Average CT: 0.00"); 
    static JLabel avgWT = new JLabel("Average WT: 0.00");
    static JLabel avgTAT = new JLabel("Average TAT: 0.00");
    static JLabel avgRT = new JLabel("Average RT: 0.00");
    static JLabel cpuUtil = new JLabel("CPU Utilization: 0.00%");
    static JLabel throughput = new JLabel("Throughput: 0.0000");
    

    static JPanel gantt = new JPanel();

    static Color green = new Color(200, 217, 184);
    static Color pink = new Color(245, 223, 231);

    public static void main(String[] args) {

        // Main window ng simulator.
        frame.setSize(850, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Shortest Job First Algorithm");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);

        // Dito ung number of processes pati buttons.
        JPanel controls = new JPanel();
        controls.setBackground(Color.WHITE);

        JButton generate = new JButton("Generate");
        JButton calculate = new JButton("Calculate SJF");
        JButton sample = new JButton("Load Sample Data");
        JButton reset = new JButton("Reset");

        generate.setBackground(green);
        calculate.setBackground(green);
        sample.setBackground(green);
        reset.setBackground(pink);

        controls.add(new JLabel("Number of Processes:"));
        controls.add(number);
        controls.add(generate);
        controls.add(calculate);
        controls.add(sample);
        controls.add(reset);

        panel.add(controls);

        // Input table para sa AT at BT.
        inputTable.setRowHeight(30);
        inputTable.getTableHeader().setBackground(green);

        panel.add(new JLabel("Process Information"));
        panel.add(new JScrollPane(inputTable));

        // Results table, hindi na editable.
        resultTable.setRowHeight(30);
        resultTable.setEnabled(false);
        resultTable.getTableHeader().setBackground(green);

        panel.add(new JLabel("Scheduling Results"));
        panel.add(new JScrollPane(resultTable));

        // Layout para sa averages at CPU metrics.
        JPanel averages = new JPanel(new GridLayout(2, 3, 5, 5));

        avgCT.setOpaque(true); 
        avgCT.setBackground(pink); 
        avgCT.setHorizontalAlignment(JLabel.CENTER);
    
        avgWT.setOpaque(true);
        avgWT.setBackground(green);
        avgWT.setHorizontalAlignment(JLabel.CENTER);

        avgTAT.setOpaque(true);
        avgTAT.setBackground(pink);
        avgTAT.setHorizontalAlignment(JLabel.CENTER);

        avgRT.setOpaque(true);
        avgRT.setBackground(green);
        avgRT.setHorizontalAlignment(JLabel.CENTER);

        cpuUtil.setOpaque(true);
        cpuUtil.setBackground(pink);
        cpuUtil.setHorizontalAlignment(JLabel.CENTER);

        throughput.setOpaque(true);
        throughput.setBackground(green);
        throughput.setHorizontalAlignment(JLabel.CENTER);

        averages.add(avgCT); 
        averages.add(avgWT);
        averages.add(avgTAT);
        averages.add(avgRT);
        averages.add(cpuUtil);
        averages.add(throughput);

        averages.setPreferredSize(new Dimension(1000, 80));
        averages.setMinimumSize(new Dimension(300, 80));
        averages.setMaximumSize(new Dimension(1000, 80));

        panel.add(averages);

        // Horizontal layout para sa Gantt blocks.
        gantt.setBackground(Color.WHITE);
        gantt.setLayout(new BoxLayout(gantt, BoxLayout.X_AXIS));

        panel.add(new JLabel("Gantt Chart"));

        JScrollPane ganttScroll = new JScrollPane(gantt);
        ganttScroll.setPreferredSize(new Dimension(750, 110));
        ganttScroll.setMinimumSize(new Dimension(100, 110));

        panel.add(ganttScroll);

        // Function na tatawagin pag pinindot ung button.
        generate.addActionListener(e -> generateTable());
        calculate.addActionListener(e -> calculateSJF());
        sample.addActionListener(e -> loadSample());
        reset.addActionListener(e -> reset());

        // May 10 processes na agad pag open.
        generateTable();

        frame.add(panel);
        frame.setVisible(true);
    }

    static void generateTable() {

        try {
            int n = Integer.parseInt(number.getText().trim());

            // Minimum of 10, maximum of 20 processes lang.
            if (n < 1 || n > 20) {
                JOptionPane.showMessageDialog(
                    frame, "Enter 1 to 20 processes.");
                return;
            }

            // Clear muna ung previous rows at results.
            input.setRowCount(0);
            clearResults();

            // Default AT is 0, BT is 1.
            for (int i = 1; i <= n; i++) {
                input.addRow(new Object[]{"P" + i, 0, 1});
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                frame, "Enter a valid number.");
        }
    }

    static void loadSample() {

        // Clear muna ung previous data.
        input.setRowCount(0);
        clearResults();
        number.setText("10");

        // Sample PID, AT at BT.
        input.addRow(new Object[]{"P1", 0, 8});
        input.addRow(new Object[]{"P2", 1, 4});
        input.addRow(new Object[]{"P3", 2, 2});
        input.addRow(new Object[]{"P4", 3, 6});
        input.addRow(new Object[]{"P5", 4, 3});
        input.addRow(new Object[]{"P6", 6, 5});
        input.addRow(new Object[]{"P7", 8, 1});
        input.addRow(new Object[]{"P8", 10, 7});
        input.addRow(new Object[]{"P9", 12, 2});
        input.addRow(new Object[]{"P10", 14, 4});
    }

    static void calculateSJF() {

        // Save muna ung cell na currently ine-edit.
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }

        clearResults(); // So that if theres a case na nagrun ka ng error, hindi magdidisplay yung old output. 

        int n = input.getRowCount();

        int[] at = new int[n];
        int[] bt = new int[n];

        try {

            // Get ung AT at BT from the input table.
            for (int i = 0; i < n; i++) {

                at[i] = Integer.parseInt(
                    input.getValueAt(i, 1).toString().trim());

                bt[i] = Integer.parseInt(
                    input.getValueAt(i, 2).toString().trim());

                // AT can be zero pero BT dapat positive.
                if (at[i] < 0 || bt[i] <= 0 ||
                    at[i] > 1000000 || bt[i] > 1000000) {

                    JOptionPane.showMessageDialog(
                        frame, "Enter valid AT and BT (up to 1000000).");
                    return;
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                frame, "Please enter valid numbers.");
            return;
        }

        clearResults();

        // Pass ung inputs sa separate SJF algorithm.
        SJFResult answer = SJFAlgorithm.calculate(at, bt);

        // Display ung computed results.
        for (int i = 0; i < n; i++) {

            result.addRow(new Object[]{
                "P" + (i + 1),
                at[i],
                bt[i],
                answer.ct[i],
                answer.tat[i],
                answer.wt[i],
                answer.rt[i]
            });
        }

        // Display ung averages at CPU metrics.
        avgCT.setText(
            String.format("Average CT: %.2f", answer.avgCT));

        avgWT.setText(
            String.format("Average WT: %.2f", answer.avgWT));

        avgTAT.setText(
            String.format("Average TAT: %.2f", answer.avgTAT));

        avgRT.setText(
            String.format("Average RT: %.2f", answer.avgRT));

        cpuUtil.setText(
            String.format("CPU Utilization: %.2f%%",
                answer.utilization));

        throughput.setText(
            String.format("Throughput: %.4f",
                answer.throughput));

        // Adjust ung scale pag sobrang haba ng execution time.
        double scale = Math.min(80.0, 30000.0 / answer.totalTime);

        // Display ung saved Gantt segments.
        for (SJFSegment step : answer.timeline) {
            addGantt(step.process, step.start, step.end, scale);
        }

        gantt.revalidate();
        gantt.repaint();
    }

    static void addGantt(String process, int start, int end,
                         double scale) {

        // Two rows per block, process name at time.
        JPanel block = new JPanel(new GridLayout(2, 1));

        JLabel name = new JLabel(process, JLabel.CENTER);

        JLabel duration = new JLabel(
            start + " - " + end, JLabel.CENTER);

        // Alternate lang ung green at pink.
        if (gantt.getComponentCount() % 2 == 0) {
            block.setBackground(green);
        } else {
            block.setBackground(pink);
        }

        // Width ng block depende sa execution time.
        int width = Math.max(1,
            (int) Math.round((end - start) * scale));

        block.setPreferredSize(new Dimension(width, 60));
        block.setMinimumSize(new Dimension(width, 60));
        block.setMaximumSize(new Dimension(width, 60));

        block.setBorder(
            BorderFactory.createLineBorder(Color.WHITE));

        // Show ung process at time pag tinapatan ng mouse.
        block.setToolTipText(process + ": " + start + " - " + end);

        block.add(name);
        block.add(duration);

        gantt.add(block);
    }

    static void clearResults() {

        // Clear ung old results bago mag-calculate ulit.
        result.setRowCount(0);
        gantt.removeAll();

        avgCT.setText("Average CT: 0.00");
        avgWT.setText("Average WT: 0.00");
        avgTAT.setText("Average TAT: 0.00");
        avgRT.setText("Average RT: 0.00");
        cpuUtil.setText("CPU Utilization: 0.00%");
        throughput.setText("Throughput: 0.0000");

        gantt.revalidate();
        gantt.repaint();
    }

    static void reset() {

        // Balik lang sa default na 10 processes.
        number.setText("10");
        generateTable();
    }
}

// Storage ng computed results.
class SJFResult {

    int[] ct;
    int[] tat;
    int[] wt;
    int[] rt;

    double avgCT;
    double avgWT;
    double avgTAT;
    double avgRT;
    double utilization;
    double throughput;

    int totalTime;

    ArrayList<SJFSegment> timeline = new ArrayList<>();

    SJFResult(int n) {
        ct = new int[n];
        tat = new int[n];
        wt = new int[n];
        rt = new int[n];
    }
}

// Storage ng bawat Gantt block.
class SJFSegment {

    String process;
    int start;
    int end;

    SJFSegment(String process, int start, int end) {
        this.process = process;
        this.start = start;
        this.end = end;
    }
}

// Dito ung actual SJF logic, separate from GUI.
class SJFAlgorithm {

    static SJFResult calculate(int[] at, int[] bt) {

        // Check muna kung valid ung arrays.
        if (at == null || bt == null ||
            at.length != bt.length ||
            at.length == 0 || at.length > 20) {

            throw new IllegalArgumentException("Invalid inputs.");
        }

        int n = at.length;

        // AT dapat hindi negative, BT dapat positive.
        for (int i = 0; i < n; i++) {
            if (at[i] < 0 || bt[i] <= 0 ||
                at[i] > 1000000 || bt[i] > 1000000) {

                throw new IllegalArgumentException("Invalid AT or BT.");
            }
        }

        SJFResult answer = new SJFResult(n);

        // Track kung tapos na ung process.
        boolean[] done = new boolean[n];

        int time = 0;
        int completed = 0;

        double totalCT = 0;
        double totalWT = 0;
        double totalTAT = 0;
        double busyTime = 0;

        // Loop hanggang matapos lahat ng processes.
        while (completed < n) {

            // -1 kasi wala pang napipiling process.
            int shortest = -1;

            // Hanapin ung shortest available process.
            for (int i = 0; i < n; i++) {

                // Check kung arrived na at hindi pa tapos.
                if (!done[i] && at[i] <= time) {

                    if (shortest == -1 ||
                        bt[i] < bt[shortest] ||
                        (bt[i] == bt[shortest] &&
                         at[i] < at[shortest])) {

                        shortest = i;
                    }
                }
            }

            // Pag same ang BT at AT, lower PID ang mauuna
            // kasi naka-order na ung process IDs.

            // Pag walang available process, idle muna ung CPU.
            if (shortest == -1) {

                int next = Integer.MAX_VALUE;

                // Hanapin ung next arrival time.
                for (int i = 0; i < n; i++) {
                    if (!done[i] && at[i] < next) {
                        next = at[i];
                    }
                }

                answer.timeline.add(
                    new SJFSegment("Idle", time, next));

                // Diretso sa next arrival time.
                time = next;
                continue;
            }

            int start = time;

            // Buong BT agad kasi non-preemptive.
            time += bt[shortest];
            busyTime += bt[shortest];

            // Compute ung scheduling metrics.
            answer.ct[shortest] = time;
            answer.tat[shortest] = time - at[shortest];
            answer.wt[shortest] = answer.tat[shortest] - bt[shortest];

            // Same lang ang RT at WT sa non-preemptive SJF.
            answer.rt[shortest] = answer.wt[shortest];

            // Add sa totals para makuha ung averages later.
            totalCT += answer.ct[shortest];
            totalTAT += answer.tat[shortest];
            totalWT += answer.wt[shortest];

            // Mark as done para hindi na mapili ulit.
            done[shortest] = true;
            completed++;

            // Save ung process at execution time para sa Gantt.
            answer.timeline.add(
                new SJFSegment("P" + (shortest + 1), start, time));
        }

        // Divide ung totals by number of processes.
        answer.avgCT = totalCT / n;
        answer.avgTAT = totalTAT / n;
        answer.avgWT = totalWT / n;
        answer.avgRT = answer.avgWT;

        // Percentage ng time na busy ung CPU.
        answer.utilization = (busyTime / time) * 100;

        // Ilang processes ang natatapos per time unit.
        answer.throughput = (double) completed / time;

        answer.totalTime = time;

        // Return ung computed results sa GUI.
        return answer;
    }
}