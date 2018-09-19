package su.logix.patreg.main.Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import su.logix.patreg.connection.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PatientModel {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty address;
    private final SimpleStringProperty formula;
    private final SimpleStringProperty bite;

    public PatientModel(int id, String name, String phone, String address, String formula, String bite) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.formula = new SimpleStringProperty(formula);
        this.bite = new SimpleStringProperty(bite);
    }

    public void save() throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("INSERT INTO patients (name, phone, address, formula, bite) VALUES ('%s', '%s', '%s', '%s', '%s')",
                    getName(), getPhone(), getAddress(), getFormula(), getBite()));
        }
    }

    public void update() throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("UPDATE patients SET name='%s', phone='%s', address='%s', formula='%s', bite='%s' WHERE id=%d",
                    getName(), getPhone(), getAddress(), getFormula(), getBite(), getId()));
        }
    }

    public static PatientModel getModelById(int id) throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        PatientModel patientModel = null;
        try (Statement stmt = connection.getStatement();
             ResultSet resultSet = stmt.executeQuery(String.format("SELECT id, name, phone, address, formula, bite FROM patients WHERE id=%d", id))) {
            if (resultSet.next()) {
                patientModel = new PatientModel(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("phone"),
                        resultSet.getString("address"), resultSet.getString("formula"), resultSet.getString("bite"));
            }
        }
        return patientModel;
    }

    public static void removeById(int id) throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("DELETE FROM patients WHERE id=%d", id));
        }
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getFormula() {
        return formula.get();
    }

    public void setFormula(String formula) {
        this.formula.set(formula);
    }

    public String getBite() {
        return bite.get();
    }

    public void setBite(String bite) {
        this.bite.set(bite);
    }
}
