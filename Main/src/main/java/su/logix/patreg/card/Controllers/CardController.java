package su.logix.patreg.card.Controllers;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.Contract;
import su.logix.patreg.card.Models.VisitModel;
import su.logix.patreg.card.Models.XrayModel;
import su.logix.patreg.connection.DBConnection;
import su.logix.patreg.main.Models.PatientModel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class CardController {
    // patients id
    private static int id;

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
    public TableView<VisitModel> tvVisits;
    @FXML
    public TableView<XrayModel> tvXray;
    @FXML
    public TableColumn<VisitModel, Integer> colVisitId;
    @FXML
    public TableColumn<VisitModel, String> colVisitDate;
    @FXML
    public TableColumn<VisitModel, String> colVisitDiagnosis;
    @FXML
    public TableColumn<VisitModel, String> colVisitTreatment;
    @FXML
    public TableColumn<VisitModel, String> colVisitNote;
    @FXML
    public TableColumn<XrayModel, Integer> colXrayId;
    @FXML
    public TableColumn<XrayModel, String> colXrayDate;
    @FXML
    public TableColumn<XrayModel, String> colXrayNote;
    @FXML
    public Label lblMessage;

    private ObservableList<VisitModel> visitList = FXCollections.observableArrayList();
    private ObservableList<XrayModel> xrayList = FXCollections.observableArrayList();

    private PatientModel patientModel;

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
    private static int getId() {
        return id;
    }

    public static void setId(int id) {
        CardController.id = id;
    }

    @FXML
    public void initialize() {
        DBConnection connection = DBConnection.getInstance();
        try (Statement stmt = connection.getStatement()) {
            initGeneralFields(stmt);
            initVisitsTable(stmt);
            initXrayTable(stmt);
            setGraphics();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tfName.textProperty().addListener(this::filedsChanged);
        tfPhone.textProperty().addListener(this::filedsChanged);
        tfAddress.textProperty().addListener(this::filedsChanged);
        tfFormula.textProperty().addListener(this::filedsChanged);
        tfBite.textProperty().addListener(this::filedsChanged);
    }

    private void setGraphics() {

    }

    private void initXrayTable(Statement stmt) throws SQLException {
        xrayList.addAll(XrayModel.getList(stmt, getId()));
        colXrayId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colXrayDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colXrayNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        tvXray.setItems(xrayList);
    }

    private void refreshXrayTable() throws SQLException {
        xrayList.clear();
        initXrayTable(DBConnection.getInstance().getStatement());
    }

    private void initVisitsTable(Statement stmt) throws SQLException {
        visitList.addAll(VisitModel.getList(stmt, getId()));
        colVisitId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVisitDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colVisitDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        colVisitTreatment.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        colVisitNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        tvVisits.setItems(visitList);
    }

    private void refreshVisitsTable() throws SQLException {
        visitList.clear();
        initVisitsTable(DBConnection.getInstance().getStatement());
    }

    private void initGeneralFields(Statement stmt) throws SQLException {
        patientModel = PatientModel.getModelById(getId());
        tfName.setText(patientModel.getName());
        tfPhone.setText(patientModel.getPhone());
        tfAddress.setText(patientModel.getAddress());
        tfFormula.setText(patientModel.getFormula());
        tfBite.setText(patientModel.getBite());
    }

    @FXML
    public void save() {
        patientModel.setName(tfName.getText());
        patientModel.setPhone(tfPhone.getText());
        patientModel.setAddress(tfAddress.getText());
        patientModel.setFormula(tfFormula.getText());
        patientModel.setBite(tfBite.getText());
        try {
            patientModel.update();
            lblMessage.setText("Сохранено");
        } catch (SQLException e) {
            lblMessage.setText("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        isChanged = false;
    }

    @FXML
    public void addVisit() {
        try {
            VisitController.setVisit(0);
            VisitController.setPatient(getId());
            Parent root = FXMLLoader.load(getClass().getResource("/visit.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Добавить посещение");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHiding(e -> {
                try {
                    refreshVisitsTable();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void removeVisit() {
        try {
            VisitModel.removeById(tvVisits.getSelectionModel().getSelectedItem().getId());
            refreshVisitsTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addXray() {
        try {
            XrayController.setXray(0);
            XrayController.setPatient(getId());
            Parent root = FXMLLoader.load(getClass().getResource("/xray.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Добавить снимок");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHiding(e -> {
                try {
                    refreshXrayTable();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeXray() {
        try {
            XrayModel.removeById(tvXray.getSelectionModel().getSelectedItem().getId());
            refreshXrayTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void close() {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void visitMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            visitEdit();
        }
    }

    @FXML
    public void xrayMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            xrayLook();
        }
    }

    public void visitEditClicked() {
        visitEdit();
    }

    private void visitEdit() {
        if (tvVisits.getSelectionModel().getSelectedItem() != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                VisitController.setVisit(tvVisits.getSelectionModel().getSelectedItem().getId());
                VisitController.setPatient(getId());
                loader.setLocation(getClass().getResource("/visit.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Редактировать посещение");
                stage.setScene(new Scene(root));
                stage.show();
                stage.setOnHiding(e -> {
                    try {
                        refreshVisitsTable();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                });
                VisitController visitController = loader.getController();
                stage.setOnCloseRequest(visitController.getCloseEventHandler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void xrayEdit() {
        if (tvXray.getSelectionModel().getSelectedItem() != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                XrayController.setXray(tvXray.getSelectionModel().getSelectedItem().getId());
                XrayController.setPatient(getId());
                loader.setLocation(getClass().getResource("/xray.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Редактировать снимок");
                stage.setScene(new Scene(root));
                stage.show();
                stage.setOnHiding(e -> {
                    try {
                        refreshXrayTable();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                });
                XrayController xrayController = loader.getController();
                stage.setOnCloseRequest(xrayController.getCloseEventHandler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void xrayLook() {
        if (tvXray.getSelectionModel().getSelectedItem() != null) {
            File image = new File(tvXray.getSelectionModel().getSelectedItem().getPath());
            Desktop dt = Desktop.getDesktop();
            try {
                dt.open(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void xrayEditClicked() {
        xrayEdit();
    }

    public void xrayLookClicked() {
        xrayLook();
    }

    private void filedsChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!oldValue.equals(newValue)) {
            isChanged = true;
        }
    }

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }
}
