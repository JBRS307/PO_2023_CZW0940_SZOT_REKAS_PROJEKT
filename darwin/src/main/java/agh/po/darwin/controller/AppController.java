package agh.po.darwin.controller;

import agh.po.darwin.model.Simulation;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.IOException;

public class AppController {
    public TextField width;
    public TextField height;
    public TextField startingGrassAmount;
    public TextField grassEatingEnergy;
    public TextField grassGrowthPerDay;
    public TextField animalsStartAmount;
    public TextField animalsStartEnergy;
    public TextField fedEnergy;
    public TextField breedEnergyCost;
    public TextField minMutations;
    public TextField maxMutations;
    public TextField genomeLength;
    public ComboBox<String> pickMap;
    public Button startBtn;

    public void initialize() {
        pickMap.getSelectionModel().selectFirst();
        System.out.println(pickMap.getItems());
        startBtn.setOnAction(event -> {
            SpawnNewSimulation();
        });
    }

    private void SpawnNewSimulation() {
        boolean hell = switch (pickMap.getValue()) {
            case "Mapa domyślna" -> false;
            case "Portal do piekła" -> true;
            default -> throw new IllegalStateException("Unexpected value: " + pickMap.getValue()); // średnio to potrzebne, bo da się wybrać tylko te wartości, ale na mnie krzyczy
        };                                                                                         // więc niech ma
        Simulation simulation = new Simulation(
                Integer.parseInt(width.getText()),
                Integer.parseInt(height.getText()),
                Integer.parseInt(startingGrassAmount.getText()),
                Integer.parseInt(grassEatingEnergy.getText()),
                Integer.parseInt(grassGrowthPerDay.getText()),
                Integer.parseInt(animalsStartAmount.getText()),
                Integer.parseInt(animalsStartEnergy.getText()),
                Integer.parseInt(fedEnergy.getText()),
                Integer.parseInt(breedEnergyCost.getText()),
                Integer.parseInt(minMutations.getText()),
                Integer.parseInt(maxMutations.getText()),
                Integer.parseInt(genomeLength.getText()),
                hell
        );
        System.out.println("Started new simulation with UUID:" + simulation.uuid);
        //OPEN simulation window
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));

        Stage newStage = new Stage();
        try {
            Parent root = loader.load();
            newStage.setTitle("Simulation " + simulation.uuid);
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SimulationController simulationController = loader.getController();
        simulationController.setSimulation(simulation);

        //Create new Thread and start new simulation inside
        Thread simulationThread = new Thread(simulation::run);
        simulationThread.start();
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    simulation.stop();
                    simulationThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
