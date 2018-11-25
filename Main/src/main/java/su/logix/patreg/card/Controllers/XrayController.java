package su.logix.patreg.card.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import su.logix.patreg.card.Models.XrayModel;
import su.logix.patreg.connection.DBConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class XrayController {
    private static int xray;
    private static int patient;
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    public DatePicker dpDate;
    @FXML
    public TextField tfFile;
    @FXML
    public TextArea taNote;
    @FXML
    public Label lblMessage;

    // for check if the file is changed
    private String oldFileName;

    @Contract(pure = true)
    private static int getPatient() {
        return patient;
    }

    static void setPatient(int patient) {
        XrayController.patient = patient;
    }

    @Contract(pure = true)
    private static int getXray() {
        return xray;
    }

    static void setXray(int xray) {
        XrayController.xray = xray;
    }

    @NotNull
    private static String getFileExtension(@NotNull Path path) {
        String fileName = path.getFileName().toString();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    @FXML
    public void initialize() {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            if (getXray() > 0) {
                initFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initFields() throws SQLException {
        XrayModel xrayModel = XrayModel.getModelById(getXray());
        dpDate.setValue(LocalDate.parse(xrayModel.getDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        tfFile.setText(xrayModel.getPath());
        taNote.setText(xrayModel.getNote());
        oldFileName = xrayModel.getPath();
    }

    @FXML
    public void fileHelper() {
        Stage stage = (Stage) tfFile.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            tfFile.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void save() {
        // Copy image to container
        Path copy = Paths.get("");
        if (!tfFile.getText().equals(oldFileName)) {
            try {
                if (getXray() > 0 && Files.exists(Paths.get(oldFileName))) {
                    Files.delete(Paths.get(oldFileName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Path original = Paths.get(tfFile.getText());
            copy = Paths.get("xray", Integer.toString(getPatient()), Long.toString(new Date().getTime()) + "." + getFileExtension(original));
            Path directory = copy.getParent();
            if (Files.notExists(directory)) {
                new File(directory.toString()).mkdir();
            }
            try {
                Files.copy(original, copy);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Save to db
        XrayModel xrayModel = new XrayModel(getXray(), getPatient(), copy.toString(), dpDate.getValue().toString(), taNote.getText());
        try {
            if (getXray() > 0) {
                xrayModel.update();
            } else {
                xrayModel.save();
            }
            Stage stage = (Stage) tfFile.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            lblMessage.setText("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
