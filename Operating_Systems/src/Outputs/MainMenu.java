package Outputs;

import java.awt.*;
import javax.swing.*;

/**
 * Integrated main menu for Group 2 - Brainzzz.
 * Launches the group's existing SJF, Priority Preemptive and C-LOOK GUIs
 * without changing the algorithms or their on-screen interfaces.
 */

public class MainMenu extends JFrame {

    // SJF and C-LOOK are standalone JFrame-based modules.
    // Start each of them once, then reuse the window after it is hidden.
    private boolean sjfOpened = false;
    private boolean clookOpened = false;

    // Priority Preemptive is a JPanel, so we place it in a JFrame ourselves.
    private JFrame priorityWindow;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }

    public MainMenu() {
        setTitle("OS Scheduling Simulator");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        Color green = new Color(231, 237, 220);
        Color pink = new Color(248, 225, 229);

        JLabel title = new JLabel("OS Scheduling Simulator", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel categories = new JPanel(new GridLayout(1, 2, 15, 0));
        categories.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel cpu = new JPanel(new GridLayout(3, 1, 10, 10));
        cpu.setBackground(green);
        cpu.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel cpuTitle = new JLabel("CPU Scheduling", JLabel.CENTER);
        cpuTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JButton sjfButton = new JButton("Shortest Job First (SJF)");
        JButton priorityButton = new JButton("Priority Preemptive");
        cpu.add(cpuTitle);
        cpu.add(sjfButton);
        cpu.add(priorityButton);

        JPanel disk = new JPanel(new GridLayout(3, 1, 10, 10));
        disk.setBackground(pink);
        disk.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel diskTitle = new JLabel("Disk Scheduling", JLabel.CENTER);
        diskTitle.setFont(new Font("Arial", Font.BOLD, 18));
        JButton clookButton = new JButton("C-LOOK");
        disk.add(diskTitle);
        disk.add(clookButton);
        disk.add(new JLabel(""));

        categories.add(cpu);
        categories.add(disk);
        add(categories, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton aboutButton = new JButton("About");
        JButton exitButton = new JButton("Exit");
        bottom.add(aboutButton);
        bottom.add(exitButton);
        add(bottom, BorderLayout.SOUTH);

        sjfButton.addActionListener(e -> openSJF());
        priorityButton.addActionListener(e -> openPriority());
        clookButton.addActionListener(e -> openCLOOK());

        aboutButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "OS Scheduling Simulator\n\n" +
                "Group 2 - Brainzzz (3ITB)\n\n" +
                "Members:\n" +
                "Jared Maqkenzy Leonardo - Priority Preemptive\n" +
                "Love Margaret G. Leonado - Shortest Job First\n" +
                "Andrei Benedict B. Manalad - C-LOOK",
                "About", JOptionPane.INFORMATION_MESSAGE));

        exitButton.addActionListener(e -> System.exit(0));
    }

    private void openSJF() {
        if (!sjfOpened) {
            SJF.main(new String[0]);
            // The existing standalone SJF.main() sets EXIT_ON_CLOSE.
            // Override it so closing the SJF window does not exit this app.
            SJF.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            sjfOpened = true;
        }
        showWindow(SJF.frame);
    }

    private void openPriority() {
        if (priorityWindow == null) {
            priorityWindow = new JFrame("Priority Preemptive Scheduling");
            priorityWindow.setContentPane(new PriorityPreemptive());
            priorityWindow.setSize(1050, 650);
            priorityWindow.setLocationRelativeTo(this);
            priorityWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        showWindow(priorityWindow);
    }

    private void openCLOOK() {
        if (!clookOpened) {
            CLOOK.main(new String[0]);
            // The existing standalone CLOOK.main() sets EXIT_ON_CLOSE.
            CLOOK.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            clookOpened = true;
        }
        showWindow(CLOOK.frame);
    }

    private static void showWindow(JFrame window) {
        window.setVisible(true);
        window.setState(Frame.NORMAL);
        window.toFront();
        window.requestFocus();
    }
}
