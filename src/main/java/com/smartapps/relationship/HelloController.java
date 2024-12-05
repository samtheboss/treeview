package com.smartapps.relationship;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class HelloController implements Initializable {
    @FXML
    private Button btnItem;

    @FXML
    private Button btnItem211;

    @FXML
    private Button btnItem1;

    @FXML
    private Button btnItem2;

    @FXML
    private Button btnItem212;

    public void setLabel(String label) {
        //welcomeText.setText(label);
    }

    void makeButton(List<Button> btns) {
        for (Button btn : btns)
            btn.setMouseTransparent(true);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Button> buttons = List.of(btnItem1,btnItem2,btnItem,btnItem211);
        makeButton(buttons);
    }
}