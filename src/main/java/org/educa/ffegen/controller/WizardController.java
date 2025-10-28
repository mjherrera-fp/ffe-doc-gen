package org.educa.ffegen.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.educa.ffegen.ExcelReader;
import org.educa.ffegen.config.AppConfig;
import org.educa.ffegen.entity.ExcelData;
import org.educa.ffegen.entity.ExtraData;
import org.educa.ffegen.entity.RAData;
import org.educa.ffegen.entity.RowData;
import org.educa.ffegen.helper.FCTDataHelper;
import org.educa.ffegen.service.DocxService;
import org.educa.ffegen.service.PdfService;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;

public class WizardController {
    private static final String BTN_CANCELAR = "Cancelar";
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private List<ExcelData> excelData = new ArrayList<>();
    private List<RAData> excelRA = new ArrayList<>();
    private ExtraData extraData = new ExtraData();
    private Map<String, List<LocalDate>> holidayData = new HashMap<>();
    private List<LocalDate> tutoriaData = new ArrayList<>();
    private List<RowData> seleccionados = new ArrayList<>();
    private ObservableList<RowData> masterData;
    private FilteredList<RowData> filteredData;
    private File sel;
    private final TextField tfExcel = new TextField();
    private String searchText = "";
    private boolean filtroSoloConEmpresa = false;

    private final FCTDataHelper fctDataHelper = new FCTDataHelper();

    private final DocxService docxService = new DocxService();
    private final PdfService pdfService = new PdfService();

    private final AppConfig config = new AppConfig();

    public WizardController(Stage s) {
        stage = s;
        showPantalla1();
    }

    public Parent getView() {
        return root;
    }

