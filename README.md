University of Santo Tomas
Course: ICS26012 — Operating Systems
Section: 3ITB
Group: 2 — Brainzzz
Academic Year: 2026–2027, First Semester

1. Project Overview

This project is a Java-based graphical application that simulates CPU and disk scheduling algorithms. It allows users to input process information or disk requests, execute scheduling simulations, and visualize the resulting execution sequences and performance metrics.


The application integrates three scheduling algorithms into a single Main Menu:

Shortest Job First (SJF) — Non-Preemptive CPU Scheduling

Priority Preemptive — Preemptive CPU Scheduling

Circular LOOK (C-LOOK) — Disk Scheduling




2. System Requirements

Programming Language: Java

Recommended Development Environment: JDK 24

GUI Framework: Java Swing

Operating System: Windows or another Java-compatible operating system

External Libraries: None

Internet Connection: Not required

The application uses the Java Standard Library and does not require a database.

The Java runtime must be compatible with the version used to compile the executable JAR.




3. How to Run the Application


Option A: Run Using the Executable JAR (Recommended)

Download and extract the project ZIP file.

Open the Operating_Systems folder.

Ensure Java is installed on your computer.

Open a terminal or Command Prompt inside the folder.

Execute:

java -jar OperatingSystemsSimulator.jar

The Main Menu will appear, allowing access to the three scheduling simulators.

Note: The executable JAR must be built and included in the submitted package.



Option B: Run from Source Code

Open the Operating_Systems folder in Visual Studio Code or another Java-compatible IDE.

Ensure the Java Development Kit is properly installed and configured.

Locate src/Outputs/MainMenu.java.

Run the MainMenu class.

Select the desired scheduling algorithm from the application.

The application can also be compiled manually using the Java compiler.




4. Application Features

Shortest Job First (Non-Preemptive)

Accepts process arrival times and burst times.

Schedules the shortest available process without interruption.

Computes Completion Time, Turnaround Time, Waiting Time, and Response Time.

Displays average performance metrics, CPU Utilization, and Throughput.

Generates a time-scaled Gantt Chart.



Priority Preemptive Scheduling

Accepts Process ID, Arrival Time, Burst Time, and Priority.

Prioritizes processes with lower numerical priority values.

Supports preemption when a strictly higher-priority process arrives.

Calculates individual and average scheduling metrics.

Displays a Gantt Chart showing execution and preemption.



C-LOOK Disk Scheduling

Accepts Disk Size, Request Queue, Initial Head Position, and Direction.

Supports both Left and Right scheduling directions.

Calculates the Seek Sequence and individual Head Movements.

Computes Total Head Movement and Average Seek Distance.

Displays a graphical representation of disk head movement, including circular jumps.

All three simulators include sample data and input validation.







5. Project Structure

Operating_Systems/
├── src/
│   ├── Outputs/
│   │   ├── MainMenu.java
│   │   ├── SJF.java
│   │   ├── PriorityPreemptive.java
│   │   ├── CLOOK.java
│   │   ├── SJFAdapter.java
│   │   ├── PriorityAdapter.java
│   │   ├── CLOOKAdapter.java
│   │   └── InterfaceTest.java
│   └── shared/
│       ├── CPUScheduler.java
│       ├── CPUProcess.java
│       ├── CPUResult.java
│       ├── DiskScheduler.java
│       └── DiskResult.java
└── OperatingSystemsSimulator.jar

The Outputs package contains the scheduling algorithms, graphical interfaces, adapters, and Main Menu. The shared package contains the interfaces and result structures used for integration.



6. Group Members and Contributions


Group Member

Love Margaret G. Leonado
Jared Maqkenzy Leonardo
Andrei Benedict B. Manalad


Assigned Algorithm

Love Margaret G. Leonado - Shortest Job First
Jared Maqkenzy Leonardo - Priority Preemptive
Andrei Benedict B. Manalad - C-LOOK


Coordination Role
Love Margaret G. Leonado - Documentation Lead
Jared Maqkenzy Leonardo - Project Leader / Testing Lead
Andrei Benedict B. Manalad - Video Lead



Each member contributed to the development, testing, and documentation of their assigned scheduling module.



7. Testing and Validation

The application is designed to handle various scheduling scenarios, including:

Simultaneous process arrivals and scheduling ties

Idle CPU periods

Single-process scheduling

Preemptive and non-preemptive execution

Disk boundary conditions

Duplicate disk requests

Circular head movement

Invalid numerical inputs

The test cases, expected results, and actual outputs are documented in Appendix C of the project documentation.



8. GitHub Repository

Repository Link: https://github.com/JustForTSU-17/Operating-System-3ITB-Group-2-Brainzzz-



9. Troubleshooting

Application does not launch: Verify that Java is installed and that the executable JAR is present.

Invalid input message: Check that all required fields contain valid numerical values within the accepted ranges.

Incorrect scheduling results: Verify the supplied arrival times, burst times, priorities, disk requests, and selected direction.

Graph or Gantt Chart is not fully visible: Resize the simulator window or use the available scrollbars.



10. Project Purpose

This project was developed as part of the ICS26012 Operating Systems Alternative Assessment. It demonstrates the practical implementation of scheduling algorithms through Java programming, performance evaluation, and graphical visualization.