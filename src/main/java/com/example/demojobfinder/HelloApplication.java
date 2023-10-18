package com.example.demojobfinder;

import com.mongodb.client.*;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.bson.Document;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;

public class HelloApplication extends Application {
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField nameField;
    private TextField jobField;
    private TextField locationField;
    private TextField salaryField;
    private TextField roleField;

    private MongoClient mongoClient;
    private MongoDatabase database;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        connectToMongoDB();

        primaryStage.setTitle("Demo Job Finder");

        displayLoginScreen(primaryStage);
    }

    private void connectToMongoDB() {
        String connectionString = "mongodb://localhost:27017";
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("JobfinderforBEL");
    }

    private void displayLoginScreen(Stage primaryStage) {
        Label startLabel = new Label("Glad you are here. Let's get started!");
        startLabel.setStyle("-fx-font-size: 25 px;");
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #349415, #1c6732);");

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Label orLabel = new Label("OR");
        Label ifLabel = new Label("if you are here for the first time.");



        usernameField.setStyle("-fx-border-color:#194679; -fx-text-fill:#036a73;-fx-background-color:#c29fa2;-fx-font-weight: bold;");
        passwordField.setStyle("-fx-border-color:#194679;-fx-text-fill:#036a73;-fx-background-color:#c29fa2;-fx-font-weight: bold;");
        usernameLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 20 px;");
        passwordLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 20 px;");
        loginButton.setStyle("-fx-background-color: #1d66be; -fx-text-fill: #1e1919;-fx-font-weight: bold;");
        orLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 20 px;");
        registerButton.setStyle("-fx-background-color: #1d66be; -fx-text-fill: #1e1919;-fx-font-weight: bold;");
        ifLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 10 px;");


        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (checkUserExistsInMongoDB(username)) {
                if (checkUserCredentialsMatchInMongoDB(username, password)) {
                    AlertBox.display("Yay!", "Login Successful!");
                    displayUserDetails(primaryStage, username);
                } else {
                    AlertBox.display("Invalid password", "Please enter a valid password.");
                }
            } else {
                AlertBox.display("User not found", "User does not exist.");
            }
        });

        registerButton.setOnAction(event -> displayRegisterScreen(primaryStage));

        root.getChildren().addAll(startLabel, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, orLabel, registerButton, ifLabel);

        Scene scene = new Scene(root, 500, 350);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayRegisterScreen(Stage primaryStage) {
        primaryStage.close();

        VBox root = new VBox(7);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #508d3d, #33557c);");

        Label nameLabel = createLabelWithStyle("Name:");
        nameField = createTextFieldWithStyle();
        Label jobLabel = createLabelWithStyle("Job:");
        jobField = createTextFieldWithStyle();
        Label locationLabel = createLabelWithStyle("Location:");
        locationField = createTextFieldWithStyle();
        Label salaryLabel = createLabelWithStyle("Salary(can be left empty): ");
        salaryField = createTextFieldWithStyle();
        Label roleLabel = createLabelWithStyle("Role(Aspiring employee or employer or employee):");
        roleField = createTextFieldWithStyle();
        Label usernameLabel = createLabelWithStyle("Username:");
        usernameField = createTextFieldWithStyle();
        Label passwordLabel = createLabelWithStyle("Password:");
        passwordField = createPasswordFieldWithStyle();
        Button registerButton = new Button("Register");

        Button loginMaybeButton = new Button("\u276E Login");
        Label loginMaybeLabel = new Label("If you already have an account: ");

        registerButton.setStyle("-fx-background-color: #1d66be; -fx-text-fill: #1e1919;-fx-font-weight: bold;");
        loginMaybeButton.setOnAction(actionEvent -> displayLoginScreen(primaryStage));
        loginMaybeButton.setStyle("-fx-background-color: #1d66be; -fx-text-fill: #1e1919;-fx-font-weight: bold;");
        loginMaybeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        registerButton.setOnAction(event -> {
            String name = nameField.getText();
            String job = jobField.getText();
            String location = locationField.getText();
            String salary = salaryField.getText();
            String role = roleField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (!validSalary(salary)) {
                AlertBox.display("Registration Error", "Salary field can be empty, negotiable or a positive integer only!");
                return;
            }

            if (!validRole(role)) {
                AlertBox.display("Wait", "The role needs to be one of the three mentioned!");
                return;
            }




            AlertBox.display("Yay!", "You are registered");
            if (name.isEmpty() || job.isEmpty() || location.isEmpty() || role.isEmpty() || username.isEmpty() || password.isEmpty()) {
                AlertBox.display("Incomplete fields", "Please fill in all the fields(salary optional).");
            } else if (checkUserExistsInMongoDB(username)) {
                AlertBox.display("Registration Error", "Username already exists. Please choose a different one.");
            } else {
                saveUserToMongoDB(name, job, location, salary, role, username, password);
                AlertBox.display("Registration Successful", "User registered successfully!");
                displayLoginScreen(primaryStage);
            }
        });



        root.getChildren().addAll(
                nameLabel, nameField,
                jobLabel, jobField,
                locationLabel, locationField,
                salaryLabel, salaryField,
                roleLabel, roleField,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                registerButton, loginMaybeLabel, loginMaybeButton
        );

        Scene scene = new Scene(root, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createLabelWithStyle(String labelText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        return label;
    }

    private TextField createTextFieldWithStyle() {
        TextField textField = new TextField();
        textField.setStyle("-fx-border-color:#194679; -fx-text-fill:#036a73; -fx-background-color:#c29fa2; -fx-font-weight:bold;");
        return textField;
    }

    private PasswordField createPasswordFieldWithStyle() {
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-border-color:#194679; -fx-text-fill:#036a73; -fx-background-color:#c29fa2; -fx-font-weight:bold;");
        return passwordField;
    }





    private boolean checkUserExistsInMongoDB(String username) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("username", username);
        return collection.countDocuments(query) > 0;
    }

    private boolean checkUserCredentialsMatchInMongoDB(String username, String password) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("username", username).append("password", password);
        return collection.countDocuments(query) > 0;
    }

    private void saveUserToMongoDB(String name, String job, String location, String salary, String role, String username, String password) {
        MongoCollection<Document> collection = database.getCollection("users");

        // Check if the username or password already exists
        Document existingUser = collection.find(
                new Document("$or",
                        Arrays.asList(
                                new Document("username", username),
                                new Document("password", password)
                        )
                )
        ).first();

        if (existingUser != null) {
            AlertBox.display("Registration Error", "Username or password already exists. Please choose a different one.");
            return;
        }

        Document user = new Document("name", name)
                .append("job", job)
                .append("location", location)
                .append("salary", salary)
                .append("role", role)
                .append("username", username)
                .append("password", password);

        collection.insertOne(user);
    }
    private void displayUserDetails(Stage primaryStage, String username) {
        primaryStage.close();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #376da9, #508d3d);");

        Label welcomeLabel = new Label("Welcome, " + username + "!");
        Label userDetailsLabel = new Label("User Details:");

        welcomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px");
        userDetailsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");

        VBox userDetailBox = new VBox(5);
        userDetailBox.setPadding(new Insets(10));
        userDetailBox.setStyle("-fx-background-color: #73938b;-fx-font-size: 10px");

        MongoCollection<Document> collection = database.getCollection("users");
        FindIterable<Document> userDetails = collection.find();

        for (Document user : userDetails) {
            user.remove("password"); // Remove the password field from the document
            VBox userDetailsBox = new VBox(5);
            userDetailsBox.setStyle("-fx-border-color: #036a73; -fx-border-width: 5px; -fx-padding: 5px;");

            user.forEach((key, value) -> {
                Label label = new Label(key + ": " + value);
                label.setStyle("-fx-text-fill: #05244d; -fx-font-weight: bold;-fx-font-size: 15px");
                userDetailsBox.getChildren().add(label);
            });

            userDetailBox.getChildren().add(userDetailsBox);
        }

        ScrollPane scrollPane = new ScrollPane(userDetailBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Button logoutButton = new Button("\u276E Logout");
        logoutButton.setStyle("-fx-background-color: #0476fa; -fx-text-fill: #1e1919; -fx-font-weight: bold;");
        logoutButton.setOnAction(event -> displayLoginScreen(primaryStage));

        Button seeEmployers = new Button("See Employers");
        Button seeEmployees = new Button("See Employees");
        Button seeAspiringEmployees = new Button("See Aspiring employees");
        seeEmployees.setStyle("-fx-background-color: #0476fa; -fx-text-fill: #1e1919; -fx-font-weight: bold;");
        seeEmployers.setStyle("-fx-background-color: #0476fa; -fx-text-fill: #1e1919; -fx-font-weight: bold;");
        seeAspiringEmployees.setStyle("-fx-background-color: #0476fa; -fx-text-fill: #1e1919; -fx-font-weight: bold;");
        seeEmployees.setOnAction(actionEvent -> allEmployeesPage(primaryStage));
        seeEmployers.setOnAction(actionEvent -> allEmployersPage(primaryStage));
        seeAspiringEmployees.setOnAction(actionEvent -> allAspiringEmployeesPage(primaryStage));

        root.getChildren().addAll(welcomeLabel, userDetailsLabel, scrollPane, logoutButton, seeEmployees, seeEmployers, seeAspiringEmployees);

        Scene scene = new Scene(root, 400, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }



    private boolean checkPasswordExistsInMongoDB(String password) {
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("password", password);
        return collection.countDocuments(query) > 0;
    }

    private boolean validSalary(String salary) {
        if (salary.equals("") || salary.equalsIgnoreCase("Negotiable")) {
            return true;
        }

        try {
            // Remove commas from salary string
            String salaryWithoutCommas = salary.replace(",", "");
            int sal = Integer.parseInt(salaryWithoutCommas);
            return true; // The salary is valid and can be parsed as an integer
        } catch (NumberFormatException e) {
            return false; // The salary is invalid and cannot be parsed as an integer
        }
    }


    private void allEmployeesPage(Stage primaryStage) {
        Label allEmployees = new Label("Here are all the employees: ");
        allEmployees.setStyle("-fx-font-weight: bold;-fx-font-size: 20px");
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #508d3d, #33557c);");

        VBox userDetailBox = new VBox(5);
        userDetailBox.setPadding(new Insets(10));
       // userDetailBox.setStyle("-fx-background-color: #c96872;-fx-border-color: #036a73; -fx-border-width: 5px; -fx-padding: 5px;");

        MongoCollection<Document> collection = database.getCollection("users");
        FindIterable<Document> userDetails = collection.find();

        for (Document user : userDetails) {
            String role = user.getString("role");
            if (role != null && role.equalsIgnoreCase("employee")) {
                user.remove("password"); // Remove the password field from the document

                VBox employeeBox = new VBox(5);
                employeeBox.setPadding(new Insets(10));
                //employeeBox.setStyle("-fx-background-color: #e3977a;");
                employeeBox.setStyle("-fx-background-color: #c96872;-fx-border-color: #036a73; -fx-border-width: 5px; -fx-padding: 5px;");
                employeeBox.getStyleClass().add("employee-box");

                user.forEach((key, value) -> {
                    Label label = new Label(key + ": " + value);
                    label.setStyle("-fx-text-fill: #1e1919; -fx-font-weight: bold;-fx-font-size: 15px");
                    employeeBox.getChildren().add(label);
                });

                //employeeBox.getChildren().add(new Label("-------------------"));
                userDetailBox.getChildren().add(employeeBox);

            }
        }

        ScrollPane scrollPane = new ScrollPane(userDetailBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.getStyleClass().add("scroll-pane");

        Button backButton = new Button("\u276E Back to all users.");
        backButton.setStyle("-fx-background-color: #0476fa; -fx-text-fill: #1e1919;-fx-font-weight: bold;");
        backButton.setOnAction(actionEvent -> displayUserDetails(primaryStage, usernameField.getText()));

        root.getChildren().addAll(allEmployees, scrollPane, backButton);

        Scene scene = new Scene(root, 400, 600);
        scene.getStylesheets().add("styles.css"); // Add the CSS file for styling

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void allEmployersPage(Stage primaryStage) {
        Label allEmployers = new Label("Here are all the employers:");
        allEmployers.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #508d3d, #33557c);");

        VBox userDetailBox = new VBox(5);
        userDetailBox.setPadding(new Insets(10));
       // userDetailBox.setStyle("-fx-background-color: -fx-background;");

        MongoCollection<Document> collection = database.getCollection("users");
        FindIterable<Document> userDetails = collection.find();

        for (Document user : userDetails) {
            String role = user.getString("role");
            if (role != null && role.equalsIgnoreCase("employer")) {
                user.remove("password");

                VBox employerBox = new VBox(5);
                employerBox.setPadding(new Insets(10));
                employerBox.setStyle("-fx-background-color: #c96872;");
                employerBox.getStyleClass().add("employer-box");

                user.forEach((key, value) -> {
                    Label label = new Label(key + ": " + value);
                    label.setStyle("-fx-text-fill: #093d81; -fx-font-weight: bold; -fx-font-size: 15px");
                    employerBox.getChildren().add(label);
                });

                userDetailBox.getChildren().add(employerBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(userDetailBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-background-insets: 0; -fx-padding: 10px;");
        scrollPane.getStyleClass().add("scroll-pane");

        Button backButton = new Button("\u276E Back to all users");
        backButton.setStyle("-fx-background-color: #1d66be; -fx-text-fill: #1e1919; -fx-font-weight: bold;");
        backButton.setOnAction(actionEvent -> displayUserDetails(primaryStage, usernameField.getText()));

        root.getChildren().addAll(allEmployers, scrollPane, backButton);

        Scene scene = new Scene(root, 400, 600);
        scene.getStylesheets().add("styles.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }







    private void allAspiringEmployeesPage(Stage primaryStage) {
        Label allAspiringEmployees = new Label("Here are all the aspiring employees: ");
        allAspiringEmployees.setStyle("-fx-font-weight: bold;-fx-font-size: 20px");
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #87a47e, #708091);");

        VBox userDetailBox = new VBox(5);
        userDetailBox.setPadding(new Insets(10));
        userDetailBox.setStyle("-fx-background-color: #c96872;");

        MongoCollection<Document> collection = database.getCollection("users");
        FindIterable<Document> userDetails = collection.find();

        for (Document user : userDetails) {
            String role = user.getString("role");
            if (role != null && role.equalsIgnoreCase("aspiring employee")) {
                user.remove("password"); // Remove the password field from the document

                VBox userDetailsBox = new VBox(5);
                userDetailsBox.setStyle("-fx-border-color: #036a73; -fx-border-width: 5px; -fx-padding: 5px;");

                user.forEach((key, value) -> {
                    Label label = new Label(key + ": " + value);
                    label.setStyle("-fx-text-fill: #490560; -fx-font-weight: bold;-fx-font-size: 15px");
                    userDetailsBox.getChildren().add(label);
                });

                //userDetailsBox.getChildren().add(new Label("-------------------"));
                userDetailBox.getChildren().add(userDetailsBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(userDetailBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Button backButton = new Button("\u276E Back to all users.");
        backButton.setStyle("-fx-background-color: #1d66be; -fx-text-fill: #1e1919;-fx-font-weight: bold;");
        backButton.setOnAction(actionEvent -> {
            displayUserDetails(primaryStage, usernameField.getText());
        });

        root.getChildren().addAll(allAspiringEmployees, scrollPane, backButton);

        Scene scene = new Scene(root, 400, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static boolean validRole(String role){
        if(role.equalsIgnoreCase("employee") || role.equalsIgnoreCase("employer") || role.equalsIgnoreCase(" aspiring employee")){
            return true;
        }
        return false;
    }

}
