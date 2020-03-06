package pub.cellebi.neteasyfx.modules.search;


import com.fasterxml.jackson.databind.json.JsonMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.service.NetService;

import pub.cellebi.neteasyfx.modules.TitleBar;
import pub.cellebi.neteasyfx.utils.LogUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class SearchPane extends VBox {

    private final TitleBar titleBar;
    private final TextField searchInput;
    private final TableView<Map<String, Object>> songsView;
    private final StackPane content;
    private final HotSearchPane hotSearchPane;
    private final HistorySearchPane historySearchPane;
    private final TableColumn<Map<String, Object>, String> songCol = new TableColumn<>();
    private final TableColumn<Map<String, Object>, String> artistsCol = new TableColumn<>();
    private final TableColumn<Map<String, Object>, String> albumCol = new TableColumn<>();
    private final TableColumn<Map<String, Object>, String> timeCol = new TableColumn<>();

    private final ObservableList<Map<String, Object>> songsData;

    public SearchPane() {
        titleBar = new TitleBar("搜索");
        content = new StackPane();
        searchInput = new TextField();
        songsView = new TableView<>();
        hotSearchPane = new HotSearchPane();
        historySearchPane = new HistorySearchPane();
        songsData = FXCollections.observableArrayList();

        render();
        registerListener();
        initState();
    }

    private void render() {
        songCol.setCellValueFactory(c -> {
            var item = c.getValue();
            return new SimpleStringProperty((String) item.get("name"));
        });
        songCol.setCellFactory(t -> new SongCell());
        songCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.5));
        songCol.setResizable(false);

        artistsCol.setCellValueFactory(c -> {
            var item = c.getValue();
            return new SimpleStringProperty((String) ((List<Map<String, Object>>) (item.containsKey("artists") ? item.get("artists") : item.get("ar"))).get(0).get("name"));
        });
        artistsCol.setCellFactory(t -> new ArtistCell());
        artistsCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.18));
        artistsCol.setResizable(false);

        albumCol.setCellValueFactory(c -> {
            var item = c.getValue();
            return new SimpleStringProperty((String) ((Map<String, Object>) (item.containsKey("album") ? item.get("album") : item.get("al"))).get("name"));
        });
        albumCol.setCellFactory(t -> new AlbumCell());
        albumCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.22));
        albumCol.setResizable(false);

        timeCol.setCellValueFactory(c -> {
            var item = c.getValue();
            var duration = ((int) (item.containsKey("duration") ? item.get("duration") : item.get("dt")) / 1000);
            var min = duration / 60;
            var second = duration % 60;
            return new SimpleStringProperty(min + ":" + second);
        });
        timeCol.setCellFactory(t -> new TimeCell());
        timeCol.prefWidthProperty().bind(songsView.widthProperty().multiply(0.083));
        timeCol.setResizable(false);
        songsView.getColumns().addAll(songCol, artistsCol, albumCol, timeCol);
        songsView.setItems(songsData);
        songsView.setPlaceholder(null);
        var hBox = new HBox();
        hBox.getChildren().addAll(hotSearchPane, historySearchPane);
        HBox.setHgrow(hotSearchPane, Priority.ALWAYS);
        HBox.setHgrow(historySearchPane, Priority.ALWAYS);
        hotSearchPane.maxWidthProperty().bind(hBox.widthProperty().divide(2));
        content.getChildren().add(hBox);
        searchInput.setPromptText("search music");
        searchInput.getStyleClass().add("search-input");
        searchInput.setBorder(Border.EMPTY);
        searchInput.setBackground(Background.EMPTY);
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(titleBar, searchInput, content);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20));
        setSpacing(10);
        getStyleClass().add("search-pane");
    }

    private void registerListener() {
        searchInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (searchInput.getText().isBlank()) {
                    return;
                }
                Jux.SEARCH_STATE.historySearch.add(searchInput.getText());
                Jux.SEARCH_STATE.searchWord.set(searchInput.getText());
            }
        });
        searchInput.setOnKeyTyped(e -> {
            if (searchInput.getText().isBlank() && !content.getChildren().contains(hotSearchPane.getParent())) {
                content.getChildren().clear();
                content.getChildren().add(hotSearchPane.getParent());
                Jux.SEARCH_STATE.searchWord.set("");
                songsData.clear();
            }
        });
        songsView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var n = songsView.getSelectionModel().getSelectedItem();
                var id = (int) n.get("id");
                var albumId = (int) ((Map<String, Object>) n.get("album")).get("id");
                var artists = (String) ((List<Map<String, Object>>) (n.containsKey("artists") ? n.get("artists") : n.get("ar"))).get(0).get("name");
                var name = (String) n.get("name");
                Jux.PLAYER_STATE.songName.set(name);
                Jux.PLAYER_STATE.artists.set(artists);
                processPic(albumId).whenComplete((v, t) -> process(id));
            }
        });
        titleBar.action = () -> Jux.SEARCH_STATE.searchWord.set("");
        Jux.SEARCH_STATE.searchWord.addListener((v, o, n) -> {
            if (n != null && !n.isBlank()) {
                searchInput.setText(n);
                if (!content.getChildren().contains(songsView)) {
                    content.getChildren().clear();
                    content.getChildren().add(songsView);
                }
                fetch(n);
            }
        });
    }

    private void initState() {
        NetService.get("http://cellebi.pub:3000/search/hot", json -> {
            var mapper = new JsonMapper();
            var map = mapper.readValue(json, Map.class);
            var list = ((Map<String, List<Map<String, Object>>>) map.get("result")).get("hots")
                    .stream().map(m -> (String) m.get("first"))
                    .collect(Collectors.toList());
            Platform.runLater(() -> {
                Jux.SEARCH_STATE.hotSearch.clear();
                Jux.SEARCH_STATE.hotSearch.addAll(list);
            });
        });
    }

    private void fetch(String text) {
        NetService.get("http://cellebi.pub:3000/search?keywords="
                + URLEncoder.encode(text, StandardCharsets.UTF_8), json -> {
            var mapper = new JsonMapper();
            var map = mapper.readValue(json, Map.class);
            var list = (List<Map<String, Object>>) (((Map<String, Map<String, Object>>) map.get("result")).get("songs"));
            Platform.runLater(() -> {
                songsData.clear();
                songsData.addAll(list);
            });
        });
    }

    private void process(int id) {
        NetService.get("http://cellebi.pub:3000/song/url?id=" + id, json -> {
            var mapper = new JsonMapper();
            var map = mapper.readValue(json, Map.class);
            var url = (String) ((List<Map<String, ?>>) map.get("data")).get(0).get("url");
            Platform.runLater(() -> {
                var media = new Media(url);
                var mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnError(() -> {
                    mediaPlayer.getError().printStackTrace();
                });
                mediaPlayer.setAutoPlay(true);
                Jux.PLAYER_STATE.updatePlayer(mediaPlayer);
                Jux.PLAYER_STATE.isPlaying.set(true);
            });

        });
    }

    private CompletableFuture<Void> processPic(int albumId) {
        return NetService.get("http://cellebi.pub:3000/album?id=" + albumId, json -> {
            var mapper = new JsonMapper();
            var map = mapper.readValue(json, Map.class);
            var url = (String) ((Map<String, Object>) map.get("album")).get("picUrl");
            Platform.runLater(() -> Jux.PLAYER_STATE.songImageUrl.set(url));
        });
    }

    public static class SongCell extends TableCell<Map<String, Object>, String> {

        public SongCell() {
            getStyleClass().add("song-cell");
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item);
                setAlignment(Pos.CENTER_LEFT);
            }
        }
    }

    public static class ArtistCell extends TableCell<Map<String, Object>, String> {

        public ArtistCell() {
            getStyleClass().add("artist-cell");
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item);
                setAlignment(Pos.CENTER_LEFT);
            }
        }
    }

    public static class AlbumCell extends TableCell<Map<String, Object>, String> {

        public AlbumCell() {
            getStyleClass().add("album-cell");
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item);
                setAlignment(Pos.CENTER_LEFT);
            }
        }
    }

    public static class TimeCell extends TableCell<Map<String, Object>, String> {

        public TimeCell() {
            getStyleClass().add("time-cell");
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item);
                setAlignment(Pos.CENTER_LEFT);
            }
        }
    }
}
