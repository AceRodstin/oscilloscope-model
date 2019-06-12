package ru.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import org.controlsfx.control.StatusBar;

import static java.lang.Thread.sleep;

public class StatusBarLine {
    private Label checkIcon;
    private ProgressIndicator progressIndicator;
    private StatusBar statusBar;
    private Thread statusBarThread;
    private Label warningIcon;

    public StatusBarLine(Label checkIcon, ProgressIndicator progressIndicator, StatusBar statusBar, Label warningIcon) {
        this.checkIcon = checkIcon;
        this.progressIndicator = progressIndicator;
        this.statusBar = statusBar;
        this.warningIcon = warningIcon;
    }

    public void setStatusOfProgress(String text) {
        clearStatusBar();
        toggleProgressIndicator(false);
        Platform.runLater(() -> {
            hideIcons();
            statusBar.setStyle("-fx-padding: 0 0 0 9.2;");
            statusBar.setText(text);
            handleStatusBar();
        });
    }

    private void hideIcons() {
        checkIcon.setStyle("-fx-opacity: 0;");
        warningIcon.setStyle("-fx-opacity: 0;");
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
                clearStatusBar();
            } catch (InterruptedException ignored) {
            }
        });

        statusBarThread.start();
    }

    public void setStatus(String text, boolean isStatusOk) {
        clearStatusBar();
        Platform.runLater(() -> {
            statusBar.setText(text);
            initIcons(isStatusOk);
            handleStatusBar();
        });
    }

    private void initIcons(boolean isStatusOk) {
        checkIcon.setTextFill(Color.web("#009700"));
        warningIcon.setTextFill(Color.web("#D30303"));

        if (isStatusOk) {
            checkIcon.setStyle("-fx-opacity: 1;");
            statusBar.setStyle("-fx-padding: 0 0 0 9.2;");
        } else {
            warningIcon.setStyle("-fx-opacity: 1;");
            statusBar.setStyle("-fx-padding: 0 0 0 9.2;");
        }
    }

    public void clearStatusBar() {
        Platform.runLater(() -> {
            hideIcons();
            statusBar.setText("");
        });
    }

    public void toggleProgressIndicator(boolean isHidden) {
        if (isHidden) {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 0;"));
        } else {
            Platform.runLater(() -> progressIndicator.setStyle("-fx-opacity: 1;"));
        }
    }
}
