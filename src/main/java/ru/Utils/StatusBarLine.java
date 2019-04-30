package ru.Utils;

import javafx.application.Platform;
import org.controlsfx.control.StatusBar;

import static java.lang.Thread.sleep;

public class StatusBarLine {
    private boolean isStatusBarHidden = true;
    private StatusBar statusBar;
    private Thread statusBarThread;

    public void setStatus(String text, StatusBar statusBar) {
        this.statusBar = statusBar;
        statusBar.setText(text);
        checkStatusBar();
    }

    private void checkStatusBar() {
        if (isStatusBarHidden) {
            startNewStatusBarThread();
        } else {
            statusBarThread.interrupt();
            startNewStatusBarThread();
        }
    }

    private void startNewStatusBarThread() {
        isStatusBarHidden = false;

        statusBarThread = new Thread(() -> {
            try {
                sleep(5000);
                clearStatus();
            } catch (InterruptedException ignored) {
            } finally {
                isStatusBarHidden = true;
            }
        });

        statusBarThread.start();
    }

    private void clearStatus() {
        Platform.runLater(() -> statusBar.setText(""));
    }
}
