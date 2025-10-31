package com.petstore.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.api.StoreApi;
import com.petstore.db.DatabaseHelper;
import com.petstore.models.PetInfo;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class PetstoreApp extends Application {

    private final StoreApi api = new StoreApi();
    private final DatabaseHelper db = new DatabaseHelper();
    private final ObservableList<PetInfo> petData = FXCollections.observableArrayList();

    private TableView<PetInfo> tableView;
    private Label loadingLabel;

    @Override
    public void start(Stage stage) {
        Label title = new Label("Swagger Petstore Client");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        ComboBox<String> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList("available", "sold", "pending")
        );

        statusCombo.setPrefWidth(150);

        Button findByStatusBtn = new Button("Найти по статусу");
        findByStatusBtn.setDefaultButton(true);

        Button findByIdBtn = new Button("Найти по ID");
        Button addPetBtn = new Button("Добавить питомца");
        Button deletePetBtn = new Button("Удалить по ID");

        HBox topBox = new HBox(10, statusCombo, findByStatusBtn);
        topBox.setAlignment(Pos.CENTER);

        HBox actionBox = new HBox(10, findByIdBtn, addPetBtn, deletePetBtn);
        actionBox.setAlignment(Pos.CENTER);

        tableView = new TableView<>();
        tableView.setItems(petData);
        tableView.setPrefHeight(350);

        TableColumn<PetInfo, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getId()).asObject());
        idCol.setMinWidth(80);

        TableColumn<PetInfo, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        nameCol.setMinWidth(160);

        TableColumn<PetInfo, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));
        statusCol.setMinWidth(120);

        TableColumn<PetInfo, String> categoryCol = new TableColumn<>("Категория");
        categoryCol.setCellValueFactory(cell -> {
            String catName = (cell.getValue().getCategory() != null)
                    ? cell.getValue().getCategory().getName()
                    : null;
            return new SimpleStringProperty(catName);
        });
        categoryCol.setMinWidth(130);

        tableView.getColumns().addAll(idCol, nameCol, statusCol, categoryCol);

        loadingLabel = new Label("Идёт загрузка...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        loadingLabel.setVisible(false);
        loadingLabel.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, topBox, actionBox, tableView, loadingLabel);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 720, 540);
        stage.setScene(scene);
        stage.setTitle("Petstore Swagger Client");
        stage.show();

        new Thread(() -> {
            List<PetInfo> savedPets = db.loadAllPets();
            Platform.runLater(() -> {
                if (!savedPets.isEmpty()) {
                    petData.setAll(savedPets);
                }
            });
        }).start();

        findByStatusBtn.setOnAction(e -> handleFindByStatus(statusCombo.getValue()));
        findByIdBtn.setOnAction(e -> handleFindById());
        addPetBtn.setOnAction(e -> handleAddPet());
        deletePetBtn.setOnAction(e -> handleDeletePet());
    }

    // === Найти по статусу ===
    private void handleFindByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ошибка", "Выберите статус.");
            return;
        }

        Platform.runLater(() -> {
            petData.clear();
            startLoading();
        });

        new Thread(() -> {
            try {
                Response response = api.getFindByStatus(status);
                Platform.runLater(() -> {
                    stopLoading();
                    if (response.statusCode() != 200) {
                        showAlert(Alert.AlertType.ERROR, "Ошибка API", "Код: " + response.statusCode());
                        return;
                    }
                    try {
                        List<PetInfo> pets = response.as(new TypeRef<List<PetInfo>>() {});
                        petData.setAll(pets);
                        db.savePets(pets);
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обработать ответ: " + ex.getMessage());
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    stopLoading();
                    showAlert(Alert.AlertType.ERROR, "Сеть", "Не удалось подключиться к API.");
                });
            }
        }).start();
    }

    // === Найти по ID ===
    private void handleFindById() {
        // Запрос данных — ТОЛЬКО в UI-потоке
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Поиск по ID");
        dialog.setHeaderText("Введите ID питомца:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get().trim();
            try {
                long id = Long.parseLong(input);

                Platform.runLater(this::startLoading);

                new Thread(() -> {
                    try {
                        Response response = api.getPetById(id);

                        Platform.runLater(() -> {
                            stopLoading();
                            if (response.statusCode() == 200) {
                                try {
                                    PetInfo pet = response.as(PetInfo.class);
                                    String details = String.format(
                                            "ID: %d\nИмя: %s\nСтатус: %s\nКатегория: %s",
                                            pet.getId(),
                                            pet.getName(),
                                            pet.getStatus(),
                                            (pet.getCategory() != null ? pet.getCategory().getName() : "No category")
                                    );
                                    showAlert(Alert.AlertType.INFORMATION, "Питомец найден", details);
                                } catch (Exception ex) {
                                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось распарсить ответ.");
                                }
                            } else {
                                showAlert(Alert.AlertType.WARNING, "Не найден", "Питомец с ID=" + id + " не найден.");
                            }
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            stopLoading();
                            showAlert(Alert.AlertType.ERROR, "Ошибка", "Нет подключения к API.");
                        });
                    }
                }).start();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Ошибка", "ID должен быть числом!");
            }
        }
    }

    // === Добавить питомца ===
    private void handleAddPet() {
        Dialog<PetInfo> dialog = new Dialog<>();
        dialog.setTitle("Добавить питомца");
        dialog.setHeaderText("Заполните данные питомца:");

        ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField idField = new TextField("0");
        idField.setPromptText("ID (можно 0)");
        TextField nameField = new TextField();
        nameField.setPromptText("Имя питомца");
        ComboBox<String> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList("available", "pending", "sold")
        );
        statusCombo.setValue("available");
        TextField categoryField = new TextField("Dogs");
        categoryField.setPromptText("Категория (например, Dogs)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Статус:"), 0, 2);
        grid.add(statusCombo, 1, 2);
        grid.add(new Label("Категория:"), 0, 3);
        grid.add(categoryField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    long id = idField.getText().trim().isEmpty() ? 0 : Long.parseLong(idField.getText().trim());
                    String name = nameField.getText().trim();
                    String status = statusCombo.getValue();
                    String categoryName = categoryField.getText().trim();

                    if (name.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Ошибка", "Имя не может быть пустым.");
                        return null;
                    }
                    if (status == null) status = "available";
                    if (categoryName.isEmpty()) categoryName = "Dogs";

                    PetInfo pet = new PetInfo();
                    pet.setId(id);
                    pet.setName(name);
                    pet.setStatus(status);

                    PetInfo.Category cat = new PetInfo.Category();
                    cat.setId(0L);
                    cat.setName(categoryName);
                    pet.setCategory(cat);

                    // photoUrls и tags оставим пустыми — Jackson обработает
                    return pet;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.WARNING, "Ошибка", "ID должен быть числом.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(pet -> {
            try {
                ObjectMapper mapper = new ObjectMapper();

                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                String jsonBody = mapper.writeValueAsString(pet);

                startLoading();
                new Thread(() -> {
                    try {
                        Response response = api.postPet(jsonBody);
                        Platform.runLater(() -> {
                            stopLoading();
                            if (response.statusCode() == 200) {
                                try {
                                    PetInfo created = response.as(PetInfo.class);
                                    db.savePet(created);
                                    Platform.runLater(() -> {
                                        showAlert(Alert.AlertType.INFORMATION, "Успех", "Питомец добавлен!");
                                        handleFindByStatus("available");
                                    });
                                } catch (Exception ex) {
                                    Platform.runLater(() -> {
                                        showAlert(Alert.AlertType.INFORMATION, "Успех", "Питомец добавлен (без деталей).");
                                        handleFindByStatus("available");
                                    });
                                }
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Ошибка",
                                        "Код: " + response.statusCode() + "\n" +
                                                (response.contentType() != null && response.contentType().contains("json")
                                                        ? response.asString()
                                                        : "Сервер вернул ошибку")
                                );
                            }
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            stopLoading();
                            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось отправить запрос: " + ex.getMessage());
                        });
                    }
                }).start();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось создать JSON: " + e.getMessage());
            }
        });
    }

    // === Удалить по ID ===
    private void handleDeletePet() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление питомца");
        dialog.setHeaderText("Введите ID питомца для удаления:");
        dialog.setContentText("ID:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                long id = Long.parseLong(input.trim());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Подтверждение");
                confirm.setHeaderText("Удалить питомца?");
                confirm.setContentText("Вы уверены, что хотите удалить питомца с ID = " + id + "?");
                confirm.showAndWait().ifPresent(btnType -> {
                    if (btnType == ButtonType.OK) {
                        startLoading();
                        new Thread(() -> {
                            try {
                                Response response = api.deletePetById(id);
                                Platform.runLater(() -> {
                                    stopLoading();
                                    if (response.statusCode() == 200 || response.statusCode() == 404) {
                                        showAlert(Alert.AlertType.INFORMATION, "Успех", "Питомец удалён (или не существовал).");
                                        handleFindByStatus("available"); // обновим список
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Ошибка", "Код: " + response.statusCode());
                                    }
                                });
                            } catch (Exception ex) {
                                Platform.runLater(() -> {
                                    stopLoading();
                                    showAlert(Alert.AlertType.ERROR, "Сеть", "Ошибка при удалении.");
                                });
                            }
                        }).start();
                    }
                });
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Ошибка", "ID должен быть целым числом!");
            }
        });
    }

    private void startLoading() {
        loadingLabel.setVisible(true);
        tableView.setPlaceholder(loadingLabel);
    }

    private void stopLoading() {
        loadingLabel.setVisible(false);
        tableView.setPlaceholder(new Label("Нет данных"));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}