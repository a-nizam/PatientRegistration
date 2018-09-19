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

public class XrayModel {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty patient;
    private final SimpleStringProperty path;
    private final SimpleStringProperty date;
    private final SimpleStringProperty note;

    public XrayModel(int id, int patient, String path, String date, String note) {
        this.id = new SimpleIntegerProperty(id);
        this.patient = new SimpleIntegerProperty(patient);
        this.path = new SimpleStringProperty(path);
        this.date = new SimpleStringProperty(date);
        this.note = new SimpleStringProperty(note);
    }

    public static List<XrayModel> getList(@NotNull Statement stmt, int patient) throws SQLException {
        List<XrayModel> list = new ArrayList<>();
        String query = String.format("SELECT id, date, path, note FROM xray WHERE patient=%d", patient);
        ResultSet resultSet = stmt.executeQuery(query);
        while (resultSet.next()) {
            list.add(new XrayModel(resultSet.getInt("id"), patient, resultSet.getString("path"), resultSet.getString("date"),
                    resultSet.getString("note")));
        }
        return list;
    }

    public static void removeById(int id) throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("DELETE FROM xray WHERE id=%d", id));
        }
    }

    public static XrayModel getModelById(int id) throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        XrayModel xrayModel = null;
        try (Statement stmt = connection.getStatement();
             ResultSet resultSet = stmt.executeQuery(String.format("SELECT patient, path, date, note FROM xray WHERE id=%d", id))) {
            if (resultSet.next()) {
                xrayModel = new XrayModel(id, resultSet.getInt("patient"), resultSet.getString("path"),
                        resultSet.getString("date"), resultSet.getString("note"));
            }
        }
        return xrayModel;
    }

    public void save() throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("INSERT INTO xray (patient, path, date, note) VALUES (%d, '%s', '%s', '%s')",
                    getPatient(), getPath(), getDate(), getNote()));
        }
    }

    public void update() throws SQLException {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            stmt.executeUpdate(String.format("UPDATE xray SET patient=%d, path='%s', date='%s', note='%s' WHERE id=%d",
                    getPatient(), getPath(), getDate(), getNote(), getId()));
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

    public String getPath() {
        return path.get();
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }
}