    private void showPantalla1() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        tfExcel.setPrefWidth(500);
        tfExcel.setEditable(false);
        if (config.get(AppConfig.DATA_FILE) != null) {
            File f = new File(config.get(AppConfig.DATA_FILE));
            if (f.exists()) {
                tfExcel.setText(config.get(AppConfig.DATA_FILE));
            }
        }
        Button btnChoose = new Button("Seleccionar Excel...");
        Button btnImport = new Button("Importar");
        box.getChildren().addAll(new Label("Importar Excel:"), new HBox(8, tfExcel, btnChoose), btnImport);
        btnChoose.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));

            // Cargar ruta previa del YAML si existe
            if (config.get(AppConfig.DATA_FILE) != null) {
                File last = new File(config.get(AppConfig.DATA_FILE));
                if (last.exists()) {
                    fc.setInitialDirectory(last.isDirectory() ? last : last.getParentFile());
                }
            }

            // Si el campo ya tiene una ruta válida, usarla
            String currentPath = tfExcel.getText();
            if (currentPath != null && !currentPath.isBlank()) {
                File file = new File(currentPath);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        fc.setInitialDirectory(file);
                    } else {
                        // si es un archivo, usamos su directorio padre
                        fc.setInitialDirectory(file.getParentFile());
                    }
                }
            }
            sel = fc.showOpenDialog(stage);
            if (sel != null) {
                tfExcel.setText(sel.getAbsolutePath());
                config.set(AppConfig.DATA_FILE, sel.getAbsolutePath());
                config.save();
            }
        });
        btnImport.setOnAction(e -> {
            if (tfExcel.getText().isBlank()) {
                alert("Selecciona un Excel");
                return;
            }
            try {
                ExcelReader excelReader = new ExcelReader();
                excelData = excelReader.readDataFromExcel(tfExcel.getText());
                excelRA = excelReader.readRAFromExcel(tfExcel.getText());
                extraData = excelReader.readExtraDataFromExcel(tfExcel.getText());
                holidayData = excelReader.readHolidayDataFromExcel(tfExcel.getText());
                tutoriaData = excelReader.readTutoriaDataFromExcel(tfExcel.getText());

                if (excelData.isEmpty()) alert("Fichero SIN alumnos");
                else showPantalla2();
            } catch (Exception ex) {
                alert("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnImport.getStyleClass().add("accent");
        root.setCenter(box);
    }

    private void showPantalla2() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));

        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por nombre, NIF o empresa...");
        txtBuscar.setText(searchText); // restaurar búsqueda previa

        // Toggle para mostrar solo alumnos con empresa
        CheckBox chkConEmpresa = new CheckBox("Solo con empresa");
        chkConEmpresa.setSelected(filtroSoloConEmpresa); // restaurar estado anterior

        Label btnClear = new Label("✕");
        btnClear.setStyle("-fx-text-fill: #808080; -fx-font-size: 14; -fx-cursor: hand;");
        btnClear.setOnMouseClicked(e -> txtBuscar.clear());

        // Contenedor que superpone el botón
        StackPane searchBox = new StackPane(txtBuscar, btnClear);
        StackPane.setAlignment(btnClear, Pos.CENTER_RIGHT);
        StackPane.setMargin(btnClear, new Insets(0, 5, 0, 0));

        // Mostrar/ocultar según texto
        btnClear.visibleProperty().bind(txtBuscar.textProperty().isNotEmpty());

        TableView<RowData> table = new TableView<>();
        table.setEditable(true);

        // que la tabla crezca para ocupar el espacio disponible
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<RowData, Boolean> colSel = getRowDataBooleanTableColumn();

        TableColumn<RowData, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(c -> c.getValue().nombreProperty());

        TableColumn<RowData, String> colNif = new TableColumn<>("NIF");
        colNif.setCellValueFactory(c -> c.getValue().nifProperty());

        TableColumn<RowData, String> colEmp = new TableColumn<>("Empresa");
        colEmp.setCellValueFactory(c -> c.getValue().empresaProperty());

        table.getColumns().addAll(colSel, colNom, colNif, colEmp);

        // Crear masterData solo la primera vez
        if (masterData == null) {
            var rows = excelData.stream()
                    .map(data -> new RowData(data.getAlumnadoApellidosNombre(), data.getAlumnadoNif(), data.getEmpresa(), data))
                    .toList();
            masterData = FXCollections.observableArrayList(rows);
        }

        // Crear filteredData
        filteredData = new FilteredList<>(masterData, p -> true);
        table.setItems(filteredData);

        // Método auxiliar para aplicar filtros
        Runnable aplicarFiltro = () -> {
            searchText = txtBuscar.getText(); // guardar búsqueda
            filtroSoloConEmpresa = chkConEmpresa.isSelected(); // guardar toggle
            String filtro = searchText.toLowerCase().trim();
            boolean soloConEmpresa = filtroSoloConEmpresa;

            filteredData.setPredicate(r -> {
                if (soloConEmpresa && (r.empresaProperty() == null || r.empresaProperty().getValue().isBlank())) {
                    return false;
                }
                if (filtro.isEmpty()) {
                    return true;
                }
                return (r.nombreProperty() != null && r.nombreProperty().getValue().toLowerCase().contains(filtro)) ||
                        (r.nifProperty() != null && r.nifProperty().getValue().toLowerCase().contains(filtro)) ||
                        (r.empresaProperty() != null && r.empresaProperty().getValue().toLowerCase().contains(filtro));
            });
        };

        // Restaurar filtro guardado
        aplicarFiltro.run();

        // Listeners
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro.run());
        chkConEmpresa.selectedProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro.run());

        // --- RowFactory para click y resaltado ---
        table.setRowFactory(tv -> {
            TableRow<RowData> row = new TableRow<>();

            // Click en la fila para marcar/desmarcar checkbox
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    RowData rowData = row.getItem();
                    if (rowData.empresaProperty() == null || rowData.empresaProperty().getValue().isBlank()) {
                        return;
                    }
                    rowData.setSeleccionado(!rowData.isSeleccionado());
                }
            });

            // Resaltado al pasar el ratón
            row.hoverProperty().addListener((obs, wasHovered, isNowHovered) ->
                    row.setStyle(Boolean.TRUE.equals(isNowHovered) ? "-fx-background-color: lightblue;" : ""));
            return row;
        });

        Button btnSelAll = new Button("Seleccionar todo");
        btnSelAll.setOnAction(e -> table.getItems().forEach(r -> {
            if (r.empresaProperty() != null && !r.empresaProperty().getValue().isBlank()) {
                r.setSeleccionado(true);
            }
        }));

        Button btnDesel = new Button("Deseleccionar todo");
        btnDesel.setOnAction(e -> table.getItems().forEach(r -> {
            if (r.empresaProperty() != null && !r.empresaProperty().getValue().isBlank()) {
                r.setSeleccionado(false);
            }
        }));

        Button btnNext = new Button("Siguiente");
        btnNext.setOnAction(e -> {
            seleccionados = table.getItems().stream()
                    .filter(RowData::isSeleccionado)
                    .toList();
            if (seleccionados.isEmpty()) {
                alert("Selecciona alumnos");
                return;
            }
            showPantalla3();
        });
        btnNext.getStyleClass().add("accent");

        Button btnCancelar = new Button(BTN_CANCELAR);
        btnCancelar.setOnAction(e -> showPantalla1());
        btnCancelar.getStyleClass().addAll("button-outlined", "danger");

        // Layout superior: buscador + toggle
        HBox filtroBox = new HBox(10, searchBox, chkConEmpresa);
        filtroBox.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(
                new Label("Selecciona alumnos:"),
                filtroBox,
                table,
                new HBox(10, btnCancelar, btnDesel, btnSelAll, btnNext)
        );

        root.setCenter(box);
    }

    private void showPantalla3() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        Label resumen = new Label("Se van a generar documentos para " + seleccionados.size() + " alumnos.");

        CheckBox cbRelacion = new CheckBox("Relación de alumnos");
        CheckBox cbPlanFormativo = new CheckBox("Plan de formación");
        CheckBox cbFichaSeguimiento = new CheckBox("Ficha de seguimiento");
        CheckBox cbValoracionFinal = new CheckBox("Valoración final del tutor de la empresa");
        CheckBox cbCalendario = new CheckBox("Calendario FFE");
        CheckBox cbCarta = new CheckBox("Carta a la empresa");
        CheckBox cbWelcomePack = new CheckBox("Welcome Pack");

        VBox wpChildrenBox = new VBox(8, cbFichaSeguimiento, cbValoracionFinal, cbCalendario, cbCarta);
        wpChildrenBox.setPadding(new Insets(0, 0, 0, 24));

        List<CheckBox> checkBoxes = List.of(cbRelacion, cbPlanFormativo, cbWelcomePack);

        Button btnSelAll = new Button("Seleccionar todo");
        btnSelAll.setOnAction(e -> checkBoxes.forEach(cb -> cb.setSelected(true)));

        Button btnDesel = new Button("Deseleccionar todo");
        btnDesel.setOnAction(e -> checkBoxes.forEach(cb -> cb.setSelected(false)));

        cbWelcomePack.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean checked = newVal;
            List<CheckBox> hijos = List.of(cbFichaSeguimiento, cbValoracionFinal, cbCalendario, cbCarta);
            hijos.forEach(cb -> {
                cb.setSelected(checked);
                cb.setDisable(checked);
            });
        });

        Button btnGen = new Button("Generar documentos");
        btnGen.setOnAction(e -> {
            if (!cbRelacion.isSelected() && !cbPlanFormativo.isSelected()
                    && !cbFichaSeguimiento.isSelected() && !cbValoracionFinal.isSelected()
                    && !cbCalendario.isSelected() && !cbCarta.isSelected()) {
                alert("Selecciona al menos un tipo");
                return;
            }

            DirectoryChooser dc = new DirectoryChooser();
            if (config.get(AppConfig.OUTPUT_PATH) != null) {
                File last = new File(config.get(AppConfig.OUTPUT_PATH));
                if (last.exists() && last.isDirectory()) dc.setInitialDirectory(last);
            }

            File folder = dc.showDialog(stage);
            if (folder == null) {
                alert("Selecciona carpeta de destino");
                return;
            }

            config.set(AppConfig.OUTPUT_PATH, folder.getAbsolutePath());
            config.save();

            // --- Crear ventana de progreso ---
            Stage progressStage = new Stage();
            progressStage.initOwner(stage);
            progressStage.setTitle("Generando documentos...");

            VBox progressBox = new VBox(15);
            progressBox.setPadding(new Insets(20));
            progressBox.setAlignment(Pos.CENTER_LEFT);

            // Lista de tareas por documento
            Map<String, Task<Void>> tasks = new LinkedHashMap<>();

            if (cbRelacion.isSelected()) {
                tasks.put("Relación de alumnos", new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        docxService.generateRelacion(folder, fctDataHelper.prepareDataByCompany(seleccionados), extraData);
                        return null;
                    }
                });
            }
            if (cbPlanFormativo.isSelected()) {
                tasks.put("Plan formativo", new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        docxService.generatePlanFormativo(folder, seleccionados, excelRA, extraData);
                        return null;
                    }
                });
            }
            if (cbFichaSeguimiento.isSelected()) {
                tasks.put("Ficha seguimiento", new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        docxService.generateSeguimiento(folder, seleccionados, excelRA, extraData);
                        return null;
                    }
                });
            }
            if (cbValoracionFinal.isSelected()) {
                tasks.put("Valoración final", new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        docxService.generateValoracionFinal(folder, seleccionados, excelRA, extraData);
                        return null;
                    }
                });
            }
            if (cbCalendario.isSelected()) {
                tasks.put("Calendario FFE", new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        pdfService.generateCalendar(folder, seleccionados, extraData, holidayData, tutoriaData);
                        return null;
                    }
                });
            }
            if (cbCarta.isSelected()) {
                tasks.put("Carta a la empresa", new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        pdfService.generateCarta(folder, fctDataHelper.prepareDataByCompany(seleccionados), extraData, excelRA);
                        return null;
                    }
                });
            }

            // Crear UI antes de ejecutar las tareas
            for (var entry : tasks.entrySet()) {
                Label lbl = new Label(entry.getKey());
                ProgressIndicator indicator = new ProgressIndicator();
                indicator.setProgress(-1); // indeterminado, gira
                indicator.setPrefSize(40, 40);

                HBox row = new HBox(10, indicator, lbl);
                row.setAlignment(Pos.CENTER_LEFT);
                progressBox.getChildren().add(row);

                Task<Void> t = entry.getValue();

                t.setOnSucceeded(ev -> Platform.runLater(() -> {
                    indicator.setProgress(1);
                    indicator.setStyle("-fx-progress-color: green;");
                    lbl.setText(entry.getKey() + " ✔️");
                }));

                t.setOnFailed(ev -> Platform.runLater(() -> {
                    indicator.setStyle("-fx-progress-color: red;");
                    lbl.setText(entry.getKey() + " ❌ (" + t.getException().getMessage() + ")");
                    t.getException().printStackTrace();
                }));
            }

            // Mostrar la ventana ANTES de iniciar las tareas
            progressStage.setScene(new javafx.scene.Scene(progressBox));
            progressStage.show();

            // Ahora ejecutar las tareas en paralelo
            // Ejecutar tareas en paralelo usando try-with-resources
            new Thread(() -> {
                try (var executor = Executors.newFixedThreadPool(Math.min(tasks.size(), 4))) {
                    tasks.values().forEach(task -> executor.submit(() -> {
                        try {
                            task.run(); // ejecuta el Task en este hilo
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }));

                    // Esperar a que terminen todas
                    for (Task<Void> t : tasks.values()) {
                        t.get();
                    }

                    Platform.runLater(() -> {
                        alertInfo("Documentos generados en: " + folder.getAbsolutePath());
                        progressStage.close();
                        showPantalla1();
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> alert("Error: " + ex.getMessage()));
                    ex.printStackTrace();
                }
            }).start();

        });

        btnGen.getStyleClass().addAll("accent");
        Button btnCancelar = new Button(BTN_CANCELAR);
        btnCancelar.setOnAction(e -> showPantalla1());
        btnCancelar.getStyleClass().addAll("button-outlined", "danger");

        Button btnVolver = new Button("Volver");
        btnVolver.setOnAction(e -> showPantalla2());

        box.getChildren().addAll(
                btnVolver,
                resumen,
                cbRelacion,
                cbPlanFormativo,
                cbWelcomePack,
                wpChildrenBox,
                new HBox(10, btnCancelar, btnDesel, btnSelAll, btnGen)
        );
        root.setCenter(box);
    }


    private TableColumn<RowData, Boolean> getRowDataBooleanTableColumn() {
        TableColumn<RowData, Boolean> colSel = new TableColumn<>("Generar");
        colSel.setCellValueFactory(c -> c.getValue().seleccionadoProperty());

        // CellFactory personalizado
        colSel.setCellFactory(col -> new TableCell<RowData, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            private BooleanProperty boundProp = null;

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (boundProp != null) {
                    checkBox.selectedProperty().unbindBidirectional(boundProp);
                    boundProp = null;
                }

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    RowData row = getTableRow().getItem();
                    boolean tieneEmpresa = row.empresaProperty() != null
                            && !row.empresaProperty().getValue().isBlank();

                    checkBox.setDisable(!tieneEmpresa);
                    if (tieneEmpresa) {
                        boundProp = row.seleccionadoProperty();
                        checkBox.selectedProperty().bindBidirectional(boundProp);
                    } else {
                        checkBox.setSelected(false);
                    }

                    setGraphic(checkBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });
        return colSel;
    }

    private void alert(String m) {
        new Alert(Alert.AlertType.ERROR, m, ButtonType.OK).showAndWait();
    }

    private void alertInfo(String m) {
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }
}