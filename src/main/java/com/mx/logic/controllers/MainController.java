package com.mx.logic.controllers;

import com.mx.application.MainApplication;
import com.mx.logic.client.Client;
import com.mx.logic.controllers.interfaces.Observer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;

public class MainController implements Observer {
    private Client client;
    private AlertController alertController;

    @FXML
    private AnchorPane menu, panel, mainPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button productsBtn, providersBtn, suppliesBtn, salesBtn, statsBtn, persAreaBtn, userWorkBtn, exitBtn;

    @FXML
    private Label labelLogin, labelRole;

    @FXML
    private Button menuBtn, panelExitBtn;

    @FXML
    private ImageView iconMenu;

    @FXML
    private void handleMenuButtonAction(ActionEvent event) throws IOException {
        Object object = event.getSource();
        alertController.removeObserver();

        if (object == productsBtn) {
            borderPane.setCenter(loadScene("products.fxml"));
        } else if (object == providersBtn) {
            borderPane.setCenter(loadScene("providers.fxml"));
        } else if (object == suppliesBtn) {
            borderPane.setCenter(loadScene("supplies.fxml"));
        } else if (object == salesBtn) {
            borderPane.setCenter(loadScene("sales.fxml"));
        } else if (object == statsBtn) {
            borderPane.setCenter(loadScene("stats.fxml"));
        } else if (object == persAreaBtn) {
            borderPane.setCenter(loadScene("personalArea.fxml"));
        } else if (object == exitBtn || object == panelExitBtn) {
            alertController.showAlert("Выйти из аккаунта?", "Выход");
            mainPane.setEffect(new BoxBlur());
        } else if (object == userWorkBtn) {
            borderPane.setCenter(loadScene("userWork.fxml"));
        }
    }

    @FXML
    private void initialize() throws IOException {
        client = Client.getInstance();
        alertController = new AlertController();
        alertController.registerObserver(this);

        borderPane.setCenter(loadScene("personalArea.fxml"));
        setLabelMenu();
        borderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> menuClose());
        menuBtn.setOnAction(e -> menuControl());
    }

    private Parent loadScene(String file) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mx/application/" + file));
        return fxmlLoader.load();
    }

    private void setLabelMenu() {
        String roleUser = client.getUser().getRole();
        labelLogin.setText(client.getUser().getName());
        if (roleUser.equals("Admin")) {
            labelRole.setText("Администратор");
        } else {
            providersBtn.setVisible(false);
            suppliesBtn.setVisible(false);
            statsBtn.setVisible(false);
            userWorkBtn.setVisible(false);
            labelRole.setText("Пользователь");
        }
    }

    private void menuControl() {

        if (menu.getLayoutX() == panel.getPrefWidth()) {
            borderPane.setEffect(null);
            menuAnimation(null, -menu.getPrefWidth());
        } else if (menu.getLayoutX() < -menu.getPrefWidth() + 10) {
            borderPane.setEffect(new BoxBlur());
            menuAnimation(new DropShadow(), panel.getPrefWidth());
        }
    }

    private void menuAnimation(Effect effectMenuBtn, double position) {
        iconMenu.setEffect(effectMenuBtn);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyValue kv = new KeyValue(menu.layoutXProperty(), position);
        EventHandler onFinished = (EventHandler<ActionEvent>) t -> timeline.stop();
        KeyFrame kf = new KeyFrame(Duration.millis(300), onFinished, kv);

        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    private void menuClose() {
        if (menu.getLayoutX() == panel.getPrefWidth()) {
            borderPane.setEffect(null);
            menuAnimation(null, -menu.getPrefWidth());
        }
    }

    @Override
    public void notification(String message) {
        if (message.equals("Выход")) {
            alertController.removeAllObserver();
            client.clearUser();
            try {
                MainApplication.stage.setScene(new Scene(loadScene("authorization.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mainPane.setEffect(null);
    }
}
