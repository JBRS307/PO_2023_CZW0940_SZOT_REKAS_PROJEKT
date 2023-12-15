package agh.po.darwin.controller;

import agh.po.darwin.model.Simulation;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    public Button startBtn;

    public void initialize() {
        startBtn.setOnAction(event -> {
            SpawnNewSimulation();
        });
    }

    private void SpawnNewSimulation() {
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
                Integer.parseInt(genomeLength.getText())
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
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Create new Thread and start new simulation inside
        Thread simulationThread = new Thread(simulation::run);
        simulationThread.start();
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    simulationThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
