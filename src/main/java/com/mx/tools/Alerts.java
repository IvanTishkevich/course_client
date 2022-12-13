package com.mx.tools;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Alerts {
    private static AnchorPane alert;
    private static Timeline timeline;
    private static Text text;

    public static void alertShow(String textAlert) {
        timelineStop();
        text.setText(textAlert);
        alert.setOpacity(1);
        alert.setVisible(true);

        timeline = new Timeline();
        timeline.setCycleCount(10);
        KeyValue kv = new KeyValue(alert.opacityProperty(), 0.05);
        EventHandler onFinished = (EventHandler<ActionEvent>) e -> {
            timeline.stop();
            alert.setVisible(false);
        };
        KeyFrame kf = new KeyFrame(Duration.millis(6000), onFinished, kv);

        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public static void timelineStop() {
        if (timeline != null) {
            timeline.stop();
            alert.setVisible(false);
        }
    }

    public static void setAlert(AnchorPane _alert,Text _text){
        alert = _alert;
        text = _text;
    }

    public static void showWarning(Label warning) {
        warning.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e ->  warning.setVisible(false));
        pause.play();
    }
}
