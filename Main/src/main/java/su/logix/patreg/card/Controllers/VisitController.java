package su.logix.patreg.card.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import su.logix.patreg.card.Models.VisitModel;
import su.logix.patreg.connection.DBConnection;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VisitController {
    private static int patient;
    private static int visit;

    @FXML
    public DatePicker dpDate;
    @FXML
    public TextArea taDiagnosis;
    @FXML
    public TextArea taTreatment;
    @FXML
    public TextArea taNote;
    @FXML
    public Label lblMessage;

    @Contract(pure = true)
    private static int getPatient() {
        return patient;
    }

    static void setPatient(int patient) {
        VisitController.patient = patient;
    }

    @Contract(pure = true)
    private static int getVisit() {
        return visit;
    }

    static void setVisit(int visit) {
        VisitController.visit = visit;
    }

    @FXML
    public void initialize() {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            if (getVisit() > 0) {
                initFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initFields() throws SQLException {
        VisitModel visitModel = VisitModel.getModelById(getVisit());
        dpDate.setValue(LocalDate.parse(visitModel.getDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        taDiagnosis.setText(visitModel.getDiagnosis());
        taTreatment.setText(visitModel.getTreatment());
        taNote.setText(visitModel.getNote());
    }

    @FXML
    public void save() {
        VisitModel visitModel = new VisitModel(getVisit(), getPatient(), dpDate.getValue().toString(), taDiagnosis.getText(), taTreatment.getText(), taNote.getText());
        try {
            if (getVisit() > 0) {
                visitModel.update();
            } else {
                visitModel.save();
            }
            Stage stage = (Stage) dpDate.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            lblMessage.setText("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
