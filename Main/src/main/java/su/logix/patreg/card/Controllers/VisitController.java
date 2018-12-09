package su.logix.patreg.card.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.Contract;
import su.logix.patreg.card.Models.VisitModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

    private boolean isChanged = false;

    private javafx.event.EventHandler<WindowEvent> closeEventHandler = event -> {
        if (isChanged) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.setContentText("Сохранить изменения?");
            ButtonType buttonTypeYes = new ButtonType("Да");
            ButtonType buttonTypeNo = new ButtonType("Нет");
            ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeYes) {
                save();
            } else if (result.get() == buttonTypeNo) {

            } else {
                event.consume();
            }
        }
    };

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
        try {
            if (getVisit() > 0) {
                initFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (getVisit() > 0) {
            dpDate.getEditor().textProperty().addListener((observable, oldValue, newValue) -> dpDate.setValue(dpDate.getConverter().fromString(dpDate.getEditor().getText())));
            dpDate.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    isChanged = true;
                }
            });
            taDiagnosis.textProperty().addListener(this::filedsChanged);
            taTreatment.textProperty().addListener(this::filedsChanged);
            taNote.textProperty().addListener(this::filedsChanged);
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
        isChanged = false;
    }

    private void filedsChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!oldValue.equals(newValue)) {
            isChanged = true;
        }
    }

    javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }
}
