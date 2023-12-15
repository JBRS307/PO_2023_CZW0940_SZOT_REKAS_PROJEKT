package agh.po.darwin.controller;

import agh.po.darwin.model.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class SimulationController implements MapChangeListener {
    public static final int CELL_SIZE = 25;
    public GridPane mapGrid;
    private Simulation simulation;

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        simulation.getWorldMap().registerSubscriber(this);
        lateInitialize();
    }

    /**
     * This method will be invoked after simulation object is assign to this simulation controller
     */
    protected void lateInitialize() {

        for (int i = 0; i < getSimulation().width; i++) {
            mapGrid.addColumn(i);
        }
        for (int i = 0; i < getSimulation().height; i++) {
            mapGrid.addRow(i);
        }
        drawMap();
    }

    private synchronized void drawMap() {
        Image dirt = new Image(Objects.requireNonNull(getClass().getResource("/bg.png")).toExternalForm(), 100, 100, false, false);
        Image grass = new Image(Objects.requireNonNull(getClass().getResource("/grass.png")).toExternalForm(), 100, 100, false, false);
        Image animal = new Image(Objects.requireNonNull(getClass().getResource("/animal.png")).toExternalForm(), 100, 100, false, false);

        for (int row = 0; row < getSimulation().height; row++) {
            for (int col = 0; col < getSimulation().width; col++) {
                ImageView imageView = new ImageView(dirt);
                WorldElement element = (getSimulation().getWorldMap().objectAt(new Vector2d(col, row)));

                if (element != null && element.getClass() == Grass.class) imageView = new ImageView(grass);
                if (element != null && element.getClass() == Animal.class) imageView = new ImageView(animal);

                imageView.setFitWidth(CELL_SIZE);
                imageView.setFitHeight(CELL_SIZE);
                GridPane.setRowIndex(imageView, row);
                GridPane.setColumnIndex(imageView, col);
                mapGrid.getChildren().add(imageView);

            }
        }
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    @Override
    public synchronized void mapChanged(WorldMap worldMap, String message) {
        if (worldMap != null) {
            drawMap();
            clearGrid();
        }
    }
}
