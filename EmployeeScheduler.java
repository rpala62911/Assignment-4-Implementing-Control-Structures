// The employee scheduling system has been designed to gather employee information and preferences for the entire week. 
// The scheduling process considered employee availability, staffing requirements, and weekly working-day limits while checking for conflicts and displaying the final schedule.

import java.util.*;

public class EmployeeScheduler {

    // Defining the days and available shifts
    static String[] days = {
            "Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"
    };

    static String[] shifts = {
            "Morning", "Afternoon", "Evening"
    };

    static final int MIN_EMPLOYEES = 2;
    static final int MAX_WORK_DAYS = 5;

    static Random random = new Random();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Collecting the total number of employees
        int total;

        while (true) {
            System.out.print("Enter number of employees (minimum 9): ");

            try {
                total = Integer.parseInt(scanner.nextLine());

                if (total >= 9) {
                    break;
                }

                System.out.println("At least 9 employees are required.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Storing employee names and daily preferences
        List<String> employees = new ArrayList<>();

        Map<String, Map<String, String>> preferences = new LinkedHashMap<>();

        for (int i = 0; i < total; i++) {

            System.out.print("\nEnter employee " + (i + 1) + " name: ");
            String name = scanner.nextLine().trim();

            // Checking for empty or duplicate employee names
            while (name.isEmpty() || employees.contains(name)) {
                System.out.print("Enter a unique, nonempty name: ");
                name = scanner.nextLine().trim();
            }

            employees.add(name);

            Map<String, String> dailyPreferences = new LinkedHashMap<>();

            System.out.println(
                    "Enter M for Morning, A for Afternoon, E for Evening, O for Off.");

            // Collecting shift preferences for each day
            for (String day : days) {

                while (true) {

                    System.out.print(day + ": ");
                    String choice = scanner.nextLine().trim().toUpperCase();

                    if (choice.equals("M")) {
                        dailyPreferences.put(day, "Morning");
                        break;

                    } else if (choice.equals("A")) {
                        dailyPreferences.put(day, "Afternoon");
                        break;

                    } else if (choice.equals("E")) {
                        dailyPreferences.put(day, "Evening");
                        break;

                    } else if (choice.equals("O")) {
                        dailyPreferences.put(day, "Off");
                        break;

                    } else {
                        System.out.println(
                                "Invalid choice. Enter M, A, E, or O.");
                    }
                }
            }

            preferences.put(name, dailyPreferences);
        }

        // Preparing the weekly schedule and workday records
        Map<String, Map<String, List<String>>> schedule = new LinkedHashMap<>();

        Map<String, Integer> workDays = new LinkedHashMap<>();

        for (String name : employees) {
            workDays.put(name, 0);
        }

        // Processing employee assignments for each day
        for (String day : days) {

            Map<String, List<String>> dailySchedule = new LinkedHashMap<>();

            // Creating an empty list for every shift
            for (String shift : shifts) {
                dailySchedule.put(shift, new ArrayList<>());
            }

            schedule.put(day, dailySchedule);

            Set<String> assignedToday = new HashSet<>();

            // Assigning employees to preferred shifts
            for (String name : employees) {

                String preferred = preferences.get(name).get(day);

                if (Arrays.asList(shifts).contains(preferred)
                        && workDays.get(name) < MAX_WORK_DAYS
                        && !assignedToday.contains(name)) {

                    dailySchedule.get(preferred).add(name);

                    assignedToday.add(name);

                    workDays.put(
                            name,
                            workDays.get(name) + 1);
                }
            }

            // Filling shifts with fewer than two employees
            for (String shift : shifts) {

                List<String> assigned = dailySchedule.get(shift);

                while (assigned.size() < MIN_EMPLOYEES) {

                    List<String> eligible = new ArrayList<>();

                    // Finding employees available for additional assignments
                    for (String name : employees) {

                        if (!assignedToday.contains(name)
                                && workDays.get(name) < MAX_WORK_DAYS) {

                            eligible.add(name);
                        }
                    }

                    if (eligible.isEmpty()) {
                        break;
                    }

                    // Prioritizing employees with matching preferences
                    List<String> matching = new ArrayList<>();

                    for (String name : eligible) {

                        if (preferences.get(name).get(day)
                                .equals(shift)) {

                            matching.add(name);
                        }
                    }

                    List<String> candidates = matching.isEmpty() ? eligible : matching;

                    // Selecting an eligible employee at random
                    String selected = candidates.get(
                            random.nextInt(candidates.size()));

                    assigned.add(selected);
                    assignedToday.add(selected);

                    workDays.put(
                            selected,
                            workDays.get(selected) + 1);
                }
            }

            // Assigning remaining employees to alternative shifts
            for (String name : employees) {

                if (assignedToday.contains(name)
                        || workDays.get(name) >= MAX_WORK_DAYS) {
                    continue;
                }

                String preferred = preferences.get(name).get(day);

                if (preferred.equals("Off")) {
                    continue;
                }

                String alternative = null;
                int minimum = Integer.MAX_VALUE;

                // Finding the shift with the fewest employees
                for (String shift : shifts) {

                    int currentSize = dailySchedule.get(shift).size();

                    if (currentSize < total
                            && currentSize < minimum) {

                        minimum = currentSize;
                        alternative = shift;
                    }
                }

                // Assigning the employee to the selected shift
                if (alternative != null) {

                    dailySchedule.get(alternative).add(name);

                    assignedToday.add(name);

                    workDays.put(
                            name,
                            workDays.get(name) + 1);
                }
            }
        }

        // Displaying the completed weekly schedule
        System.out.println("Final Employee Shift Schedule");

        for (String day : days) {

            System.out.println("\n" + day);

            for (String shift : shifts) {

                List<String> assigned = schedule.get(day).get(shift);

                // Checking the staffing level for each shift
                String status = assigned.size() >= MIN_EMPLOYEES
                        ? "Staffing OK"
                        : "STAFFING SHORTAGE";

                System.out.printf(
                        "%-12s: %s%n",
                        shift,
                        assigned.isEmpty()
                                ? "No employees assigned"
                                : String.join(", ", assigned));

                System.out.println(
                        "            Employees: "
                                + assigned.size() + " | " + status);
            }
        }

        // Displaying the total working days for each employee
        System.out.println("\nEmployee Workday Summary");

        for (String name : employees) {

            System.out.printf(
                    "%-25s %d working days%n",
                    name,
                    workDays.get(name));
        }

        // Checking scheduling restrictions and conflicts
        System.out.println("\nScheduling Validation");

        boolean valid = true;

        for (String day : days) {

            Set<String> dailyEmployees = new HashSet<>();

            // Detecting employees assigned to multiple shifts
            for (String shift : shifts) {

                for (String name : schedule.get(day).get(shift)) {

                    if (!dailyEmployees.add(name)) {

                        System.out.println(
                                "Conflict detected on " + day
                                        + ": " + name
                                        + " assigned multiple shifts.");

                        valid = false;
                    }
                }
            }
        }

        // Checking the maximum weekly working-day limit
        for (String name : employees) {

            if (workDays.get(name) > MAX_WORK_DAYS) {

                System.out.println(
                        "Weekly limit exceeded for " + name);

                valid = false;
            }
        }

        // Displaying the final validation results
        if (valid) {
            System.out.println(
                    "No employee has multiple shifts on the same day.");

            System.out.println(
                    "No employee exceeds five working days.");

            System.out.println(
                    "Scheduling restrictions are satisfied.");
        }

        System.out.println("\nSchedule generation completed.");

        // Closing the input resource
        scanner.close();
    }
}