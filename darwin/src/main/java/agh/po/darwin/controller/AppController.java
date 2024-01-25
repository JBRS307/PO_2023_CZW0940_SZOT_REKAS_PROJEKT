package agh.po.darwin.controller;

import agh.po.darwin.model.Simulation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AppController {
    public ComboBox<String> pickConfig;
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
    public ComboBox<String> pickGenome;
    public Button startBtn;
    public Button saveBtn;
    public Button clearConfigBtn;
    public CheckBox exportCheckBox;
    public Label errMsg;

    private JSONArray configList;
    private JSONObject currConfig = new JSONObject();

    public void initialize() {
        readConfigList();
        setConfigurationComboBox();
        applyConfig();
        pickMap.getSelectionModel().selectFirst();
        pickGenome.getSelectionModel().selectFirst();
        startBtn.setOnAction(event -> {
            SpawnNewSimulation();
        });
        saveBtn.setOnAction(event -> {
            saveConfiguration();
        });
        clearConfigBtn.setOnAction(event -> {
            clearConfigFile();
            readConfigList();
            setConfigurationComboBox();
        });
    }

    private boolean inputValidation() {
        String width = this.width.getText();
        String height = this.height.getText();
        String startingGrassAmount = this.startingGrassAmount.getText();
        String grassEatingEnergy = this.grassEatingEnergy.getText();
        String grassGrowthPerDay = this.grassGrowthPerDay.getText();
        String animalsStartAmount = this.animalsStartAmount.getText();
        String animalsStartEnergy = this.animalsStartEnergy.getText();
        String fedEnergy = this.fedEnergy.getText();
        String breedCostEnergy = this.breedEnergyCost.getText();
        String minMutations = this.minMutations.getText();
        String maxMutations = this.maxMutations.getText();
        String genomeLength = this.genomeLength.getText();

        if (width.isEmpty() || height.isEmpty() || startingGrassAmount.isEmpty() ||
            grassEatingEnergy.isEmpty() || grassGrowthPerDay.isEmpty() || animalsStartAmount.isEmpty() ||
            animalsStartEnergy.isEmpty() || fedEnergy.isEmpty() || breedCostEnergy.isEmpty() || minMutations.isEmpty() ||
            maxMutations.isEmpty() || genomeLength.isEmpty()) {
            errMsg.setText("Żadne pole nie może być puste");
            errMsg.setVisible(true);
            return false;
        }

        try {
            Integer.parseInt(this.width.getText());
            Integer.parseInt(this.height.getText());
            Integer.parseInt(this.startingGrassAmount.getText());
            Integer.parseInt(this.grassEatingEnergy.getText());
            Integer.parseInt(this.grassGrowthPerDay.getText());
            Integer.parseInt(this.animalsStartAmount.getText());
            Integer.parseInt(this.animalsStartEnergy.getText());
            Integer.parseInt(this.fedEnergy.getText());
            Integer.parseInt(this.breedEnergyCost.getText());
            Integer.parseInt(this.minMutations.getText());
            Integer.parseInt(this.maxMutations.getText());
            Integer.parseInt(this.genomeLength.getText());

        } catch (NumberFormatException ignored){
            errMsg.setText("Wartości muszą być liczbami całkowitymi");
            errMsg.setVisible(true);
            return false;
        }
        return true;

    }

    private boolean dataValidation() {
        int width = Integer.parseInt(this.width.getText());
        int height = Integer.parseInt(this.height.getText());
        int startingGrassAmount = Integer.parseInt(this.startingGrassAmount.getText());
        int grassEatingEnergy = Integer.parseInt(this.grassEatingEnergy.getText());
        int grassGrowthPerDay = Integer.parseInt(this.grassGrowthPerDay.getText());
        int animalsStartAmount = Integer.parseInt(this.animalsStartAmount.getText());
        int animalsStartEnergy = Integer.parseInt(this.animalsStartEnergy.getText());
        int fedEnergy = Integer.parseInt(this.fedEnergy.getText());
        int breedEnergyCost = Integer.parseInt(this.breedEnergyCost.getText());
        int minMutations = Integer.parseInt(this.minMutations.getText());
        int maxMutations = Integer.parseInt(this.maxMutations.getText());
        int genomeLength = Integer.parseInt(this.genomeLength.getText());

        if (width <= 0 || height <= 0 || fedEnergy <= 0 || genomeLength <= 0) {
            errMsg.setText("Wysokość, szerokość, energia potrzebna do roznmożenia oraz długość genomu muszą być większe od 0");
            errMsg.setVisible(true);
            return false;
        }

        if (startingGrassAmount < 0 || grassGrowthPerDay < 0 || animalsStartAmount < 0 ||
            animalsStartEnergy < 0 || minMutations < 0 || maxMutations < 0) {
            errMsg.setText("Tylko energia za zjedzenie trawy oraz koszt rozmnożenia mogą być mniejsze niż 0");
            errMsg.setVisible(true);
            return false;
        }

        if (animalsStartAmount + startingGrassAmount > width*height) {
            errMsg.setText("Sumaryczna startowa ilość trawy i zwierząt musi być mniejsza niż ilość pól");
            errMsg.setVisible(true);
            return false;
        }

        if (grassGrowthPerDay > width*height) {
            errMsg.setText("Dzienny przyrost trawy nie może być większy niż liczba pól");
            errMsg.setVisible(true);
            return false;
        }

        if (minMutations > maxMutations) {
            errMsg.setText("Minimalna liczba mutacji nie może być większa niż maksymalna liczba mutacji");
            errMsg.setVisible(true);
            return false;
        }

        if (width*height > 2000) {
            errMsg.setText("Maksymalna ilość pól to 3000");
            errMsg.setVisible(true);
            return false;
        }

        if (width > 700) {
            errMsg.setText("Maksymalna szerokość to 700");
            errMsg.setVisible(true);
            return false;
        }

        if (height > 500) {
            errMsg.setText("Maksymalna wysokość to 500");
            errMsg.setVisible(true);
            return false;
        }

        errMsg.setVisible(false);
        return true;
    }

    private void SpawnNewSimulation() {
        if (!inputValidation()) return;
        if (!dataValidation()) return;
        boolean hell = switch (pickMap.getValue()) {
            case "Mapa domyslna" -> false;
            case "Portal do piekla" -> true;
            default -> throw new IllegalStateException("Unexpected value: " + pickMap.getValue()); // średnio to potrzebne, bo da się wybrać tylko te wartości, ale na mnie krzyczy
        };                                                                                         // więc niech ma
        boolean leftRight = switch (pickGenome.getValue()) {
            case "Standardowy" -> false;
            case "Lewo-Prawo" -> true;
            default -> throw new IllegalStateException("Unexpected value: " + pickGenome.getValue());
        };
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
                hell,
                leftRight,
                exportCheckBox.isSelected()
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

    private ObservableList<String> getConfigNames() {
        JSONObject jo;
        ObservableList<String> names = FXCollections.observableArrayList();
        for (int i = 0; i < configList.length(); i++) {
            jo = (JSONObject) configList.get(i);
            String joName = jo.getString("name");
            String name = "";
            if (Integer.parseInt(jo.getString("counter")) != 0) {
                name = joName + "/" + jo.getString("counter");
            } else {
                name = joName;
            }
            names.add(name);
        }
        return names;
    }

    public void setConfigurationComboBox() {
        var names = getConfigNames();
        pickConfig.setItems(names);
    }

    public void readConfigList() {
        File fp = new File("./config.json");
        String fileContent = "";
        StringBuilder contentBuilder = new StringBuilder();
        try {
            if (!fp.createNewFile()) {
                Scanner fpScanner = new Scanner(fp);
                while (fpScanner.hasNextLine()) {
                    contentBuilder.append(fpScanner.nextLine());
                }
                fileContent = contentBuilder.toString();
                fpScanner.close();
            }
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
        if (fileContent.isEmpty()) fileContent = "[]";
        configList = new JSONArray(fileContent);
    }

    private JSONObject getConfigByName(String name) {
        int counter = 0;
        if (name.contains("/")) {
            counter = Integer.parseInt(name.substring(name.indexOf('/')+1));
            name = name.substring(0, name.indexOf('/'));
        }
        for (int i = 0; i < configList.length(); i++) {
            JSONObject jo = configList.getJSONObject(i);
            if (name.equals(jo.getString("name")) &&
                counter == Integer.parseInt(jo.getString("counter"))) {
                return jo;
            }
        }
        throw new RuntimeException("No such config");
    }

    private void setValuesFromConfig() {
        width.setText(currConfig.getString("width"));
        height.setText(currConfig.getString("height"));
        startingGrassAmount.setText(currConfig.getString("startingGrassAmount"));
        grassEatingEnergy.setText(currConfig.getString("grassEatingEnergy"));
        grassGrowthPerDay.setText(currConfig.getString("grassGrowthPerDay"));
        animalsStartAmount.setText(currConfig.getString("animalsStartAmount"));
        animalsStartEnergy.setText(currConfig.getString("animalsStartEnergy"));
        fedEnergy.setText(currConfig.getString("fedEnergy"));
        breedEnergyCost.setText(currConfig.getString("breedEnergyCost"));
        minMutations.setText(currConfig.getString("minMutations"));
        maxMutations.setText(currConfig.getString("maxMutations"));
        genomeLength.setText(currConfig.getString("genomeLength"));
        pickMap.getSelectionModel().select(currConfig.getString("pickMap"));
        pickGenome.getSelectionModel().select(currConfig.getString("pickGenome"));
    }

    private void applyConfig() {
        pickConfig.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    currConfig = getConfigByName(newValue);
                    setValuesFromConfig();
                }
            }
        });
    }

    private JSONObject currConfigToJSON() {
        JSONObject jo = new JSONObject(currConfig.toString());
        jo.clear();
        jo.put("width", width.getText());
        jo.put("height", height.getText());
        jo.put("startingGrassAmount", startingGrassAmount.getText());
        jo.put("grassEatingEnergy", grassEatingEnergy.getText());
        jo.put("grassGrowthPerDay", grassGrowthPerDay.getText());
        jo.put("animalsStartAmount", animalsStartAmount.getText());
        jo.put("animalsStartEnergy", animalsStartEnergy.getText());
        jo.put("fedEnergy", fedEnergy.getText());
        jo.put("breedEnergyCost", breedEnergyCost.getText());
        jo.put("minMutations", minMutations.getText());
        jo.put("maxMutations", maxMutations.getText());
        jo.put("genomeLength", genomeLength.getText());
        jo.put("pickMap", pickMap.getValue());
        jo.put("pickGenome", pickGenome.getValue());
        return jo;
    }

    private void saveConfiguration() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("save.fxml"));
        Stage saveStage = new Stage();
        try {
            Parent root = loader.load();
            saveStage.setTitle("Zapisz konfigurację");
            saveStage.setScene(new Scene(root));
            saveStage.setResizable(false);
            saveStage.show();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }

        SaveController saveController = loader.getController();
        saveController.setCurrConfig(currConfigToJSON());
        saveController.setConfigList(configList);
        saveController.setApp(this);
        saveController.initialize();

    }

    private void clearConfigFile() {
        try {
            File fp = new File("./config.json");
            if(!fp.exists()) return;

            FileWriter fpWriter = new FileWriter(fp);
            fpWriter.write("[]");
            fpWriter.close();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
}
