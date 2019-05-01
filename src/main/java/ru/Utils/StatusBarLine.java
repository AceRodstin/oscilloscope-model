package ru.Utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.controlsfx.control.StatusBar;

import static java.lang.Thread.sleep;

public class StatusBarLine {
    private Label checkIcon;
    private boolean isStatusOk;
    private StatusBar statusBar;
    private Thread statusBarThread;
    private Label warningIcon;

    public void setStatus(String text, StatusBar statusBar) {
        this.statusBar = statusBar;
        clearStatusBar();
        toggleIconsState("-fx-opacity: 0;");
        statusBar.setStyle("-fx-padding: 0 0 0 9.2;");
        statusBar.setText(text);
        handleStatusBar();
    }

    private void toggleIconsState(String state) {
        if (checkIcon != null && warningIcon != null) {
            checkIcon.setStyle(state);
            warningIcon.setStyle(state);
        }
    }

    private void handleStatusBar() {
        if (statusBarThread != null) {
            statusBarThread.interrupt();
        }
        startNewStatusBarThread();
    }

    private void startNewStatusBarThread() {
        statusBarThread = new Thread(() -> {
            try {
                sleep(5000);
                Platform.runLater(() -> clearStatusBar());
            } catch (InterruptedException ignored) {
            }
        });

        statusBarThread.start();
    }

    public void setStatus(String text, StatusBar statusBar, Label checkIcon, Label warningIcon) {
        this.checkIcon = checkIcon;
        this.statusBar = statusBar;
        this.warningIcon = warningIcon;
        clearStatusBar();
        initIcons();
        statusBar.setText(text);
        handleStatusBar();
    }

    private void initIcons() {
        checkIcon.setTextFill(Color.web("#009700"));
        warningIcon.setTextFill(Color.web("#D30303"));

        if (isStatusOk) {
            checkIcon.setStyle("-fx-opacity: 1;");
            statusBar.setStyle("-fx-padding: 0 0 0 9.2;");
            isStatusOk = false;
        } else {
            warningIcon.setStyle("-fx-opacity: 1;");
            statusBar.setStyle("-fx-padding: 0 0 0 9.2;");
        }
    }

    public void clearStatusBar() {
        toggleIconsState("-fx-opacity: 0;");
        statusBar.setText("");
    }

    public void setStatusOk(boolean statusOk) {
        isStatusOk = statusOk;
    }
}
