package ru.Utils;

import javafx.application.Platform;
import org.controlsfx.control.StatusBar;

import static java.lang.Thread.sleep;

public class StatusBarLine {
    private boolean isStatusBarHidden = true;
    private Thread statusBarThread;

    public void setStatus(String text, StatusBar statusBar) {
        statusBar.setText(text);
        if (isStatusBarHidden) {
            startNewStatusBarThread(statusBar);
        } else {
            statusBarThread.interrupt();
            startNewStatusBarThread(statusBar);
        }
    }

    private void startNewStatusBarThread(StatusBar statusBar) {
        statusBarThread = new Thread(() -> {
            isStatusBarHidden = false;
            try {
                sleep(5000);
                Platform.runLater(() -> statusBar.setText(""));
            } catch (InterruptedException ignored) {
            } finally {
                isStatusBarHidden = true;
            }
        });
        statusBarThread.start();
    }
}
