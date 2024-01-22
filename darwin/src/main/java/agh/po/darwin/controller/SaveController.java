package agh.po.darwin.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveController {
    private AppController app;
    public Button saveBtn;
    public Button cancelBtn;
    public TextField configName;
    public Label forbiddenChar;
    private JSONObject currConfig;
    private JSONArray configList;

    public void initialize() {
        cancelBtn.setOnAction(event -> {
            handleCloseButtonAction();
        });
        saveBtn.setOnAction(event -> {
            if(handleSave())
                handleCloseButtonAction();
        });
    }


    public void setConfigList(JSONArray ja) {
        configList = ja;
    }
    public void setCurrConfig(JSONObject jo) {
        currConfig = jo;
    }

    public void setApp(AppController app) {
        this.app = app;
    }
    private void handleCloseButtonAction() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private int getNameCounter(String name) {
        int length = configList.length();
        int maxCounter = -1;
        for (int i = 0; i < length; i++) {
            JSONObject jo = configList.getJSONObject(i);
            if (jo.getString("name").equals(name)) {
                int counter = Integer.parseInt(jo.getString("counter"));
                maxCounter = Math.max(counter, maxCounter);
            }
        }
        return maxCounter+1;
    }
    private boolean handleSave() {
        String name = configName.getText();
        if (name.contains("/")) {
            forbiddenChar.setVisible(true);
            return false;
        }
        int counter = getNameCounter(name);
        currConfig.put("name", name);
        currConfig.put("counter", counter+"");

        configList.put(currConfig);

        try {
            File fp = new File("./src/main/resources/config.json");
            if(!fp.exists()) fp.createNewFile();

            FileWriter fpWriter = new FileWriter(fp);
            BufferedWriter bpWriter = new BufferedWriter(fpWriter);
            bpWriter.write(configList.toString());
            bpWriter.close();
            fpWriter.close();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
        app.readConfigList();
        app.setConfigurationComboBox();
        if (counter == 0)
            app.pickConfig.getSelectionModel().select(name);
        else
            app.pickConfig.getSelectionModel().select(name + "/" + counter);
        return true;
//        System.out.println(configList);
    }
}
