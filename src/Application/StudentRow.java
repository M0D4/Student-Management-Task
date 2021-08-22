package Application;

import entities.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;

public class StudentRow {
    private int No;
    private String name, department, nationalID, mobileNumber;
    private Button update, delete;

    public StudentRow(int no, String name, String department, String nationalID, String mobileNumber, Button update, Button delete) {
        this.No = no;
        this.name = name;
        this.department = department;
        this.nationalID = nationalID;
        this.mobileNumber = mobileNumber;
        this.update = update;
        this.delete = delete;
        update.setTooltip(new Tooltip("Update"));
        delete.setTooltip(new Tooltip("Delete"));
        delete.setId("deleteButton");
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Button getUpdate() {
        return update;
    }

    public void setUpdate(Button update) {
        this.update = update;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }

    public static ObservableList<StudentRow> toObservableList(ArrayList<Student> students){
        ObservableList<StudentRow> list = FXCollections.observableArrayList();
        for(Student student: students){
            list.add(new StudentRow(student.getNo(), student.getName(), student.getDepartment(),
                    student.getNationalID(), student.getMobileNumber(), new Button("U"), new Button("D")));
        }
        return list;
    }
}
