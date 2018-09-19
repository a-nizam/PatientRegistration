package su.logix.patreg.card.Models;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.NotNull;
import su.logix.patreg.connection.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VisitModel {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty patient;
    private final SimpleStringProperty date;
    private final SimpleStringProperty diagnosis;
    private final SimpleStringProperty treatment;
    private final SimpleStringProperty note;

    public VisitModel(int id, int patient, String date, String diagnosis, String treatment, String note) {
        this.id = new SimpleIntegerProperty(id);
        this.patient = new SimpleIntegerProperty(patient);
        this.date = new SimpleStringProperty(date);
        this.diagnosis = new SimpleStringProperty(diagnosis);
        this.treatment = new SimpleStringProperty(treatment);
        this.note = new SimpleStringProperty(note);
    }

    public static List<VisitModel> getList(@NotNull Statement stmt, int patient) throws SQLException {
        List<VisitModel> list = new ArrayList<>();
        String query = String.format("SELECT id, date, diagnosis, treatment, note FROM visits WHERE patient=%d", patient);
        ResultSet resultSet = stmt.executeQuery(query);
        while (resultSet.next()) {
            list.add(new VisitModel(resultSet.getInt("id"), patient, resultSet.getString("date"), resultSet.getString("diagnosis"),
                    resultSet.getString("treatment"), resultSet.getString("note")));
        }
        return list;
    }

    public static void removeById(int id) throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("DELETE FROM visits WHERE id=%d", id));
        }
    }

    public static VisitModel getModelById(int id) throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        VisitModel visitModel = null;
        try (Statement stmt = connection.getStatement();
             ResultSet resultSet = stmt.executeQuery(String.format("SELECT patient, date, diagnosis, treatment, note FROM visits WHERE id=%d", id))) {
            if (resultSet.next()) {
                visitModel = new VisitModel(id, resultSet.getInt("patient"), resultSet.getString("date"), resultSet.getString("diagnosis"),
                        resultSet.getString("treatment"), resultSet.getString("note"));
            }
        }
        return visitModel;
    }

    public void update() throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("UPDATE visits SET date='%s', diagnosis='%s', treatment='%s', note='%s' WHERE id=%d",
                    getDate(), getDiagnosis(), getTreatment(), getNote(), getId()));
        }
    }

    public void save() throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("INSERT INTO visits (patient, date, diagnosis, treatment, note) VALUES (%d, '%s', '%s', '%s', '%s')",
                    getPatient(), getDate(), getDiagnosis(), getTreatment(), getNote()));
        }
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getPatient() {
        return patient.get();
    }

    public void setPatient(int patient) {
        this.patient.set(patient);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getDiagnosis() {
        return diagnosis.get();
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis.set(diagnosis);
    }

    public String getTreatment() {
        return treatment.get();
    }

    public void setTreatment(String treatment) {
        this.treatment.set(treatment);
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }
}
