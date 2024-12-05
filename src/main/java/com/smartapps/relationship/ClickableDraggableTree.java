package com.smartapps.relationship;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickableDraggableTree extends Application {
    private final Map<String, Pane> nodes = new HashMap<>();
    private final Pane rootPane = new Pane();
    boolean highlight = false;

    @Override
    public void start(Stage primaryStage) {
        Button reset = new Button();

        reset.setOnAction(e -> {
            resetHighlightAfterDelay();
            highlight = true;

        });
        rootPane.getChildren().add(reset);
        addNode("Root", null, 300, 50);          // Root node
        addNode("Left Child 1", "Root", 300, 150);
        addNode("Left Child 2", "Left Child 1", 150, 250);
        addNode("Right Child 1", "Root", 450, 150);
        addNode("Right Child 2", "Right Child 1", 450, 250);
        addNode("Right Child 3", "Root", 600, 150);
        // Create and set the scene
        Scene scene = new Scene(rootPane, 800, 400);
        primaryStage.setTitle("Clickable Draggable Tree");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private final Map<String, List<String>> parentToChildrenMap = new HashMap<>();

    private void addNode(String label, String parentLabel, double x, double y) {
        Pane node = createNode(label, x, y);
        makeDraggable(node);
        makeClickable(node, label);
        rootPane.getChildren().add(node);
        if (parentLabel != null) {
            Pane parent = nodes.get(parentLabel);
            if (parent != null) {
                Pane connection = connectNodes(parent, node);
                rootPane.getChildren().add(connection);
                parentToChildrenMap.computeIfAbsent(parentLabel, k -> new ArrayList<>()).add(label);
            } else {
                System.err.println("Parent node not found: " + parentLabel);
            }
        }
        nodes.put(label, node);
    }


    public static Pane connectNodes(Pane parent, Pane child) {
        // Line properties
        DoubleProperty startX = new SimpleDoubleProperty();
        DoubleProperty startY = new SimpleDoubleProperty();
        DoubleProperty endX = new SimpleDoubleProperty();
        DoubleProperty endY = new SimpleDoubleProperty();
        startX.bind(parent.layoutXProperty().add(parent.widthProperty()));
        startY.bind(parent.layoutYProperty().add(parent.heightProperty().divide(2)));
        endX.bind(child.layoutXProperty());
        endY.bind(child.layoutYProperty().add(child.heightProperty().divide(2)));
        Line line = new Line();
        line.startXProperty().bind(startX);
        line.startYProperty().bind(startY);
        line.endXProperty().bind(endX);
        line.endYProperty().bind(endY);
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);
        double arrowLength = 10;
        Line arrow1 = new Line();
        Line arrow2 = new Line();
        arrow1.startXProperty().bind(endX);
        arrow1.startYProperty().bind(endY);
        arrow2.startXProperty().bind(endX);
        arrow2.startYProperty().bind(endY);
        arrow1.endXProperty().bind(Bindings.createDoubleBinding(() ->
                        endX.get() + arrowLength * Math.cos(Math.atan2(startY.get() - endY.get(), startX.get() - endX.get()) + Math.toRadians(45)),
                startX, startY, endX, endY
        ));
        arrow1.endYProperty().bind(Bindings.createDoubleBinding(() ->
                        endY.get() + arrowLength * Math.sin(Math.atan2(startY.get() - endY.get(), startX.get() - endX.get()) + Math.toRadians(45)),
                startX, startY, endX, endY
        ));
        arrow2.endXProperty().bind(Bindings.createDoubleBinding(() ->
                        endX.get() + arrowLength * Math.cos(Math.atan2(startY.get() - endY.get(), startX.get() - endX.get()) - Math.toRadians(45)),
                startX, startY, endX, endY
        ));
        arrow2.endYProperty().bind(Bindings.createDoubleBinding(() ->
                        endY.get() + arrowLength * Math.sin(Math.atan2(startY.get() - endY.get(), startX.get() - endX.get()) - Math.toRadians(45)),
                startX, startY, endX, endY
        ));
        Pane connectionPane = new Pane();
        connectionPane.getChildren().addAll(line, arrow1, arrow2);
        connectionPane.setOnMouseClicked(event -> {
            System.out.println("Connection clicked!");
        });
        connectionPane.setMouseTransparent(true);
        return connectionPane;
    }

    private VBox createNode(String label, double x, double y) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("node.fxml"));
            VBox node = loader.load();
            HelloController controller = loader.getController();
            controller.setLabel(label);
            node.setLayoutX(x);
            node.setLayoutY(y);
            return node;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load node.fxml", e);
        }
    }

    private void makeDraggable(Pane node) {
        node.setOnMousePressed(event -> {
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });
        node.setOnMouseDragged(event -> {
            double[] startPosition = (double[]) node.getUserData();
            if (startPosition != null) {
                double deltaX = event.getSceneX() - startPosition[0];
                double deltaY = event.getSceneY() - startPosition[1];
                node.setLayoutX(node.getLayoutX() + deltaX);
                node.setLayoutY(node.getLayoutY() + deltaY);
                node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
            }
        });
        node.setOnMouseReleased(event -> {

        });
    }
    private void makeClickable(Pane node, String label) {
        node.setOnMouseClicked(event -> {
            highlightNode(node);
            resetHighlightAfterDelay();
            List<String> children = parentToChildrenMap.get(label);
            if (children != null) {
                for (String childLabel : children) {
                    Pane childNode = nodes.get(childLabel);
                    if (childNode != null) {
                        highlightNode(childNode);
                    }
                }
            }
        });
    }

    private void highlightNode(Pane node) {
        node.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 10px;");
    }

    private void resetHighlightAfterDelay() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2)); // Use factory method
        pause.setOnFinished(e -> {
            for (Pane node : nodes.values()) {
                node.setStyle("-fx-border-color: #2196f3; -fx-border-width: 2px; -fx-border-radius: 10px;");
            }
        });
        pause.play();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
