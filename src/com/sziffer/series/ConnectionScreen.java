package com.sziffer.series;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.SQLException;


public class ConnectionScreen extends StackPane {

    private DatabaseManager databaseManager;

    private ObservableList<SeriesItem> seriesItems;
    private ListView<SeriesItem> listView;
    private TextArea selectedItemDescriptionTextArea;
    private Label selectedItemNameLabel;
    private DatePicker selectedItemDatePicker;

    private Button insertButton, deleteButton, updateButton;
    private TextField seriesNameTextField, descriptionTextField;
    private DatePicker nextEpisode;
    private SeriesItem selectedItem;

    ConnectionScreen(DatabaseManager databaseManager) {

        this.databaseManager = databaseManager;
        initUI();
    }

    void fetchDataToList(ObservableList<SeriesItem> result) {
        seriesItems = result;
        listView.setItems(seriesItems);
        listView.refresh();
    }

    void itemRemoved(SeriesItem seriesItem) {

        seriesItems.remove(seriesItem);
        listView.refresh();
    }

    private void initUI() {

        seriesItems = FXCollections.observableArrayList();
        listView = new ListView<SeriesItem>(seriesItems);
        listView.setMaxSize(200,200);
        listView.setPadding(new Insets(5));
        listView.setCellFactory(seriesItemListView -> new SeriesItemCell());
        listView.setOnMouseClicked(
                mouseEvent -> {
                    if (listView.getSelectionModel().getSelectedItem() == null)
                        return;
                    selectedItem = listView.getSelectionModel().getSelectedItem();
                    selectedItemNameLabel.setText(selectedItem.getName());
                    selectedItemDescriptionTextArea.setText(selectedItem.getDescription());
                    selectedItemDatePicker.setValue(selectedItem.getNextEpisode().toLocalDate());
                }
        );
        Label label = new Label("Series Database Connection");
        label.setFont(Font.font(25));
        label.setTextFill(Color.WHITE);
        BackgroundImage myBI= new BackgroundImage(new Image("com/sziffer/series/bg.jpg",
                800,600,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        setBackground(new Background(myBI));
        selectedItemNameLabel = new Label();
        selectedItemNameLabel.setFont(Font.font(18));
        selectedItemNameLabel.setTextFill(Color.WHITE);
        selectedItemNameLabel.setAlignment(Pos.CENTER);
        selectedItemDescriptionTextArea = new TextArea();
        selectedItemDescriptionTextArea.setPromptText("Selected series description");
        selectedItemDescriptionTextArea.setWrapText(true);
        selectedItemDatePicker = new DatePicker();
        selectedItemNameLabel.setMaxWidth(200);
        selectedItemDescriptionTextArea.setMaxWidth(200);
        selectedItemDescriptionTextArea.setMinHeight(120);
        insertButton = new Button("Insert series");
        insertButton.setMaxWidth(200);
        insertButton.setOnMouseClicked(
                event -> {
                    if (seriesNameTextField.getText() != null && descriptionTextField.getText() != null
                            && nextEpisode.getValue() != null) {
                        try {
                            databaseManager.insertSeries(seriesNameTextField.getText(),
                                    descriptionTextField.getText(), nextEpisode.getValue());
                            seriesNameTextField.clear();
                            descriptionTextField.clear();
                            nextEpisode.getEditor().clear();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
        );
        deleteButton = new Button("Remove selected series");
        deleteButton.setMaxWidth(200);
        deleteButton.setOnMouseClicked(
                event -> {
                    if (selectedItem != null) {
                        try {
                            databaseManager.removeSeries(selectedItem);
                            selectedItemNameLabel.setText("");
                            selectedItemDescriptionTextArea.setText("");
                            selectedItemDatePicker.getEditor().clear();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
        );
        updateButton = new Button("Update selected series");
        updateButton.setMaxWidth(200);
        updateButton.setOnMouseClicked(event -> {
            if (selectedItemDatePicker.getValue() != null && selectedItemDescriptionTextArea.getText() != null) {
                if (selectedItem == null)
                    return;
                try {
                    databaseManager.updateSeries(
                            selectedItemDescriptionTextArea.getText(),
                            selectedItemDatePicker.getValue(),
                            selectedItem.getId());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        seriesNameTextField = new TextField();
        seriesNameTextField.setPromptText("Name");
        seriesNameTextField.setMaxWidth(200);
        descriptionTextField = new TextField();
        descriptionTextField.setPromptText("Description");
        descriptionTextField.setMaxWidth(200);
        descriptionTextField.setMaxHeight(200);
        nextEpisode = new DatePicker();

        HBox hBox = new HBox(30);
        VBox insertVBox = new VBox(10);
        insertVBox.setAlignment(Pos.CENTER);
        VBox otherButtonsVBox = new VBox(10);
        VBox itemSelectionBox = new VBox(10);
        itemSelectionBox.setAlignment(Pos.CENTER);
        Label insertInfoLabel = new Label("Insert new item");
        insertInfoLabel.setFont(Font.font(17));
        insertInfoLabel.setTextFill(Color.WHITE);

        hBox.setAlignment(Pos.CENTER);
        insertVBox.setAlignment(Pos.CENTER);
        otherButtonsVBox.setAlignment(Pos.CENTER);
        insertVBox.getChildren().addAll(insertInfoLabel, seriesNameTextField,
                descriptionTextField, nextEpisode, insertButton);
        otherButtonsVBox.getChildren().addAll(deleteButton, updateButton);
        itemSelectionBox.getChildren().addAll(selectedItemNameLabel,
                selectedItemDescriptionTextArea, selectedItemDatePicker,
                listView);

        hBox.getChildren().addAll(otherButtonsVBox, itemSelectionBox, insertVBox);

        VBox view = new VBox(30);
        view.setAlignment(Pos.CENTER);

        view.getChildren().addAll(label, hBox);

        final MediaPlayer bgVideo = new MediaPlayer(
                new Media(getClass().getResource("bg.mp4").toString())
        );
        MediaView mediaView = new MediaView(bgVideo);
        bgVideo.setMute(true);
        bgVideo.setCycleCount(MediaPlayer.INDEFINITE);
        bgVideo.play();

        getChildren().addAll(mediaView,view);

    }

    private static class SeriesItemCell extends ListCell<SeriesItem> {
        @Override
        protected void updateItem(SeriesItem seriesItem, boolean b) {
            super.updateItem(seriesItem, b);
            if (seriesItem == null)
                setGraphic(null);
            else {
                Label label = new Label(seriesItem.getName());
                setGraphic(label);
            }
        }
    }
}
