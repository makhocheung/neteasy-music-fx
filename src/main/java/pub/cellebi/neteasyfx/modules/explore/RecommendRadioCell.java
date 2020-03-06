package pub.cellebi.neteasyfx.modules.explore;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import pub.cellebi.neteasyfx.state.Jux;
import pub.cellebi.neteasyfx.service.NetService;
import pub.cellebi.neteasyfx.utils.ImageUtil;

import java.util.List;
import java.util.Map;

import static pub.cellebi.neteasyfx.utils.Util.MAPPER;

public class RecommendRadioCell extends HBox {

    private final ImageView imageView;
    private final Label name;
    private final Label artist;

    private final Map<String, Object> data;

    public RecommendRadioCell(Map<String, Object> data) {
        imageView = new ImageView();
        name = new Label();
        artist = new Label();
        this.data = data;

        render();
        initState();
        registerListener();
    }

    private void render() {
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        var vBox = new VBox();
        vBox.getChildren().addAll(name, artist);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setSpacing(5);
        artist.setTextFill(Color.GRAY);

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);
        getChildren().addAll(imageView, vBox);
        setPrefHeight(60);
        setMaxWidth(300);
        getStyleClass().add("meta-cell");
    }

    @SuppressWarnings("unchecked")
    private void registerListener() {
        setOnMouseClicked(e -> {
            NetService.get("http://cellebi.pub:3000/dj/program/detail?id=" + data.get("id"), json -> {
                var program = (Map<String, Object>) MAPPER.readValue(json, Map.class).get("program");
                var mainSong = (Map<String, Object>) program.get("mainSong");
                Platform.runLater(() -> {
                    Jux.PLAYER_STATE.songImageUrl.set((String) program.get("coverUrl"));
                    Jux.PLAYER_STATE.songName.set(name.getText());
                    Jux.PLAYER_STATE.artists.set(artist.getText());
                    process((int) mainSong.get("id"));
                });
            });
        });
    }

    @SuppressWarnings("unchecked")
    private void initState() {
        name.setText((String) data.get("name"));
        var radio = (Map<String, Object>) data.get("radio");
        artist.setText((String) radio.get("name"));
        var image = ImageUtil.loadImage((String) data.get("picUrl"), 180);
        imageView.setImage(image);
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
