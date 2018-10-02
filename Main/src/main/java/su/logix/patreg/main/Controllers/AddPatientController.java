package su.logix.patreg.main.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import su.logix.patreg.main.Models.PatientModel;

import java.sql.SQLException;

public class AddPatientController {
    @FXML
    public TextField tfName;
    @FXML
    public TextField tfPhone;
    @FXML
    public TextField tfAddress;
    @FXML
    public TextField tfFormula;
    @FXML
    public TextField tfBite;
    @FXML
    public Label lblMessage;

    @FXML
    public void save() {
        PatientModel patientModel = new PatientModel(-1, tfName.getText(), tfPhone.getText(), tfAddress.getText(), tfFormula.getText(), tfBite.getText());
        try {
            patientModel.save();
            Stage stage = (Stage) tfName.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            lblMessage.setText("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
