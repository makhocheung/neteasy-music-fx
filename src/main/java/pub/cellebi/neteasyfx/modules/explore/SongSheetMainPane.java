package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pub.cellebi.neteasyfx.modules.TitleBar;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.modules.search.SearchPane;
import pub.cellebi.neteasyfx.utils.ImageUtil;
import pub.cellebi.neteasyfx.utils.Util;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pub.cellebi.neteasyfx.utils.Util.MAPPER;

public class SongSheetMainPane extends VBox {

    private final TitleBar titleBar;
    private final HBox header;
    private final ImageView songSheetImage;
    private final TableView<Map<String, Object>> songsView;
    private final Label nameLabel;
    private final Label createTimeLabel;
    private final Label descLabel;
    private final TableColumn<Map<String, Object>, String> songCol = new TableColumn<>();
    private final TableColumn<Map<String, Object>, String> artistsCol = new TableColumn<>();
    private final TableColumn<Map<String, Object>, String> albumCol = new TableColumn<>();
    private final TableColumn<Map<String, Object>, String> timeCol = new TableColumn<>();

    private final ObservableList<Map<String, Object>> songsData;
    private final Map<String, Object> data;

    public SongSheetMainPane(Map<String, Object> data) {
        titleBar = new TitleBar("歌单");
        header = new HBox();
        songSheetImage = new ImageView();
        songsView = new TableView<>();
        nameLabel = new Label();
        createTimeLabel = new Label();
        descLabel = new Label();

        this.data = data;
        songsData = FXCollections.observableArrayList();

        render();
        registerListener();
        initState();
    }

    @SuppressWarnings("unchecked")
    public void render() {
        songCol.setCellValueFactory(c -> {
            var item = c.getValue();
            return new SimpleStringProperty((String) item.get("name"));
        });
        songCol.setCellFactory(t -> new SearchPane.SongCell());
        songCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.5));
        songCol.setResizable(false);
        artistsCol.setCellValueFactory(c -> {
            var item = c.getValue();
            return new SimpleStringProperty((String) ((List<Map<String, Object>>) (item.containsKey("artists") ? item.get("artists") : item.get("ar"))).get(0).get("name"));
        });
        artistsCol.setCellFactory(t -> new SearchPane.ArtistCell());
        artistsCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.18));
        artistsCol.setResizable(false);
        albumCol.setCellValueFactory(c -> {
            var item = c.getValue();
            return new SimpleStringProperty((String) ((Map<String, Object>) (item.containsKey("album") ? item.get("album") : item.get("al"))).get("name"));
        });
        albumCol.setCellFactory(t -> new SearchPane.AlbumCell());
        albumCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.22));
        albumCol.setResizable(false);
        timeCol.setCellValueFactory(c -> {
            var item = c.getValue();
            var duration = ((int) (item.containsKey("duration") ? item.get("duration") : item.get("dt")) / 1000);
            var min = duration / 60;
            var second = duration % 60;
            return new SimpleStringProperty(min + ":" + second);
        });
        timeCol.setCellFactory(t -> new SearchPane.TimeCell());
        timeCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.083));
        timeCol.setResizable(false);
        songsView.getColumns().addAll(songCol, artistsCol, albumCol, timeCol);
        songsView.setItems(songsData);
        songsView.setPlaceholder(new Pane());
        VBox.setVgrow(songsView, Priority.ALWAYS);
        initHeader();

        setPadding(new Insets(20, 30, 0, 30));
        setSpacing(20);
        getChildren().addAll(titleBar, header, songsView);
        getStyleClass().add("song-sheet-pane");
    }

    @SuppressWarnings("unchecked")
    public void initState() {
        songSheetImage.setImage(ImageUtil.loadImage((String) data.get("coverImgUrl"), 360));
        nameLabel.setText((String) data.get("name"));
        createTimeLabel.setText("创建日期: " + Util.parseTime((long) data.get("createTime")));
        descLabel.setText("介绍: " + data.get("description"));
        var trackIds = ((List<Map<String, Object>>) data.get("trackIds")).stream()
                .map(m -> String.valueOf(m.get("id")))
                .collect(Collectors.joining(","));
        NetService.get("http://cellebi.pub:3000/song/detail?ids=" + trackIds, json -> {
            var songs = (List<Map<String, Object>>) MAPPER.readValue(json, Map.class).get("songs");
            Platform.runLater(() -> {
                songsData.clear();
                songsData.addAll(songs);
            });
        });
    }

    @SuppressWarnings("unchecked")
    public void registerListener() {
        songsView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var item = songsView.getSelectionModel().getSelectedItem();
                var id = (int) item.get("id");
                var albumId = (int) ((Map<String, Object>) item.get("al")).get("id");
                var artists = (String) ((List<Map<String, Object>>) (item.containsKey("artists") ? item.get("artists") : item.get("ar"))).get(0).get("name");
                var songName = (String) item.get("name");
                Jux.PLAYER_STATE.songName.set(songName);
                Jux.PLAYER_STATE.artists.set(artists);
                NetService.get("http://cellebi.pub:3000/album?id=" + albumId, json -> {
                    var map = MAPPER.readValue(json, Map.class);
                    var url = (String) ((Map<String, Object>) map.get("album")).get("picUrl");
                    Platform.runLater(() -> Jux.PLAYER_STATE.songImageUrl.set(url));
                }).whenComplete((v, t) -> process(id));

            }
        });
    }

    private void initHeader() {
        songSheetImage.setFitWidth(150);
        songSheetImage.setPreserveRatio(true);
        var detailPane = new VBox();
        detailPane.getChildren().addAll(nameLabel, createTimeLabel, descLabel);
        detailPane.setSpacing(10);
        header.getChildren().addAll(songSheetImage, detailPane);
        header.setSpacing(20);
        nameLabel.setFont(Font.font("Noto Sans Mono CJK SC", FontWeight.BOLD, 20));
        createTimeLabel.setTextFill(Color.rgb(150, 150, 150));
        descLabel.setTextFill(Color.rgb(150, 150, 150));
    }

    @SuppressWarnings("unchecked")
    private void process(int id) {
        NetService.get("http://cellebi.pub:3000/song/url?id=" + id, json -> {
            var map = MAPPER.readValue(json, Map.class);
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
