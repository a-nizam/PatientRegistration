package su.logix.patreg.main.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import su.logix.patreg.card.Controllers.CardController;
import su.logix.patreg.connection.DBConnection;
import su.logix.patreg.main.Models.PatientModel;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {
    @FXML
    public TableView<PatientModel> tableView;
    @FXML
    public TableColumn<PatientModel, Integer> columnId;
    @FXML
    public TableColumn<PatientModel, String> columnName;
    @FXML
    public TableColumn<PatientModel, String> columnPhone;
    @FXML
    public TableColumn<PatientModel, String> columnAddress;

    private ObservableList<PatientModel> tableList;

    @FXML
    public void initialize() {
        String url = "jdbc:sqlite:patients.db";
        DBConnection dbConnection = DBConnection.getInstance();
        dbConnection.setParams(url);
        try {
            dbConnection.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement stmt = dbConnection.getStatement()) {
            initTable(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initTable(Statement stmt) throws SQLException {
        tableList = FXCollections.observableArrayList();
        try (ResultSet resultSet = stmt.executeQuery("SELECT id, name, phone, address FROM patients ORDER BY id ASC")) {
            while (resultSet.next()) {
                tableList.add(new PatientModel(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("phone"), resultSet.getString("address"), "", ""));
            }
        }

        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        tableView.setItems(tableList);
    }

    private void refreshTable() throws SQLException {
        tableList.clear();
        initTable(DBConnection.getInstance().getStatement());
    }

    @FXML
    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                int cardId = tableView.getSelectionModel().getSelectedItem().getId();
                CardController.setId(cardId);
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/card.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Карточка пациента №" + cardId);
                    stage.setScene(new Scene(root));
                    stage.show();
                    stage.setOnCloseRequest(e -> {
                        try {
                            refreshTable();
                        } catch (SQLException el) {
                            el.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addPatient() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/addPatient.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Добавление пациента");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHiding(e -> {
                try {
                    refreshTable();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePatient() {
        try {
            PatientModel.removeById(tableView.getSelectionModel().getSelectedItem().getId());
            refreshTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void close() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }
}
