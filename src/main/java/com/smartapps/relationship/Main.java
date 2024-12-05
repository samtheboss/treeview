package com.smartapps.relationship;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {
    @Override/*from  w  ww. j av  a2s.  c  o  m*/
    public void start(Stage primaryStage) {

        double startX = 10;
        double startY = 10;
        double endX = 200;
        double endY = 200;
        Pane arrowPane = new Pane();
        drawArrowLine(startX, startY, endX, endY, arrowPane);

        StackPane pane = new StackPane();
        pane.setPadding(new Insets(20));
        pane.getChildren().add(arrowPane);

        Scene scene = new Scene(pane);
        primaryStage.setTitle("java2s.com");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Pane drawArrowLine(double startX, double startY, double endX, double endY, Pane pane) {
        // get the slope of the line and find its angle
        double slope = (startY - endY) / (startX - endX);
        double lineAngle = Math.atan(slope);

        double arrowAngle = startX > endX ? Math.toRadians(45) : -Math.toRadians(225);

        Line line = new Line(startX, startY, endX, endY);

        double lineLength = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
        double arrowLength = lineLength / 10;

        // create the arrow legs
        Line arrow1 = new Line();
        arrow1.setStartX(line.getEndX());
        arrow1.setStartY(line.getEndY());
        arrow1.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle - arrowAngle));
        arrow1.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle - arrowAngle));

        Line arrow2 = new Line();
        arrow2.setStartX(line.getEndX());
        arrow2.setStartY(line.getEndY());
        arrow2.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle + arrowAngle));
        arrow2.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle + arrowAngle));

        pane.getChildren().addAll(line, arrow1, arrow2);
        return pane;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}