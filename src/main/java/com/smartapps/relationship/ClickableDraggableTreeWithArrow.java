package com.smartapps.relationship;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ClickableDraggableTreeWithArrow extends Application {

    // Map to store nodes and their parent relationships
    private final Map<String, Pane> nodes = new HashMap<>();
    private final Pane rootPane = new Pane();

    @Override
    public void start(Stage primaryStage) {
        // Example: Adding nodes with specified parents
        addNode("Root", null, 300, 50);          // Root node
        addNode("Left Child 1", "Root", 150, 150);
        addNode("Left Child 2", "Left Child 1", 150, 250);
        addNode("Right Child 1", "Root", 450, 150);
        addNode("Right Child 2", "Right Child 1", 450, 250);
        addNode("Right Child 3", "Root", 600, 150);

        // Create and set the scene
        Scene scene = new Scene(rootPane, 800, 400);
        primaryStage.setTitle("Clickable Draggable Tree with Arrows");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Adds a node to the tree with a specified parent.
     *
     * @param label      The label of the node.
     * @param parentLabel The label of the parent node. Null for the root node.
     * @param x          The X-coordinate of the node.
     * @param y          The Y-coordinate of the node.
     */
    private void addNode(String label, String parentLabel, double x, double y) {
        Pane node = createNode(x, y, label);
        makeDraggable(node);
        makeClickable(node, label);

        rootPane.getChildren().add(node);

        // If the node has a parent, connect it
        if (parentLabel != null) {
            Pane parent = nodes.get(parentLabel);
            if (parent != null) {
                LineWithArrow connection = connectNodes(parent, node);
                rootPane.getChildren().add(connection.line);
                rootPane.getChildren().add(connection.arrow);
            } else {
                System.err.println("Parent node not found: " + parentLabel);
            }
        }

        // Store the node for later use
        nodes.put(label, node);
    }

    /**
     * Creates a visual node (rectangle with text).
     *
     * @param x     The X-coordinate of the node.
     * @param y     The Y-coordinate of the node.
     * @param label The label to display in the node.
     * @return A Pane containing the node's rectangle and label.
     */
    private Pane createNode(double x, double y, String label) {
        Rectangle rectangle = new Rectangle(100, 50);
        rectangle.setFill(Color.LIGHTBLUE);
        rectangle.setStroke(Color.BLACK);

        Text text = new Text(label);
        text.setX(15);  // Position text inside the rectangle
        text.setY(30);

        Pane pane = new Pane();
        pane.setLayoutX(x);
        pane.setLayoutY(y);
        pane.getChildren().addAll(rectangle, text);

        return pane;
    }

    /**
     * Connects two nodes with a line that dynamically moves when nodes are dragged.
     *
     * @param parent The parent node.
     * @param child  The child node.
     * @return A LineWithArrow containing the line and arrowhead.
     */

    public static void drawArrowLine(Pane parent, Pane child, Pane rootPane) {
        // Properties for the start (parent node)
        DoubleProperty startX = new SimpleDoubleProperty();
        DoubleProperty startY = new SimpleDoubleProperty();
        startX.bind(parent.layoutXProperty().add(parent.widthProperty().divide(2)));
        startY.bind(parent.layoutYProperty().add(parent.heightProperty()));

        // Properties for the end (child node)
        DoubleProperty endX = new SimpleDoubleProperty();
        DoubleProperty endY = new SimpleDoubleProperty();
        endX.bind(child.layoutXProperty().add(child.widthProperty().divide(2)));
        endY.bind(child.layoutYProperty());

        // Line connecting the nodes
        Line line = new Line();
        line.startXProperty().bind(startX);
        line.startYProperty().bind(startY);
        line.endXProperty().bind(endX);
        line.endYProperty().bind(endY);

        // Arrowhead lines
        Line arrow1 = new Line();
        Line arrow2 = new Line();

        // Bind arrowhead positions to the line's endpoint
        line.endXProperty().addListener((obs, oldValue, newValue) -> updateArrowHead(startX.get(), startY.get(), endX.get(), endY.get(), line, arrow1, arrow2));
        line.endYProperty().addListener((obs, oldValue, newValue) -> updateArrowHead(startX.get(), startY.get(), endX.get(), endY.get(), line, arrow1, arrow2));
        line.startXProperty().addListener((obs, oldValue, newValue) -> updateArrowHead(startX.get(), startY.get(), endX.get(), endY.get(), line, arrow1, arrow2));
        line.startYProperty().addListener((obs, oldValue, newValue) -> updateArrowHead(startX.get(), startY.get(), endX.get(), endY.get(), line, arrow1, arrow2));

        // Initial arrowhead positioning
        updateArrowHead(startX.get(), startY.get(), endX.get(), endY.get(), line, arrow1, arrow2);

        // Add the line and arrowheads to the root pane
        rootPane.getChildren().addAll(line, arrow1, arrow2);
    }

    private static void updateArrowHead(double startX, double startY, double endX, double endY, Line line, Line arrow1, Line arrow2) {
        double slope = (startY - endY) / (startX - endX);
        double lineAngle = Math.atan(slope);
        double arrowAngle = startX > endX ? Math.toRadians(45) : -Math.toRadians(225);

        double lineLength = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
        double arrowLength = lineLength / 10;

        // Update arrow1
        arrow1.setStartX(line.getEndX());
        arrow1.setStartY(line.getEndY());
        arrow1.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle - arrowAngle));
        arrow1.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle - arrowAngle));

        // Update arrow2
        arrow2.setStartX(line.getEndX());
        arrow2.setStartY(line.getEndY());
        arrow2.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle + arrowAngle));
        arrow2.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle + arrowAngle));
    }




    private LineWithArrow connectNodes(Pane parent, Pane child) {
        // Properties for the parent node
        DoubleProperty startX = new SimpleDoubleProperty();
        DoubleProperty startY = new SimpleDoubleProperty();
        startX.bind(parent.layoutXProperty().add(50)); // Center-bottom of the parent rectangle
        startY.bind(parent.layoutYProperty().add(50)); // Bottom of the parent rectangle

        // Properties for the child node
        DoubleProperty endX = new SimpleDoubleProperty();
        DoubleProperty endY = new SimpleDoubleProperty();
        endX.bind(child.layoutXProperty().add(50)); // Center-top of the child rectangle
        endY.bind(child.layoutYProperty());        // Top of the child rectangle

        // Create the line and bind its properties
        Line line = new Line();
        line.startXProperty().bind(startX);
        line.startYProperty().bind(startY);
        line.endXProperty().bind(endX);
        line.endYProperty().bind(endY);

        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);

        // Create the arrow (polygon)
        Polygon arrow = createArrow(line);

        return new LineWithArrow(line, arrow);
    }

    /**
     * Creates an arrowhead at the end of a line, correctly oriented.
     *
     * @param line The line to which the arrowhead should be attached.
     * @return The arrowhead (Polygon).
     */
    private Polygon createArrow(Line line) {
        Polygon arrow = new Polygon();
        arrow.setFill(Color.BLACK);

        // Set the points for the arrowhead (before rotating)
        arrow.getPoints().setAll(
                0.0, 0.0,  // Tip of the arrow
                -5.0, 10.0, // Left side
                5.0, 10.0   // Right side
        );

        // Calculate the angle of the line to rotate the arrow correctly
        double dx = line.getEndX() - line.getStartX();
        double dy = line.getEndY() - line.getStartY();
        double angle = Math.atan2(dy, dx);  // Angle in radians

        // Apply rotation to the arrow
        arrow.setRotate(Math.toDegrees(angle));  // Convert angle to degrees

        // Position the arrow at the end of the line
        arrow.layoutXProperty().bind(line.endXProperty());
        arrow.layoutYProperty().bind(line.endYProperty());

        return arrow;
    }

    /**
     * Makes a node draggable by adding mouse event handlers.
     *
     * @param node The node to make draggable.
     */
    private void makeDraggable(Pane node) {
        node.setOnMousePressed(event -> {
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()}); // Store the initial position
        });

        node.setOnMouseDragged(event -> {
            double[] startPosition = (double[]) node.getUserData();
            double deltaX = event.getSceneX() - startPosition[0];
            double deltaY = event.getSceneY() - startPosition[1];

            node.setLayoutX(node.getLayoutX() + deltaX);
            node.setLayoutY(node.getLayoutY() + deltaY);

            // Update initial position for the next drag event
            node.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });
    }

    /**
     * Makes a node clickable by adding a mouse click event handler.
     *
     * @param node  The node to make clickable.
     * @param label The label of the node.
     */
    private void makeClickable(Pane node, String label) {
        node.setOnMouseClicked(event -> {
            System.out.println("Node clicked: " + label);

            // Optional: Visual feedback for the clicked node
            Rectangle rectangle = (Rectangle) node.getChildren().get(0);
            rectangle.setFill(Color.YELLOW); // Highlight the rectangle
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Helper class to store both the line and the arrow for each connection
    private static class LineWithArrow {
        Line line;
        Polygon arrow;

        LineWithArrow(Line line, Polygon arrow) {
            this.line = line;
            this.arrow = arrow;
        }
    }
}
