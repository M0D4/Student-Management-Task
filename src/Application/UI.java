package Application;

import DataAccess.DepartmentDA;
import DataAccess.StudentDA;
import DataAccess.UsersDA;
import entities.Student;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.DataFormatException;

public class UI extends Application {

    private Stage window;
    private Scene homeScene, loginScene, addingScene, updateScene;
    private Alert confirmAlert, errorAlert, infoAlert;
    private TableView<StudentRow> table;

    @Override
    public void start(Stage primaryStage) throws DataFormatException, SQLException {
        window = primaryStage;

        window.setOnCloseRequest(e -> {
            System.out.println(window.getWidth() + " " + window.getHeight());
            e.consume();
            closeApplicationCheck();
        });


        initErrorAlert();
        initConfirmAlert();
        initInfoAlert();

        initLoginScene();
        //initHomeScene();
        window.show();
    }



    private void initLoginScene(){
        GridPane loginLayout = new GridPane();
        loginLayout.setPadding(new Insets(10, 10, 10, 10));
        loginLayout.setVgap(8);
        loginLayout.setHgap(10);


        Label usernameLabel = new Label("Username: ");
        GridPane.setConstraints(usernameLabel, 0, 0);
        TextField usernameTextField = new TextField();
        GridPane.setConstraints(usernameTextField, 1, 0);

        Label passwordLabel = new Label("Password: ");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setAlignment(Pos.CENTER);
        loginButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordField.getText();
            try {
                if(UsersDA.userExists(username, password)){
                    initHomeScene();
                }else{
                    errorAlert.setContentText("Invalid username and/or password");
                    errorAlert.showAndWait();
                }
            } catch (SQLException | DataFormatException throwables) {
                showErrorMessage(throwables.getMessage());
            }
        });


        loginButton.setDefaultButton(true);

        loginLayout.getChildren().addAll(usernameLabel, usernameTextField, passwordLabel, passwordField, loginButton);

        loginScene = new Scene(loginLayout, 400, 150);
        loginScene.getStylesheets().add(Objects.requireNonNull(UI.class.getResource("Light.css")).toExternalForm());
        window.setTitle("Login");
        window.setScene(loginScene);
        window.setResizable(false);
    }

    private void initHomeScene() throws DataFormatException, SQLException {
        GridPane homeLayout = new GridPane();
        homeLayout.setPadding(new Insets(10, 10, 10, 10));
        homeLayout.setVgap(8);
        homeLayout.setHgap(10);

        TextField searchTextField = new TextField();
        searchTextField.setPromptText("Name or National ID");

        Button searchButton = new Button("Search");

        searchButton.setOnAction(e -> {
            String s = searchTextField.getText();
            try {
                setTableItems(table, s);
            } catch (Exception ex) {
                showErrorMessage(ex.getMessage());
            }
        });

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            try {
                setTableItems(table, "");
                searchTextField.clear();
            } catch (Exception ex) {
                showErrorMessage(ex.getMessage());
            }
        });


        HBox searchHBox = new HBox(10);
        searchHBox.getChildren().addAll(searchTextField, searchButton, resetButton);

        Button addStudentButton = new Button("Add Student");
        HBox addStudentHBox = new HBox(10);
        addStudentHBox.getChildren().addAll(addStudentButton);

        HBox allButtonsHBox = new HBox(500);
        allButtonsHBox.getChildren().addAll(searchHBox, addStudentHBox);
        GridPane.setConstraints(allButtonsHBox, 0, 0);
        addStudentButton.setOnAction(e -> {
            try {
                initAddingScene();
            } catch (SQLException throwables) {
                showErrorMessage(throwables.getMessage());
            }
        });

        table = new TableView<>();

        initTableColumns(table);

        GridPane.setConstraints(table, 0, 1);

        homeLayout.getChildren().addAll(allButtonsHBox, table);

        homeScene = new Scene(homeLayout, 980, 511);
        window.setMinWidth(981);
        window.setMinHeight(511);
        homeScene.getStylesheets().add(UI.class.getResource("Light.css").toExternalForm());
        window.setScene(homeScene);
        window.setTitle("Student Management System");
    }

    private void initTableColumns(TableView<StudentRow> table) throws DataFormatException, SQLException {
        TableColumn<StudentRow, Student> NoColumn = new TableColumn<>("No.");
        NoColumn.setMinWidth(40);
        NoColumn.setPrefWidth(40);
        NoColumn.setCellValueFactory(new PropertyValueFactory<>("No"));

        TableColumn<StudentRow, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StudentRow, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setMinWidth(100);
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<StudentRow, String> nationalIDColumn = new TableColumn<>("National ID");
        nationalIDColumn.setMinWidth(150);
        nationalIDColumn.setCellValueFactory(new PropertyValueFactory<>("nationalID"));

        TableColumn<StudentRow, String> mobileNumberColumn = new TableColumn<>("Mobile Number");
        mobileNumberColumn.setMinWidth(130);
        mobileNumberColumn.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));

        TableColumn<StudentRow, Button> updateColumn = new TableColumn<>("Update");
        updateColumn.setMinWidth(40);
        updateColumn.setCellValueFactory(new PropertyValueFactory<>("update"));
        updateColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<StudentRow, Button> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setMinWidth(40);
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("delete"));
        deleteColumn.setStyle("-fx-alignment: CENTER;");

        setTableItems(table, "");
        table.getColumns().addAll(NoColumn, nameColumn, departmentColumn,
                nationalIDColumn, mobileNumberColumn, updateColumn, deleteColumn);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setTableItems(TableView<StudentRow> table, String s) throws DataFormatException, SQLException {
        ObservableList<StudentRow> list = StudentRow.toObservableList(StudentDA.search(s));
        for(StudentRow studentRow: list){
            studentRow.getUpdate().setOnAction(e -> {
                try {
                    initUpdateScene(studentRow.getNationalID());
                } catch (DataFormatException dataFormatException) {
                    showErrorMessage(dataFormatException.getMessage());
                } catch (SQLException throwables) {
                    showErrorMessage(throwables.getMessage());
                }
            });

            studentRow.getDelete().setOnAction(e -> {
                try {
                    deleteStudent(studentRow.getName(), studentRow.getNationalID());
                } catch (SQLException | DataFormatException throwables) {
                    showErrorMessage(throwables.getMessage());
                }
            });
        }
        table.setItems(list);
    }

    private void deleteStudent(String name, String nationalID) throws SQLException, DataFormatException {
        confirmAlert.setContentText("Are you sure you want to delete student " + name + "?");
        Optional<ButtonType> answer = confirmAlert.showAndWait();
        if(answer.get() == ButtonType.OK){
            StudentDA.delete(nationalID);
            showSuccessMessage("Deleted student " + name);
            setTableItems(table, "");
        }
    }

    private void initAddingScene() throws SQLException {
        Stage addWindow = new Stage();
        addWindow.setOnCloseRequest(e -> {
            System.out.println(addWindow.getWidth() + " " + addWindow.getHeight());
            e.consume();
            addWindow.close();
        });

        GridPane addingLayout = new GridPane();
        addingLayout.setPadding(new Insets(10, 10, 10, 10));
        addingLayout.setVgap(30);
        addingLayout.setHgap(10);

        Label nameLabel = new Label("Name");
        GridPane.setConstraints(nameLabel, 0, 0);

        TextField nameTextField = new TextField();
        GridPane.setConstraints(nameTextField, 1, 0);

        Label departmentLabel = new Label("Department");
        GridPane.setConstraints(departmentLabel, 0, 1);
        ComboBox<String> departmentComboBox = new ComboBox<>();
        for(String department: DepartmentDA.getAll()){
            departmentComboBox.getItems().add(department);
        }
        departmentComboBox.setPromptText("Choose department");
        GridPane.setConstraints(departmentComboBox, 1, 1);

        Label nationalIDLabel = new Label("National ID");
        GridPane.setConstraints(nationalIDLabel, 0, 2);
        TextField nationalIDTextField = new TextField();
        GridPane.setConstraints(nationalIDTextField, 1, 2);

        Label mobileNumberLabel = new Label("Mobile Number");
        GridPane.setConstraints(mobileNumberLabel, 0, 3);
        TextField mobileNumberTextField = new TextField();
        GridPane.setConstraints(mobileNumberTextField, 1, 3);

        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(e -> {
            try {
                StudentDA.insert(nameTextField.getText(), departmentComboBox.getValue()
                , nationalIDTextField.getText(), mobileNumberTextField.getText());
                showSuccessMessage("Added student " + nameTextField.getText().trim());
                setTableItems(table, "");
                addWindow.close();
            } catch (DataFormatException dataFormatException) {
                showErrorMessage(dataFormatException.getMessage());
            } catch (SQLException throwables) {
                showErrorMessage(throwables.getMessage());
            }
        });

        saveButton.setDefaultButton(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            addWindow.close();
        });

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.getChildren().addAll(saveButton, cancelButton);
        GridPane.setConstraints(buttonsHBox, 1, 4);

        addingLayout.getChildren().addAll(nameLabel, nameTextField, departmentLabel,
                departmentComboBox, nationalIDLabel, nationalIDTextField,
                mobileNumberLabel, mobileNumberTextField, buttonsHBox);

        addingScene = new Scene(addingLayout, 348, 356);
        addingScene.getStylesheets().add(UI.class.getResource("Light.css").toExternalForm());
        addWindow.setMaxWidth(373);
        addWindow.setMaxHeight(366);
        addWindow.setMinWidth(373);
        addWindow.setMinHeight(366);
        addWindow.setScene(addingScene);
        addWindow.setTitle("Add Student");
        addWindow.show();
        addWindow.setResizable(false);
    }

    private void initUpdateScene(String oldNationalID) throws DataFormatException, SQLException {
        Stage updateWindow = new Stage();
        updateWindow.initModality(Modality.APPLICATION_MODAL);

        Student student = StudentDA.search(oldNationalID).get(0);
        GridPane updateLayout = new GridPane();
        updateLayout.setPadding(new Insets(10, 10, 10, 10));
        updateLayout.setVgap(30);
        updateLayout.setHgap(10);

        Label nameLabel = new Label("Name");
        GridPane.setConstraints(nameLabel, 0, 0);

        TextField nameTextField = new TextField(student.getName());
        GridPane.setConstraints(nameTextField, 1, 0);

        Label departmentLabel = new Label("Department");
        GridPane.setConstraints(departmentLabel, 0, 1);
        ComboBox<String> departmentComboBox = new ComboBox<>();
        for(String department: DepartmentDA.getAll()){
            departmentComboBox.getItems().add(department);
        }
        departmentComboBox.setValue(student.getDepartment());
        GridPane.setConstraints(departmentComboBox, 1, 1);

        Label nationalIDLabel = new Label("National ID");
        GridPane.setConstraints(nationalIDLabel, 0, 2);
        TextField nationalIDTextField = new TextField(student.getNationalID());
        GridPane.setConstraints(nationalIDTextField, 1, 2);

        Label mobileNumberLabel = new Label("Mobile Number");
        GridPane.setConstraints(mobileNumberLabel, 0, 3);
        TextField mobileNumberTextField = new TextField(student.getMobileNumber());
        GridPane.setConstraints(mobileNumberTextField, 1, 3);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                StudentDA.update(oldNationalID, nameTextField.getText(), departmentComboBox.getValue()
                        , nationalIDTextField.getText(), mobileNumberTextField.getText());
                showSuccessMessage("Updated student " + nameTextField.getText().trim());
                setTableItems(table, "");
                updateWindow.close();
            } catch (DataFormatException dataFormatException) {
                showErrorMessage(dataFormatException.getMessage());
            } catch (SQLException throwables) {
                showErrorMessage(throwables.getMessage());
            }
        });

        saveButton.setDefaultButton(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> updateWindow.close());

        HBox buttonsHBox = new HBox(20);
        buttonsHBox.getChildren().addAll(saveButton, cancelButton);
        GridPane.setConstraints(buttonsHBox, 1, 4);

        updateLayout.getChildren().addAll(nameLabel, nameTextField, departmentLabel,
                departmentComboBox, nationalIDLabel, nationalIDTextField,
                mobileNumberLabel, mobileNumberTextField, buttonsHBox);

        updateScene = new Scene(updateLayout, 400, 310);
        updateScene.getStylesheets().add(UI.class.getResource("Light.css").toExternalForm());
        updateWindow.setScene(updateScene);
        updateWindow.setTitle("Update Student");
        updateWindow.show();
        updateWindow.setResizable(false);
    }

    private void returnToHomeScence(){
        window.setScene(homeScene);
        window.setTitle("Student Management System");
    }

    private void closeApplicationCheck(){
        confirmAlert.setContentText("Are you sure you want to quit?");
        Optional<ButtonType> answer = confirmAlert.showAndWait();
        if(answer.get() == ButtonType.OK){
            window.close();
        }
    }

    private void initErrorAlert(){
        errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
    }

    private void initConfirmAlert(){
        confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
    }

    private void initInfoAlert() {
        infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setHeaderText(null);
        infoAlert.setTitle("Info");
    }

    private void showErrorMessage(String message){
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
    private void showSuccessMessage(String s){
        infoAlert.setContentText(s + " successfully");
        infoAlert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
