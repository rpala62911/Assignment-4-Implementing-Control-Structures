# The employee scheduling system was developed to collect employee preferences and generate a weekly shift schedule. 
# The scheduling process considered employee availability, shift requirements, and working-day limits while checking for scheduling conflicts.

import random

# Defining the days and available shifts
days = ["Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"]

shifts = ["Morning", "Afternoon", "Evening"]

MIN_EMPLOYEES = 2
MAX_WORK_DAYS = 5


# Collecting employee details and shift preferences
employees = []
preferences = {}

while True:
    try:
        total = int(input("Enter number of employees (minimum 9): "))
        if total >= 9:
            break
        print("At least 9 employees are required.")
    except ValueError:
        print("Please enter a valid number.")

# Recording names and preferred shifts for each employee
for i in range(total):
    name = input(f"\nEnter employee {i + 1} name: ").strip()

    while not name or name in employees:
        name = input("Enter a unique, nonempty name: ").strip()

    employees.append(name)
    preferences[name] = {}

    print("Enter M for Morning, A for Afternoon, E for Evening, O for Off.")

    # Gathering daily shift preferences
    for day in days:
        while True:
            choice = input(f"{day}: ").strip().upper()

            if choice == "M":
                preferences[name][day] = "Morning"
                break
            elif choice == "A":
                preferences[name][day] = "Afternoon"
                break
            elif choice == "E":
                preferences[name][day] = "Evening"
                break
            elif choice == "O":
                preferences[name][day] = "Off"
                break
            else:
                print("Invalid choice. Enter M, A, E, or O.")


# Preparing an empty schedule for the entire week
schedule = {
    day: {shift: [] for shift in shifts}
    for day in days
}

# Tracking the working days of each employee
work_days = {name: 0 for name in employees}


# Assigning preferred shifts while following work limits
for day in days:

    assigned_today = set()

    for name in employees:
        preferred = preferences[name][day]

        if (preferred in shifts
                and work_days[name] < MAX_WORK_DAYS
                and name not in assigned_today):

            schedule[day][preferred].append(name)
            assigned_today.add(name)
            work_days[name] += 1

    # Filling shifts that have fewer than two employees
    for shift in shifts:

        while len(schedule[day][shift]) < MIN_EMPLOYEES:

            # Finding employees available for additional assignments
            eligible = [
                name for name in employees
                if name not in assigned_today
                and work_days[name] < MAX_WORK_DAYS
            ]

            if not eligible:
                break

            # Prioritizing employees who prefer the required shift
            matching = [
                name for name in eligible
                if preferences[name][day] == shift
            ]

            candidates = matching if matching else eligible

            # Selecting an eligible employee at random
            selected = random.choice(candidates)

            schedule[day][shift].append(selected)
            assigned_today.add(selected)
            work_days[selected] += 1

    # Assigning remaining employees to alternative shifts
    for name in employees:

        if name in assigned_today:
            continue

        if work_days[name] >= MAX_WORK_DAYS:
            continue

        preferred = preferences[name][day]

        if preferred == "Off":
            continue

        # Finding shifts with available capacity
        available_shifts = [
            shift for shift in shifts
            if len(schedule[day][shift]) < total
        ]

        if available_shifts:

            # Choosing the shift with the fewest assigned employees
            alternative = min(
                available_shifts,
                key=lambda shift: len(schedule[day][shift])
            )

            schedule[day][alternative].append(name)
            assigned_today.add(name)
            work_days[name] += 1


# Displaying the completed weekly shift schedule
print("Final Employee Shift Schedule")

for day in days:

    print(f"\n{day}")

    for shift in shifts:

        assigned = schedule[day][shift]

        # Preparing the list of assigned employees
        if assigned:
            names = ", ".join(assigned)
        else:
            names = "No employees assigned"

        # Checking whether the minimum staffing requirement was met
        if len(assigned) >= MIN_EMPLOYEES:
            status = "Staffing OK"
        else:
            status = "STAFFING SHORTAGE"

        print(f"{shift:<12}: {names}")
        print(f"{'':12}  Employees: {len(assigned)} | {status}")


# Displaying the total working days for each employee
print("\nEmployee Workday Summary")

for name in employees:
    print(f"{name:<25} {work_days[name]} working days")


# Validating scheduling rules and identifying conflicts
print("\nScheduling Validation")

valid = True

for day in days:

    daily_employees = []

    # Gathering all employees assigned on the same day
    for shift in shifts:
        daily_employees.extend(schedule[day][shift])

    # Detecting employees assigned to multiple shifts
    if len(daily_employees) != len(set(daily_employees)):
        print(f"Conflict detected on {day}: employee assigned multiple shifts.")
        valid = False

# Checking the maximum weekly working-day limit
for name in employees:
    if work_days[name] > MAX_WORK_DAYS:
        print(f"Weekly limit exceeded for {name}.")
        valid = False

# Displaying the final validation results
if valid:
    print("No employee has multiple shifts on the same day.")
    print("No employee exceeds five working days.")
    print("Scheduling restrictions are satisfied.")

print("\nSchedule generation completed.")