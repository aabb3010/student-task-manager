import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class StudentTaskManager {

    private static final Scanner scanner = new Scanner(System.in);

    private static final ArrayList<Task> tasks = new ArrayList<>();

    private static final Path FILE_PATH =
            Paths.get("tasks.txt");

    // Represents one student task
    static class Task {
        private String title;
        private String subject;
        private LocalDate dueDate;
        private String priority;
        private boolean completed;

        public Task(String title, String subject,
                    LocalDate dueDate, String priority) {
            this.title = title;
            this.subject = subject;
            this.dueDate = dueDate;
            this.priority = priority;
            this.completed = false;
        }

        public String getTitle() {
            return title;
        }

        public String getSubject() {
            return subject;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public String getPriority() {
            return priority;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void markCompleted() {
            completed = true;
        }

        public String getStatus() {
            return completed ? "Completed" : "Pending";
        }

        @Override
        public String toString() {
            return "Title: " + title
                    + "\nSubject: " + subject
                    + "\nDue date: " + dueDate
                    + "\nPriority: " + priority
                    + "\nStatus: " + getStatus();
        }
    }

    // Main program
    public static void main(String[] args) {
        loadTasks();

        System.out.println("================================");
        System.out.println("     STUDENT TASK MANAGER");
        System.out.println("================================");

        boolean running = true;

        while (running) {
            displayMenu();

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    addTask();
                    break;

                case 2:
                    viewTasks();
                    break;

                case 3:
                    completeTask();
                    break;

                case 4:
                    deleteTask();
                    break;

                case 5:
                    filterBySubject();
                    break;

                case 6:
                    filterByStatus();
                    break;

                case 7:
                    showStatistics();
                    break;

                case 8:
                    saveTasks();
                    System.out.println("Tasks saved. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice. Please choose 1-8."
                    );
            }
        }

        scanner.close();
    }

    // Display the main menu
    private static void displayMenu() {
        System.out.println("\n---------- MENU ----------");
        System.out.println("1. Add a task");
        System.out.println("2. View all tasks");
        System.out.println("3. Mark a task as completed");
        System.out.println("4. Delete a task");
        System.out.println("5. Filter tasks by subject");
        System.out.println("6. Filter tasks by status");
        System.out.println("7. View statistics");
        System.out.println("8. Save and exit");
        System.out.println("---------------------------");
    }

    // Add a new task
    private static void addTask() {
        System.out.println("\n--- ADD A TASK ---");

        String title = readNonEmpty("Task title: ");
        String subject = readNonEmpty("Subject: ");

        LocalDate dueDate = readDate(
                "Due date (YYYY-MM-DD): "
        );

        String priority = readPriority();

        Task task = new Task(
                title, subject, dueDate, priority
        );

        tasks.add(task);
        saveTasks();

        System.out.println("Task added successfully!");
    }

    // Display all tasks
    private static void viewTasks() {
        System.out.println("\n--- ALL TASKS ---");

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("\nTask #" + (i + 1));
            System.out.println(tasks.get(i));
            System.out.println("---------------------------");
        }
    }

    // Mark a task as completed
    private static void completeTask() {
        if (tasks.isEmpty()) {
            System.out.println("There are no tasks to complete.");
            return;
        }

        viewTasks();

        int number = readInt(
                "Enter the task number to complete: "
        );

        if (number < 1 || number > tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        Task task = tasks.get(number - 1);

        if (task.isCompleted()) {
            System.out.println("This task is already completed.");
        } else {
            task.markCompleted();
            saveTasks();
            System.out.println("Task marked as completed!");
        }
    }

    // Delete a task
    private static void deleteTask() {
        if (tasks.isEmpty()) {
            System.out.println("There are no tasks to delete.");
            return;
        }

        viewTasks();

        int number = readInt(
                "Enter the task number to delete: "
        );

        if (number < 1 || number > tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        Task removed = tasks.remove(number - 1);
        saveTasks();

        System.out.println(
                "Deleted task: " + removed.getTitle()
        );
    }

    // Filter tasks by subject
    private static void filterBySubject() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        String subject = readNonEmpty(
                "Enter the subject to search for: "
        );

        boolean found = false;

        System.out.println("\n--- SEARCH RESULTS ---");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            if (task.getSubject().equalsIgnoreCase(subject)) {
                System.out.println("\nTask #" + (i + 1));
                System.out.println(task);
                System.out.println("---------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println(
                    "No tasks found for this subject."
            );
        }
    }

    // Filter tasks by completion status
    private static void filterByStatus() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        System.out.println("1. Pending tasks");
        System.out.println("2. Completed tasks");

        int choice = readInt("Choose a status: ");

        if (choice != 1 && choice != 2) {
            System.out.println("Invalid choice.");
            return;
        }

        boolean wantedStatus = (choice == 2);
        boolean found = false;

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            if (task.isCompleted() == wantedStatus) {
                System.out.println("\nTask #" + (i + 1));
                System.out.println(task);
                System.out.println("---------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No tasks match this status.");
        }
    }

    // Display task statistics
    private static void showStatistics() {
        int completed = 0;
        int pending = 0;
        int overdue = 0;

        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.isCompleted()) {
                completed++;
            } else {
                pending++;

                if (task.getDueDate().isBefore(today)) {
                    overdue++;
                }
            }
        }

        System.out.println("\n--- TASK STATISTICS ---");
        System.out.println("Total tasks: " + tasks.size());
        System.out.println("Completed: " + completed);
        System.out.println("Pending: " + pending);
        System.out.println("Overdue: " + overdue);

        if (!tasks.isEmpty()) {
            double percentage =
                    (completed * 100.0) / tasks.size();

            System.out.printf(
                    "Completion rate: %.1f%%%n",
                    percentage
            );
        }
    }

    // Read a valid integer
    private static int readInt(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Please enter a valid whole number."
                );
            }
        }
    }

    // Read non-empty text
    private static String readNonEmpty(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    // Read and validate a date
    private static LocalDate readDate(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Invalid date. Use YYYY-MM-DD, "
                        + "for example 2026-10-20."
                );
            }
        }
    }

    // Read a valid priority
    private static String readPriority() {
        while (true) {
            System.out.println("Priority:");
            System.out.println("1. Low");
            System.out.println("2. Medium");
            System.out.println("3. High");

            int choice = readInt("Choose priority: ");

            switch (choice) {
                case 1:
                    return "Low";
                case 2:
                    return "Medium";
                case 3:
                    return "High";
                default:
                    System.out.println(
                            "Please choose 1, 2, or 3."
                    );
            }
        }
    }

    // Save all tasks to a text file
    private static void saveTasks() {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FILE_PATH, StandardCharsets.UTF_8)) {

            for (Task task : tasks) {
                String line =
                        encode(task.getTitle()) + "\t"
                        + encode(task.getSubject()) + "\t"
                        + task.getDueDate() + "\t"
                        + task.getPriority() + "\t"
                        + task.isCompleted();

                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println(
                    "Error saving tasks: " + e.getMessage()
            );
        }
    }

    // Load saved tasks when the program starts
    private static void loadTasks() {
        if (!Files.exists(FILE_PATH)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FILE_PATH, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t", -1);

                if (parts.length != 5) {
                    System.out.println(
                            "Skipping an invalid saved task."
                    );
                    continue;
                }

                try {
                    String title = decode(parts[0]);
                    String subject = decode(parts[1]);
                    LocalDate dueDate =
                            LocalDate.parse(parts[2]);
                    String priority = parts[3];

                    if (!priority.equals("Low")
                            && !priority.equals("Medium")
                            && !priority.equals("High")) {
                        continue;
                    }

                    if (!parts[4].equals("true")
                            && !parts[4].equals("false")) {
                        continue;
                    }

                    Task task = new Task(
                            title, subject, dueDate, priority
                    );

                    if (Boolean.parseBoolean(parts[4])) {
                        task.markCompleted();
                    }

                    tasks.add(task);

                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Skipping an invalid saved task."
                    );
                }
            }

            System.out.println(
                    tasks.size() + " saved task(s) loaded."
            );

        } catch (IOException e) {
            System.out.println(
                    "Error loading tasks: " + e.getMessage()
            );
        }
    }

    // Encode text safely, even if it contains tabs
    private static String encode(String text) {
        return Base64.getEncoder().encodeToString(
                text.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String decode(String text) {
        return new String(
                Base64.getDecoder().decode(text),
                StandardCharsets.UTF_8
        );
    }
}