package org.educa.ffegen;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.educa.ffegen.controller.WizardController;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        WizardController controller = new WizardController(stage);
        Parent root = controller.getView();

        Scene scene = new Scene(root, 800, 480);
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        stage.setTitle("FFE - Generador de documentos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
