import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// This program must be able to manipulate data in the Poise database.
public class Poise {
	/*
	 * These variables are declared here so that they can be used across all
	 * methods.
	 */
	private static Scanner input = new Scanner(System.in);
	private static String sqlStatement;

	public static void main(String[] args) {
		try {
			Connection poiseDatabase = DriverManager.getConnection("jdbc:mysql://localhost:3306/poisepms", "otheruser",
					"swordfish");
			Statement statement = poiseDatabase.createStatement();

			System.out.println("Welcome to the Poise database.");
			/*
			 * This boolean will allow the program to constantly prompt the user for an
			 * operation. There are 7 operations that this program must be able to perform
			 * so there will be 7 if statements and 1 to break the loop.
			 */
			boolean exitProgram = false;
			int selectedOperation = -1;
			while (!exitProgram) {
				System.out.println(
						"Select an operation:\n1. View all projects\n2. Add project\n3. Update project\n4. Delete project and associated people\n5. Finalise project\n6. View incomplete projects\n7. View projects past due\n0. Exit\n");
				selectedOperation = Integer.parseInt(input.nextLine());

				if (selectedOperation == 1) {
					viewAllProjects(statement);
				} else if (selectedOperation == 2) {
					addProject(statement);
				} else if (selectedOperation == 3) {
					updateProject(statement);
				} else if (selectedOperation == 4) {
					deleteProject(statement);
				} else if (selectedOperation == 5) {
					finaliseProject(statement);
				} else if (selectedOperation == 6) {
					viewIncompleteProjects(statement);
				} else if (selectedOperation == 7) {
					viewPastDueProjects(statement);
				} else if (selectedOperation == 0) {
					exitProgram = true;
				} else {
					/*
					 * If the input is anything other than 0-7 then nothing will happen but the loop
					 * will continue.
					 */
				}
			}
			// A goodbye message is printed on exit.
			System.out.println("Goodbye.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void viewAllProjects(Statement statement) throws SQLException {
		/*
		 * This method will display information of every project in the database
		 * including the people associated with them.
		 */
		List<Integer> projectNumbers = new ArrayList<>();
		ResultSet results = statement.executeQuery("select * from projects");

		while (results.next()) {
			// Every project number that's found will be added to a list.
			projectNumbers.add(results.getInt("project_number"));
		}

		for (int currentProjectNumber : projectNumbers) {
			sqlStatement = String.format("select * from projects where project_number = %s", currentProjectNumber);
			ResultSet projectsTable = statement.executeQuery(sqlStatement);
			projectsTable.next();
			System.out.println("\nproject_number: " + projectsTable.getInt("project_number") + "\nproject_name: "
					+ projectsTable.getString("project_name") + "\nproject_deadline: "
					+ projectsTable.getDate("project_deadline") + "\nbuilding_type: "
					+ projectsTable.getString("building_type") + "\naddress: " + projectsTable.getString("address")
					+ "\nerf_number: " + projectsTable.getInt("erf_number") + "\ntotal_fee: "
					+ projectsTable.getDouble("total_fee") + "\namount_paid: " + projectsTable.getDouble("amount_paid")
					+ "\nstatus: " + projectsTable.getString("status") + "\ncompletion_date: "
					+ projectsTable.getDate("completion_date"));

			// The IDs of these people are stored before the result set is closed.
			int structural_engineer_id = projectsTable.getInt("structural_engineer_id");
			int project_manager_id = projectsTable.getInt("project_manager_id");
			int architect_id = projectsTable.getInt("architect_id");
			int customer_id = projectsTable.getInt("customer_id");

			sqlStatement = String.format("select * from structural_engineers where id = %s", structural_engineer_id);
			ResultSet structuralEngineersTable = statement.executeQuery(sqlStatement);
			structuralEngineersTable.next();
			System.out.println("Structural Engineer: " + structuralEngineersTable.getString("name"));

			sqlStatement = String.format("select * from project_managers where id = %s", project_manager_id);
			ResultSet projectManagersTable = statement.executeQuery(sqlStatement);
			projectManagersTable.next();
			System.out.println("Project Manager: " + projectManagersTable.getString("name"));

			sqlStatement = String.format("select * from architects where id = %s", architect_id);
			ResultSet architectsTable = statement.executeQuery("select * from architects");
			architectsTable.next();
			System.out.println("Architect: " + architectsTable.getString("name"));

			sqlStatement = String.format("select * from customers where id = %s", customer_id);
			ResultSet customersTable = statement.executeQuery("select * from customers");
			customersTable.next();
			System.out.println("Customer: " + customersTable.getString("name") + "\n");
		}
	}

	private static void addProject(Statement statement) throws SQLException {
		// This method will let the user add a project to the database.

		/*
		 * The user can choose between adding a new structural engineer or selecting an
		 * existing one for their new project.
		 */
		int selectOrAdd;
		System.out.println(
				"For your new project would you like to:\n1. select an existing structural engineer\n2. add a new one");
		selectOrAdd = Integer.parseInt(input.nextLine());

		int structuralEngineer_id;
		if (selectOrAdd == 1) {
			System.out.println("Enter the ID of an existing structural engineer:");
			structuralEngineer_id = Integer.parseInt(input.nextLine());
		} else {
			/*
			 * This else block will prompt the user to enter info about the new structural
			 * engineer and they will be added to the structural engineer table.
			 */
			System.out.println("Enter the ID of the new structural engineer:");
			structuralEngineer_id = Integer.parseInt(input.nextLine());
			System.out.println("Enter the name of the structural engineer:");
			String strucutralEngineer_name = input.nextLine();
			System.out.println("Enter the telephone of the structural engineer:");
			int structuralEngineer_telephone = Integer.parseInt(input.nextLine());
			System.out.println("Enter the email address of the structural engineer:");
			String structuralEngineer_email = input.nextLine();
			System.out.println("Enter the address of the structural engineer:");
			String structuralEngineer_address = input.nextLine();
			sqlStatement = String.format("insert into structural_engineers values (%s, '%s', %s, '%s', '%s')",
					structuralEngineer_id, strucutralEngineer_name, structuralEngineer_telephone,
					structuralEngineer_email, structuralEngineer_address);
			statement.executeUpdate(sqlStatement);
		}

		// This is the project manager section.
		System.out.println(
				"For your new project would you like to:\n1. select an existing project manager\n2. add a new one");
		selectOrAdd = Integer.parseInt(input.nextLine());

		int projectManager_id;
		if (selectOrAdd == 1) {
			System.out.println("Enter the ID of an existing project manager:");
			projectManager_id = Integer.parseInt(input.nextLine());
		} else {
			System.out.println("Enter the ID of the new project manager:");
			projectManager_id = Integer.parseInt(input.nextLine());
			System.out.println("Enter the name of the project manager:");
			String projectManager_name = input.nextLine();
			System.out.println("Enter the telephone of the project manager:");
			int projectManager_telephone = Integer.parseInt(input.nextLine());
			System.out.println("Enter the email address of the project manager:");
			String projectManager_email = input.nextLine();
			System.out.println("Enter the address of the project manager:");
			String projectManager_address = input.nextLine();
			sqlStatement = String.format("insert into project_managers values (%s, '%s', %s, '%s', '%s')",
					projectManager_id, projectManager_name, projectManager_telephone, projectManager_email,
					projectManager_address);
			statement.executeUpdate(sqlStatement);
		}

		// This is the architect section.
		System.out
				.println("For your new project would you like to:\n1. select an existing architect\n2. add a new one");
		selectOrAdd = Integer.parseInt(input.nextLine());

		int architect_id;
		if (selectOrAdd == 1) {
			System.out.println("Enter the ID of an existing architect:");
			architect_id = Integer.parseInt(input.nextLine());
		} else {
			System.out.println("Enter the ID of the new architect:");
			architect_id = Integer.parseInt(input.nextLine());
			System.out.println("Enter the name of the architect:");
			String architect_name = input.nextLine();
			System.out.println("Enter the telephone of the architect:");
			int architect_telephone = Integer.parseInt(input.nextLine());
			System.out.println("Enter the email address of the architect:");
			String architect_email = input.nextLine();
			System.out.println("Enter the address of the architect:");
			String architect_address = input.nextLine();
			sqlStatement = String.format("insert into architects values (%s, '%s', %s, '%s', '%s')", architect_id,
					architect_name, architect_telephone, architect_email, architect_address);
			statement.executeUpdate(sqlStatement);
		}

		// This is the customer section.
		System.out.println("For your new project would you like to:\n1. select an existing customer\n2. add a new one");
		selectOrAdd = Integer.parseInt(input.nextLine());

		int customer_id;
		String customer_name;
		if (selectOrAdd == 1) {
			System.out.println("Enter the ID of an customer:");
			customer_id = Integer.parseInt(input.nextLine());
			sqlStatement = String.format("select * from customers where id = %s", customer_id);
			ResultSet results = statement.executeQuery(sqlStatement);
			results.next();
			customer_name = results.getString("name");
		} else {
			System.out.println("Enter the ID of the new customer:");
			customer_id = Integer.parseInt(input.nextLine());
			System.out.println("Enter the name of the customer:");
			customer_name = input.nextLine();
			System.out.println("Enter the telephone of the customer:");
			int customer_telephone = Integer.parseInt(input.nextLine());
			System.out.println("Enter the email address of the customer:");
			String customer_email = input.nextLine();
			System.out.println("Enter the address of the customer:");
			String customer_address = input.nextLine();

			sqlStatement = String.format("insert into customers values (%s, '%s', %s, '%s', '%s')", customer_id,
					customer_name, customer_telephone, customer_email, customer_address);
			statement.executeUpdate(sqlStatement);
		}

		List<Integer> projectNumbers = new ArrayList<>();
		ResultSet projectsTable = statement.executeQuery("select * from projects");
		while (projectsTable.next()) {
			// Every project number that's found will be added to a list.
			projectNumbers.add(projectsTable.getInt("project_number"));
		}

		System.out.println("Enter a project number:");
		int project_number = Integer.parseInt(input.nextLine());
		/*
		 * These if and while blocks will prevent the user from entering an existing
		 * project number. Once the user enters a number that is not in the
		 * projectNumbers list, they can proceed to enter the rest of the details.
		 */
		if (projectNumbers.contains(project_number)) {
			while (projectNumbers.contains(project_number)) {
				System.out.println("A project with that number exists. Enter another project number:");
				project_number = Integer.parseInt(input.nextLine());
			}
		}

		// This block will prompt the user to enter info about the project.
		System.out.println("Enter a project name:");
		String project_name = input.nextLine();
		System.out.println("Enter the deadline for this project (yyyy-mm-dd):");
		String date = input.nextLine();
		LocalDate project_deadline = LocalDate.parse(date);
		System.out.println("Enter the building type:");
		String building_type = input.nextLine();
		System.out.println("Enter the address:");
		String address = input.nextLine();
		System.out.println("Enter the erf number:");
		int erf_number = Integer.parseInt(input.nextLine());
		System.out.println("Enter the total fee:");
		Double total_fee = Double.parseDouble(input.nextLine());
		System.out.println("Enter the amount paid thus far:");
		Double amount_paid = Double.parseDouble(input.nextLine());
		System.out.println("Enter the status of the project (complete or incomplete):");
		String status = input.nextLine();

		Object completion_date;
		if (status.equalsIgnoreCase("complete")) {
			/*
			 * If the status of the project was entered as "complete" then the user will be
			 * prompted to enter an completion date.
			 */
			System.out.println("Enter the completion date if the project is already done:");
			date = input.nextLine();

			completion_date = LocalDate.parse(date);

			sqlStatement = String.format(
					"insert into projects values (%s, '%s', '%s', '%s', '%s', %s, %s, %s, '%s', '%s', %s, %s, %s, %s)",
					project_number, project_name, project_deadline, building_type, address, erf_number, total_fee,
					amount_paid, status, completion_date, structuralEngineer_id, projectManager_id, architect_id,
					customer_id);
			statement.executeUpdate(sqlStatement);
		} else {
			// If "complete" was not entered for the status then the date will be set to
			// null.
			completion_date = null;

			/*
			 * In this SQL statement the completion date is not encapsulated by quotation
			 * marks so that null can be inserted as its value.
			 */
			sqlStatement = String.format(
					"insert into projects values (%s, '%s', '%s', '%s', '%s', %s, %s, %s, '%s', %s, %s, %s, %s, %s)",
					project_number, project_name, project_deadline, building_type, address, erf_number, total_fee,
					amount_paid, status, completion_date, structuralEngineer_id, projectManager_id, architect_id,
					customer_id);
			statement.executeUpdate(sqlStatement);
		}

		/*
		 * If the user did not provide a project name, the customer's name and project
		 * building type must be concatenated for the project's name.
		 */
		if (project_name.isBlank() || project_name.isEmpty()) {
			sqlStatement = String.format("update projects set project_name = '%s %s' where project_number = %s",
					customer_name, building_type, project_number);
			statement.executeUpdate(sqlStatement);
		}
		System.out.println("Project added.");
	}

	private static void updateProject(Statement statement) throws SQLException {
		// This method will let the user update a project in the database.
		List<Integer> projectNumbers = new ArrayList<>();
		List<String> projectNames = new ArrayList<>();
		ResultSet projectsTable = statement.executeQuery("select * from projects");

		while (projectsTable.next()) {
			// Every project number and name found will be added to a list.
			projectNumbers.add(projectsTable.getInt("project_number"));
			projectNames.add(projectsTable.getString("project_name"));
		}

		// projectToUpdate will store the project_number to be updated.
		int projectToUpdate = 0;
		boolean valueFound = false;
		while (!valueFound) {
			/*
			 * This while block will prompt the user to enter a project number or name that
			 * exists. If the name or number can be found in the respective lists the while
			 * loop will be broken.
			 */
			System.out.println("Enter the name or number of the project you would like to modify:");
			String searchValue = input.nextLine();

			if (projectNumbers.contains(Integer.parseInt(searchValue))) {
				/*
				 * The user's input (searchValue) is parsed to an integer to check if it is
				 * present in the projectNumbers list. If so, projectToUpdate is set to the
				 * integer value of searchValue which would be the project_number that is going
				 * to be used to execute an update statement.
				 */
				projectToUpdate = Integer.parseInt(searchValue);
				// valueFound is set to true so the loop can terminate.
				valueFound = true;
			} else if (projectNames.contains(searchValue)) {
				/*
				 * If the user's input is found in the projectNames list then the index of the
				 * name in the list is stored in a variable. This index variable is then used to
				 * find the project_number of that project_name by using .get(index) on the
				 * projectNumbers list.
				 */
				int index = projectNames.indexOf(searchValue);
				projectToUpdate = projectNumbers.get(index);
				valueFound = true;
			} else {
				System.out.println("Project not found.");
			}
		}

		// This will display the selected project row.
		sqlStatement = String.format("select * from projects where project_number = %s", projectToUpdate);
		projectsTable = statement.executeQuery(sqlStatement);
		projectsTable.next();
		System.out.println("\nproject_number: " + projectsTable.getInt("project_number") + "\nproject_name: "
				+ projectsTable.getString("project_name") + "\nproject_deadline: "
				+ projectsTable.getDate("project_deadline") + "\nbuilding_type: "
				+ projectsTable.getString("building_type") + "\naddress: " + projectsTable.getString("address")
				+ "\nerf_number: " + projectsTable.getInt("erf_number") + "\ntotal_fee: "
				+ projectsTable.getDouble("total_fee") + "\namount_paid: " + projectsTable.getDouble("amount_paid")
				+ "\nstatus: " + projectsTable.getString("status") + "\ncompletion_date: "
				+ projectsTable.getDate("completion_date"));

		// The user will select the column value to modify.
		System.out.println(
				"Which field would you like to update?\n1. project_name\n2. project_deadline\n3. building_type\n4. address\n5. erf_number\n6. total_fee\n7. amount_paid\n8. status\n9. completion_date\n0. Cancel");
		int fieldToUpdate = Integer.parseInt(input.nextLine());

		if (fieldToUpdate == 1) {
			// Modifying the project name.
			System.out.println("Enter a new project name:");
			String newProjectName = input.nextLine();

			sqlStatement = String.format("update projects set project_name = '%s' where project_number = %s",
					newProjectName, projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 2) {
			// Modifying the project deadline.
			System.out.println("Enter a new project deadline:");
			String date = input.nextLine();
			LocalDate newCompletionDate = LocalDate.parse(date);

			sqlStatement = String.format("update projects set project_deadline = '%s' where project_number = %s",
					newCompletionDate, projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 3) {
			// Modifying the building type.
			System.out.println("Enter a new building type:");
			String newBuildingType = input.nextLine();

			sqlStatement = String.format("update projects set building_type = '%s' where project_number = %s",
					newBuildingType, projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 4) {
			// Modifying the address of the project.
			System.out.println("Enter a new address:");
			String newAddress = input.nextLine();

			sqlStatement = String.format("update projects set address = '%s' where project_number = %s", newAddress,
					projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 5) {
			// Modifying the erf number.
			System.out.println("Enter a new erf number:");
			int newErfNumber = Integer.parseInt(input.nextLine());

			sqlStatement = String.format("update projects set erf_number = %s where project_number = %s", newErfNumber,
					projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 6) {
			// Modifying the total fee of the project.
			System.out.println("Enter a new total fee amount:");
			Double newTotalFee = Double.parseDouble(input.nextLine());

			sqlStatement = String.format("update projects set total_fee = %s where project_number = %s", newTotalFee,
					projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 7) {
			// Modifying the amount that has been paid.
			System.out.println("Enter the new amount that's been paid:");
			Double newAmountPaid = Double.parseDouble(input.nextLine());

			sqlStatement = String.format("update projects set amount_paid = %s where project_number = %s",
					newAmountPaid, projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 8) {
			// Modifying the status of the project.
			System.out.println("Enter the new status of the project:");
			String newStatus = input.nextLine();

			sqlStatement = String.format("update projects set status = '%s' where project_number = %s", newStatus,
					projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 9) {
			// Modifying the completion date of the project.
			System.out.println("Enter the new completion date of the project(yyyy-mm-dd):");
			String newCompletionDate = input.nextLine();
			LocalDate date = LocalDate.parse(newCompletionDate);

			sqlStatement = String.format("update projects set completion_date = '%s' where project_number = %s", date,
					projectToUpdate);
			statement.executeUpdate(sqlStatement);
		} else if (fieldToUpdate == 0) {
			System.out.println("Cancelled.");
		}
	}

	private static void deleteProject(Statement statement) throws SQLException {
		// This method will let the user delete a project and the people involved.
		List<Integer> projectNumbers = new ArrayList<>();
		List<String> projectNames = new ArrayList<>();
		ResultSet projectsTable = statement.executeQuery("select * from projects");

		while (projectsTable.next()) {
			projectNumbers.add(projectsTable.getInt("project_number"));
			projectNames.add(projectsTable.getString("project_name"));
		}

		int projectToDelete = 0;
		boolean valueFound = false;
		while (!valueFound) {
			System.out.println("Enter the name or number of the project you would like to delete:");
			String searchValue = input.nextLine();

			if (projectNumbers.contains(Integer.parseInt(searchValue))) {
				projectToDelete = Integer.parseInt(searchValue);

				valueFound = true;
			} else if (projectNames.contains(searchValue)) {
				int index = projectNames.indexOf(searchValue);
				projectToDelete = projectNumbers.get(index);
				valueFound = true;
			} else {
				System.out.println("Project not found.");
			}
		}

		// These variables allow the program to find the people to delete.
		sqlStatement = String.format("select * from projects where project_number = %s", projectToDelete);
		ResultSet projectToDeleteTable = statement.executeQuery(sqlStatement);
		projectToDeleteTable.next();
		int structural_engineer_id = projectToDeleteTable.getInt("structural_engineer_id");
		int project_manager_id = projectToDeleteTable.getInt("project_manager_id");
		int architect_id = projectToDeleteTable.getInt("architect_id");
		int customer_id = projectToDeleteTable.getInt("customer_id");

		sqlStatement = String.format("delete from projects where project_number = %s", projectToDelete);
		statement.executeUpdate(sqlStatement);
		sqlStatement = String.format("delete from structural_engineers where id = %s", structural_engineer_id);
		statement.executeUpdate(sqlStatement);
		sqlStatement = String.format("delete from project_managers where id = %s", project_manager_id);
		statement.executeUpdate(sqlStatement);
		sqlStatement = String.format("delete from architects where id = %s", architect_id);
		statement.executeUpdate(sqlStatement);
		sqlStatement = String.format("delete from customers where id = %s", customer_id);
		statement.executeUpdate(sqlStatement);

	}

	private static void finaliseProject(Statement statement) throws SQLException {
		/*
		 * This method will let the user mark a project as complete (the project's
		 * status will be updated to complete and the date of completion will be
		 * inserted).
		 */
		List<Integer> projectNumbers = new ArrayList<>();
		List<String> projectNames = new ArrayList<>();
		ResultSet projectsTable = statement.executeQuery("select * from projects");

		while (projectsTable.next()) {
			// Every project number and name found will be added to a list.
			projectNumbers.add(projectsTable.getInt("project_number"));
			projectNames.add(projectsTable.getString("project_name"));
		}

		int projectToFinalise = 0;
		boolean valueFound = false;
		while (!valueFound) {
			System.out.println("Enter the name or number of the project you would like to finalise:");
			String searchValue = input.nextLine();

			if (projectNumbers.contains(Integer.parseInt(searchValue))) {
				projectToFinalise = Integer.parseInt(searchValue);
				// valueFound is set to true so the loop can terminate.
				valueFound = true;
			} else if (projectNames.contains(searchValue)) {

				int index = projectNames.indexOf(searchValue);
				projectToFinalise = projectNumbers.get(index);
				valueFound = true;
			} else {
				System.out.println("Project not found.");
			}
		}

		sqlStatement = String.format("update projects set status = 'complete' where project_number = %s",
				projectToFinalise);
		statement.executeUpdate(sqlStatement);
		// This variable will retrieve the current date to use as the completion date.
		LocalDate currentDate = LocalDate.now();
		sqlStatement = String.format("update projects set completion_date = '%s' where project_number = %s",
				currentDate, projectToFinalise);
		statement.executeUpdate(sqlStatement);
	}

	private static void viewIncompleteProjects(Statement statement) throws SQLException {
		// This method will display the incomplete projects in the database.
		sqlStatement = "select * from projects where status != 'complete'";
		ResultSet projectsTable = statement.executeQuery(sqlStatement);

		while (projectsTable.next()) {
			System.out.println("\nproject_number: " + projectsTable.getInt("project_number") + "\nproject_name: "
					+ projectsTable.getString("project_name") + "\nproject_deadline: "
					+ projectsTable.getDate("project_deadline") + "\nbuilding_type: "
					+ projectsTable.getString("building_type") + "\naddress: " + projectsTable.getString("address")
					+ "\nerf_number: " + projectsTable.getInt("erf_number") + "\ntotal_fee: "
					+ projectsTable.getDouble("total_fee") + "\namount_paid: " + projectsTable.getDouble("amount_paid")
					+ "\nstatus: " + projectsTable.getString("status") + "\ncompletion_date: "
					+ projectsTable.getDate("completion_date"));
		}
	}

	private static void viewPastDueProjects(Statement statement) throws SQLException {
		// This method will display the projects that have missed their deadline.
		LocalDate currentDate = LocalDate.now();
		sqlStatement = String.format("select * from projects where project_deadline < '%s'", currentDate);
		ResultSet projectsTable = statement.executeQuery(sqlStatement);

		while (projectsTable.next()) {
			System.out.println("\nproject_number: " + projectsTable.getInt("project_number") + "\nproject_name: "
					+ projectsTable.getString("project_name") + "\nproject_deadline: "
					+ projectsTable.getDate("project_deadline") + "\nbuilding_type: "
					+ projectsTable.getString("building_type") + "\naddress: " + projectsTable.getString("address")
					+ "\nerf_number: " + projectsTable.getInt("erf_number") + "\ntotal_fee: "
					+ projectsTable.getDouble("total_fee") + "\namount_paid: " + projectsTable.getDouble("amount_paid")
					+ "\nstatus: " + projectsTable.getString("status") + "\ncompletion_date: "
					+ projectsTable.getDate("completion_date"));
		}
	}
}