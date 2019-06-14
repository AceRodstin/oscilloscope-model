package ru;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.controllers.MainController;
import ru.utils.ControllerManager;

import java.io.IOException;

public class Main extends Application implements ControllerManager {
    private boolean finished;
    private Scene mainScene;

    @Override
    public void init() throws IOException {
        createMainScene();
    }

    private void createMainScene() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/layouts/mainView.fxml"));
        Parent parent = loader.load();
        mainScene = new Scene(parent);

        MainController mainController = loader.getController();
        mainController.setControllerManager(this);
    }

    public void start(Stage primaryStage) {
        Stage mainStage = primaryStage;
        mainStage.setScene(mainScene);
        mainStage.show();
    }

    public void stop() {
        finished = true;
    }


    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
