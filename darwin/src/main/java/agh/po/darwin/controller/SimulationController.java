package agh.po.darwin.controller;

import agh.po.darwin.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class SimulationController implements MapChangeListener {
    public static final int CELL_SIZE = 25;
    public GridPane mapGrid;
    public Button play;
    public Button pause;
    public Slider speed;
    public LineChart<Long, Integer> animalsCount;
    private XYChart.Series<Long, Integer> series;
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
        drawMap();
        speed.valueProperty().addListener(
                new ChangeListener<Number>() {

                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {

                        simulation.setSpeed(newValue.floatValue());
                    }
                });
        pause.setOnAction(event -> {
            simulation.setPause(true);
        });
        play.setOnAction(event -> {
            simulation.setPause(false);
        });
        series = new XYChart.Series<>();
        series.setName("Data Series");
        animalsCount.setLegendVisible(false);
        animalsCount.setCreateSymbols(false);
        animalsCount.getData().add(series);
    }

    private synchronized void drawMap() {
        for (int i = 0; i < getSimulation().width; i++) {
            mapGrid.addColumn(i);
        }
        for (int i = 0; i < getSimulation().height; i++) {
            mapGrid.addRow(i);
        }

        Image dirt = new Image(Objects.requireNonNull(getClass().getResource("/bg.png")).toExternalForm(), 100, 100, false, false);
        Image grass = new Image(Objects.requireNonNull(getClass().getResource("/grass.png")).toExternalForm(), 100, 100, false, false);
        Image animal = new Image(Objects.requireNonNull(getClass().getResource("/animal.png")).toExternalForm(), 100, 100, false, false);

        for (int row = 0; row < getSimulation().height; row++) {
            for (int col = 0; col < getSimulation().width; col++) {
                ImageView imageView = new ImageView(dirt);
                MapTile element = (getSimulation().getWorldMap().at(new Vector2d(col, row)));

                if (element.toString().equals("grass")) imageView = new ImageView(grass);
                if (element.toString().equals("animal")) imageView = new ImageView(animal);

                imageView.setFitWidth(750/simulation.width);
                imageView.setFitHeight(500/simulation.height);
                GridPane.setRowIndex(imageView, row);
                GridPane.setColumnIndex(imageView, col);
                mapGrid.getChildren().add(imageView);

            }
        }
    }

    private void clearGrid() {
        mapGrid.getChildren().clear();
    }

    @Override
    public synchronized void mapChanged(WorldMap worldMap, String message) {
        if (worldMap != null) {
            clearGrid();
            drawMap();
            series.getData().add(new XYChart.Data<>(simulation.getDay(), simulation.animalsCount));
        }
    }
}
