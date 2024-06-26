package agh.po.darwin.controller;

import agh.po.darwin.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Comparator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimulationController implements MapChangeListener {
    public static final int CELL_SIZE = 25;
    public GridPane mapGrid;
    public Label mapVariant;
    public Label genomeVariant;
    public Button play;
    public Button pause;
    public Slider speed;
    public LineChart<Long, Integer> statisticsChart;
    public BarChart genomeBarChart;
    public Label tracked_energy;
    public Label tracked_genome;
    public Label tracked_active_genome;
    public Label tracked_amount_descendant;
    public Label tracked_amount_children;
    public Label tracked_grass_eaten;
    public Label tracked_lifespan;
    public Label tracked_direction;
    public Label trackedMoveCount;
    public Label simDay;
    public Label animalCount;
    public Label grassCount;
    public Label avg_lifespan;
    public Label avg_energy;
    public Label avg_children_count;
    private XYChart.Series<Long, Integer> animalSeries;
    private XYChart.Series<Long, Integer> grassSeries;
    private XYChart.Series<String, Number> genomeSeries;
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
        if(!simulation.hell) {
            mapVariant.setText(mapVariant.getText() + "MAPA DOMYSLNA, ");
        } else {
            mapVariant.setText(mapVariant.getText() + "PORTAL DO PIEKLA, ");
        }

        if(!simulation.leftRight) {
            genomeVariant.setText(genomeVariant.getText() + "Standardowy");
        } else {
            genomeVariant.setText(genomeVariant.getText() + "Lewo-Prawo");
        }
        drawMap();
        speed.valueProperty().addListener(
                new ChangeListener<Number>() {

                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {

                        simulation.setSpeed(newValue.floatValue());
                    }
                });
        pause.setOnAction(event -> {
            drawBarChartOfGenomes();
            simulation.setPause(true);
            simulation.setGrassPreferredTiles();
            simulation.setTopGenomes();
            drawWithHighlights();
        });
        play.setOnAction(event -> {
            simulation.setPause(false);
        });
        animalSeries = new XYChart.Series<>();
        animalSeries.setName("animals");

        grassSeries = new XYChart.Series<>();
        grassSeries.setName("grass");

        genomeSeries = new XYChart.Series<>();
        genomeSeries.setName("Genome");

        statisticsChart.setCreateSymbols(false);
        statisticsChart.getData().add(animalSeries);
        statisticsChart.getData().add(grassSeries);

        genomeBarChart.getData().add(genomeSeries);

        formatSeries();
    }

    private void formatSeries() {
        animalSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #ec0095;");
        grassSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: green;");
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
        Image fatAnimal = new Image(Objects.requireNonNull(getClass().getResource("/fat_animal.png")).toExternalForm(), 100, 100, false, false);
        Image animalSelected = new Image(Objects.requireNonNull(getClass().getResource("/animalSelected.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimalSelected = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalSelected.png")).toExternalForm(), 100, 100, false, false);



        for (int row = 0; row < getSimulation().height; row++) {
            for (int col = 0; col < getSimulation().width; col++) {
                ImageView imageView = new ImageView(dirt);
                MapTile element = (getSimulation().getWorldMap().at(new Vector2d(col, row)));


                if (element.toString().equals("grass")) imageView = new ImageView(grass);
                if (element.toString().equals("animal")) {
                    Animal currAnimal;
                    if (element.getAnimals().contains(simulation.getTrackedAnimal())) {
                        currAnimal = simulation.getTrackedAnimal();
                    } else {
                        currAnimal = element.getAnimals().peek();
                    }

                    if (currAnimal == null) continue;

                    if (currAnimal.getEnergy() >= simulation.fedEnergy) {
                        imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(fatAnimalSelected) : new ImageView(fatAnimal);
                    } else {
                        imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(animalSelected) : new ImageView(animal);
                    }
                }

                imageView.setOnMouseClicked(event -> {
                    element.onClick(simulation);
                });

                imageView.setFitWidth(750 / simulation.width);
                imageView.setFitHeight(500 / simulation.height);
                GridPane.setRowIndex(imageView, row);
                GridPane.setColumnIndex(imageView, col);
                mapGrid.getChildren().add(imageView);

            }
        }
    }

    private synchronized void drawWithHighlights() {
        for (int i = 0; i < getSimulation().width; i++) {
            mapGrid.addColumn(i);
        }
        for (int i = 0; i < getSimulation().height; i++) {
            mapGrid.addRow(i);
        }

        Image dirt = new Image(Objects.requireNonNull(getClass().getResource("/bg.png")).toExternalForm(), 100, 100, false, false);
        Image grass = new Image(Objects.requireNonNull(getClass().getResource("/grass.png")).toExternalForm(), 100, 100, false, false);
        Image animal = new Image(Objects.requireNonNull(getClass().getResource("/animal.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimal = new Image(Objects.requireNonNull(getClass().getResource("/fat_animal.png")).toExternalForm(), 100, 100, false, false);
        Image highlightedDirt = new Image(Objects.requireNonNull(getClass().getResource("/BgHighlight.png")).toExternalForm(), 100, 100, false, false);
        Image highlightedGrass = new Image(Objects.requireNonNull(getClass().getResource("/grassHighlight.png")).toExternalForm(), 100, 100, false, false);
        Image highlightedAnimal = new Image(Objects.requireNonNull(getClass().getResource("/animalHighlight.png")).toExternalForm(), 100, 100, false, false);
        Image highlightedFatAnimal = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalHighlight.png")).toExternalForm(), 100, 100, false, false);
        Image animalSelected = new Image(Objects.requireNonNull(getClass().getResource("/animalSelected.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimalSelected = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalSelected.png")).toExternalForm(), 100, 100, false, false);
        Image highlightedAnimalSelected = new Image(Objects.requireNonNull(getClass().getResource("/animalHighlightSelected.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimalHighlightSelected = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalHighlightSelected.png")).toExternalForm(), 100, 100, false, false);
        Image animalDominant = new Image(Objects.requireNonNull(getClass().getResource("/animalDominant.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimalDominant = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalDominant.png")).toExternalForm(), 100, 100, false, false);
        Image animalSelectedDominant = new Image(Objects.requireNonNull(getClass().getResource("/animalSelectedDominant.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimalSelectedDominant = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalSelectedDominant.png")).toExternalForm(), 100, 100, false, false);
        Image animalHighlightedDominant = new Image(Objects.requireNonNull(getClass().getResource("/animalHighlightDominant.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimalHighlightedDominant = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalHighlightDominant.png")).toExternalForm(), 100, 100, false, false);
        Image animalHighlightedSelectedDominant = new Image(Objects.requireNonNull(getClass().getResource("/animalHighlightSelectedDominant.png")).toExternalForm(), 100, 100, false, false);
        Image fatAnimalHighlightedSelectedDominant = new Image(Objects.requireNonNull(getClass().getResource("/fat_animalHighlightSelectedDominant.png")).toExternalForm(), 100, 100, false, false);

        for (int row = 0; row < getSimulation().height; row++) {
            for (int col = 0; col < getSimulation().width; col++) {
                ImageView imageView = new ImageView(dirt);
                MapTile element = (getSimulation().getWorldMap().at(new Vector2d(col, row)));

                switch (element.toString()) {
                    case "dirt":
                        imageView = element.getGrassPreferred() ? new ImageView(highlightedDirt) : new ImageView(dirt);
                        break;
                    case "grass":
                        imageView = element.getGrassPreferred() ? new ImageView(highlightedGrass) : new ImageView(grass);
                        break;
                    case "animal":
                        Animal currAnimal;
                        if (element.getAnimals().contains(simulation.getTrackedAnimal())) {
                            currAnimal = simulation.getTrackedAnimal();
                        } else {
                            currAnimal = element.getAnimals().peek();
                        }

                        if (currAnimal == null) continue;

                        if (currAnimal.getEnergy() >= simulation.fedEnergy) {
                            if (element.getGrassPreferred()) {
                                if (currAnimal.getGenome().getDominantGenome()){
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(fatAnimalHighlightedSelectedDominant) : new ImageView(fatAnimalHighlightedDominant);
                                } else {
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(fatAnimalHighlightSelected) : new ImageView(highlightedFatAnimal);
                                }
                            } else {
                                if (currAnimal.getGenome().getDominantGenome()) {
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(fatAnimalSelectedDominant) : new ImageView(fatAnimalDominant);
                                } else {
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(fatAnimalSelected) : new ImageView(fatAnimal);
                                }
                            }
                        } else {
                            if (element.getGrassPreferred()) {
                                if (currAnimal.getGenome().getDominantGenome()) {
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(animalHighlightedSelectedDominant) : new ImageView(animalHighlightedDominant);
                                } else {
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(highlightedAnimalSelected) : new ImageView(highlightedAnimal);
                                }
                            } else {
                                if (currAnimal.getGenome().getDominantGenome()) {
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(animalSelectedDominant) : new ImageView(animalDominant);
                                } else {
                                    imageView = currAnimal.equals(simulation.getTrackedAnimal()) ? new ImageView(animalSelected) : new ImageView(animal);
                                }
                            }
                        }
                        break;
                }

                imageView.setOnMouseClicked(event -> {
                    element.onClick(simulation);
                });

                imageView.setFitWidth(750 / simulation.width);
                imageView.setFitHeight(500 / simulation.height);
                GridPane.setRowIndex(imageView, row);
                GridPane.setColumnIndex(imageView, col);
                mapGrid.getChildren().add(imageView);

            }
        }
    }

    private void clearGrid() {
        mapGrid.getChildren().clear();
    }

    private void drawBarChartOfGenomes() {
        var genomes = simulation.getGenomeCount();
        int MAX_CHART_CATEGORIES_COUNT = 10;

        Map<String, Integer> genomesToDisplay = genomes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(MAX_CHART_CATEGORIES_COUNT)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (e1, e2) -> e1, LinkedHashMap::new));


        genomeSeries.getData().clear();
        genomesToDisplay.forEach((key, value) -> {
            genomeSeries.getData().add(new XYChart.Data<>(key, value));
        });

    }
    private void updateTrackedAnimalStatistics(){
        var animal = simulation.getTrackedAnimal();
        if(animal == null) return;
        tracked_energy.setText(String.valueOf(animal.getEnergy()));
        tracked_genome.setText(String.valueOf(animal.getGenome().getCode()));
        tracked_amount_children.setText(String.valueOf(animal.getAmountOfChildren()));
        tracked_amount_descendant.setText(String.valueOf(animal.getAmountOfDecadence()));
        tracked_lifespan.setText(String.valueOf(animal.getLifeTime()));
        tracked_active_genome.setText(animal.getActiveGen());
        trackedMoveCount.setText(String.valueOf(animal.getMoveCount()));
        tracked_grass_eaten.setText(String.valueOf(animal.getGrassEaten()));
        tracked_direction.setText(animal.getDirection().toString());
    }

    @Override
    public synchronized void mapChanged(WorldMap worldMap, String message) {
        if (worldMap != null) {
            clearGrid();
            if (simulation.getPause()) {
                drawWithHighlights();
            } else {
                drawMap();
            }
            updateTrackedAnimalStatistics();
            updateSimulationStatistics();
            animalSeries.getData().add(new XYChart.Data<>(simulation.getDay(), simulation.getAnimalsCount()));
            grassSeries.getData().add(new XYChart.Data<>(simulation.getDay(), simulation.getGrassCount()));
        }
    }

    private void updateSimulationStatistics() {
        simDay.setText(String.valueOf(simulation.getDay()));
        animalCount.setText(String.valueOf(simulation.getAnimalsCount()));
        grassCount.setText(String.valueOf(simulation.getGrassCount()));
        avg_lifespan.setText(String.valueOf(Math.round(100*simulation.getAverageLifeSpan())/100.0));
        avg_energy.setText(String.valueOf(Math.round(100*simulation.getAverageEnergy())/100.0));
        avg_children_count.setText(String.valueOf(Math.round(100*simulation.getAverageCountOfChildren())/100.0));
    }
}
