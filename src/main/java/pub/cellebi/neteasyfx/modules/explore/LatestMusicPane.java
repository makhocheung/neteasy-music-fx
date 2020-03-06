package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.Line;
import pub.cellebi.neteasyfx.utils.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LatestMusicPane extends BorderPane {

    private final Label title;
    private final GridPane contentPane;
    private final ListView<Map<String, Object>> leftListView;
    private final ListView<Map<String, Object>> rightListView;

    private final ObservableList<Map<String, Object>> leftDataList;
    private final ObservableList<Map<String, Object>> rightDataList;

    public LatestMusicPane() {
        title = new Label("最新音乐");
        contentPane = new GridPane();
        leftListView = new ListView<>();
        rightListView = new ListView<>();
        leftDataList = FXCollections.observableArrayList();
        rightDataList = FXCollections.observableArrayList();

        render();
        initState();
        registerListener();
    }

    private void render() {
        var svgPath = new SVGPath();
        svgPath.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 14.5c-2.49 0-4.5-2.01-4.5-4.5S9.51 7.5 12 7.5s4.5 2.01 4.5 4.5-2.01 4.5-4.5 4.5zm0-5.5c-.55 0-1 .45-1 1s.45 1 1 1 1-.45 1-1-.45-1-1-1z");
        svgPath.getStyleClass().add("label-icon");
        title.setGraphicTextGap(5);
        title.setGraphic(svgPath);
        var vBox = new VBox();
        vBox.getChildren().addAll(title, Line.HLine());
        vBox.setSpacing(10);
        BorderPane.setMargin(vBox, new Insets(0, 0, 10, 0));
        contentPane.addColumn(0, leftListView);
        contentPane.addColumn(1, Line.VLine());
        contentPane.addColumn(2, rightListView);
        GridPane.setHgrow(leftListView, Priority.ALWAYS);
        GridPane.setHgrow(rightListView, Priority.ALWAYS);
        var callback = (Callback<ListView<Map<String, Object>>, ListCell<Map<String, Object>>>) l -> new LatestMusicCell();
        leftListView.setCellFactory(callback);
        rightListView.setCellFactory(callback);
        leftListView.setItems(leftDataList);
        rightListView.setItems(rightDataList);
        leftListView.setPrefHeight(401.7);
        rightListView.setPrefHeight(401.7);

        setTop(vBox);
        setCenter(contentPane);
        setMaxWidth(1080);
        getStyleClass().add("latest-music-pane");
    }

    private void registerListener() {
        leftListView.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            if (n != null) {
                rightListView.getSelectionModel().clearSelection();
            }
        });
        leftListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var n = leftListView.getSelectionModel().getSelectedItem();
                var id = (int) n.get("id");
                Jux.PLAYER_STATE.songImageUrl.set((String) n.get("picUrl"));
                Jux.PLAYER_STATE.songName.set((String) n.get("name"));
                Jux.PLAYER_STATE.artists.set((String) n.get("artists"));
                process(id);
            }
        });
        rightListView.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            if (n != null) {
                leftListView.getSelectionModel().clearSelection();
            }
        });
        rightListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var n = rightListView.getSelectionModel().getSelectedItem();
                var id = (int) n.get("id");
                Jux.PLAYER_STATE.songImageUrl.set((String) n.get("picUrl"));
                Jux.PLAYER_STATE.songName.set((String) n.get("name"));
                Jux.PLAYER_STATE.artists.set((String) n.get("artists"));
                process(id);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initState() {
        NetService.get("http://cellebi.pub:3000/personalized/newsong", json -> {
            var map = Util.MAPPER.readValue(json, Map.class);
            var list = ((List<Map<String, Object>>) map.get("result")).stream()
                    .peek(m -> {
                        var song = (Map<String, Object>) m.get("song");
                        var artists = ((List<Map<String, Object>>) song.get("artists"))
                                .stream().map(a -> (String) a.get("name"))
                                .collect(Collectors.joining("/"));
                        m.put("artists", artists);
                    }).limit(10).collect(Collectors.toList());
            Platform.runLater(() -> initContent(list));
        });
    }

    private void initContent(List<Map<String, Object>> items) {
        var itemsMap = items.stream().collect(Collectors.partitioningBy(i -> items.indexOf(i) % 2 == 0));
        var oddList = itemsMap.get(false);
        var evenList = itemsMap.get(true);
        leftDataList.addAll(oddList);
        rightDataList.addAll(evenList);
    }

    @SuppressWarnings("unchecked")
    private void process(int id) {
        NetService.get("http://cellebi.pub:3000/song/url?id=" + id, json -> {
            var map = Util.MAPPER.readValue(json, Map.class);
            var url = (String) ((List<Map<String, ?>>) map.get("data")).get(0).get("url");
            Platform.runLater(() -> {
                var media = new Media(url);
                var mediaPlayer = new MediaPlayer(media);
                Jux.PLAYER_STATE.updatePlayer(mediaPlayer);
                Jux.PLAYER_STATE.isPlaying.set(true);
            });
        });
    }
}
