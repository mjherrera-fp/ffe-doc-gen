package org.educa.ffegen.controller;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.educa.ffegen.ExcelReader;
import org.educa.ffegen.entity.ExcelData;
import org.educa.ffegen.entity.ExtraData;
import org.educa.ffegen.entity.RAData;
import org.educa.ffegen.entity.RowData;
import org.educa.ffegen.service.DocxService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WizardController {
    private static final String BTN_CANCELAR = "Cancelar";
    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private List<ExcelData> excelData = new ArrayList<>();
    private List<RAData> excelRA = new ArrayList<>();
    private ExtraData extraData = new ExtraData();
    private List<RowData> seleccionados = new ArrayList<>();
    private ObservableList<RowData> masterData;
    private FilteredList<RowData> filteredData;
    private File sel;
    private final TextField tfExcel = new TextField();
    private String searchText = "";
    private boolean filtroSoloConEmpresa = false;

    private final DocxService docxService = new DocxService();

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
        Button btnChoose = new Button("Seleccionar Excel...");
        Button btnImport = new Button("Importar");
        box.getChildren().addAll(new Label("Importar Excel:"), new HBox(8, tfExcel, btnChoose), btnImport);
        btnChoose.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
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
            if (sel != null) tfExcel.setText(sel.getAbsolutePath());
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
        CheckBox cbFichaSeguimiento = new CheckBox("Ficha de seguimiento");
        CheckBox cbPlanFormativo = new CheckBox("Plan de formación");
        CheckBox cbValoracionFinal = new CheckBox("Valoración final del tutor de la empresa");

        List<CheckBox> checkBoxes = List.of(cbRelacion, cbFichaSeguimiento, cbPlanFormativo, cbValoracionFinal);

        Button btnSelAll = new Button("Seleccionar todo");
        btnSelAll.setOnAction(e -> checkBoxes.forEach(cb -> {
            cb.setSelected(true);
        }));

        Button btnDesel = new Button("Deseleccionar todo");
        btnDesel.setOnAction(e -> checkBoxes.forEach(cb -> {
            cb.setSelected(false);
        }));

        Button btnGen = new Button("Generar documentos");
        btnGen.setOnAction(e -> {
            if (!cbRelacion.isSelected() && !cbFichaSeguimiento.isSelected()
                    && !cbPlanFormativo.isSelected() && !cbValoracionFinal.isSelected()) {
                alert("Selecciona al menos un tipo");
                return;
            }
            DirectoryChooser dc = new DirectoryChooser();
            File folder = dc.showDialog(stage);
            if (folder == null) {
                alert("Selecciona carpeta de destino");
                return;
            }

            try {
                if (cbRelacion.isSelected()) {
                    docxService.generateRelacion(folder, seleccionados, extraData);
                }

                if (cbFichaSeguimiento.isSelected()) {
                    docxService.generateSeguimiento(folder, seleccionados, excelRA, extraData);
                }

                if (cbPlanFormativo.isSelected()) {
                    docxService.generatePlanFormativo(folder, seleccionados, excelRA, extraData);
                }

                if (cbValoracionFinal.isSelected()) {
                    docxService.generateValoracionFinal(folder, seleccionados, excelRA, extraData);
                }

                alertInfo("Generados en: " + folder.getAbsolutePath());
                showPantalla1();
            } catch (Exception ex) {
                alert("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnGen.getStyleClass().addAll("accent");

        Button btnCancelar = new Button(BTN_CANCELAR);
        btnCancelar.setOnAction(e -> showPantalla1());
        btnCancelar.getStyleClass().addAll("button-outlined", "danger");

        Button btnVolver = new Button("Volver");
        btnVolver.setOnAction(e -> showPantalla2());
        box.getChildren().addAll(btnVolver, resumen, cbRelacion, cbFichaSeguimiento, cbPlanFormativo, cbValoracionFinal,
                new HBox(10, btnCancelar, btnDesel, btnSelAll, btnGen));
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