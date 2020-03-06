package pub.cellebi.neteasyfx.state;

import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import pub.cellebi.neteasyfx.Song;

public class PlayerState {

    public final ObjectProperty<MediaPlayer> player;
    public final SimpleStringProperty songImageUrl;
    public final SimpleBooleanProperty isPlaying;
    public final SimpleStringProperty songName;
    public final SimpleStringProperty artists;

    public PlayerState() {
        player = new SimpleObjectProperty<>();
        isPlaying = new SimpleBooleanProperty(false);
        songImageUrl = new SimpleStringProperty();
        songName = new SimpleStringProperty();
        artists = new SimpleStringProperty();
    }

    public void onPlaying() {
        isPlaying.set(true);
    }

    public void onPause() {
        isPlaying.set(false);
    }

    public boolean isDisable() {
        return player.get() == null;
    }

    public void updatePlayer(MediaPlayer nextPlayer) {
        var url = nextPlayer.getMedia().getSource();
        var opSong = Jux.SONG_LIST_STATE.songs.stream().filter(s -> s.url.equals(url)).findFirst();
        if (opSong.isPresent()) {
            var s = opSong.get();
            Jux.SONG_LIST_STATE.currentIndex = Jux.SONG_LIST_STATE.songs.indexOf(s);
            update(s);
            System.out.println("no need add");
            return;
        }
        if (this.player.get() != null) {
            this.player.get().stop();
            this.player.get().dispose();
        }
        this.player.set(nextPlayer);
        nextPlayer.play();
        var song = new Song(
                this.player.get().getMedia().getSource(),
                this.songName.get(),
                this.songImageUrl.get(),
                this.artists.get()
        );
        Jux.SONG_LIST_STATE.songs.add(song);
    }

    public void update(Song song) {
        if (this.player.get() != null) {
            this.player.get().stop();
            this.player.get().dispose();
        }
        var nextPlayer = new MediaPlayer(new Media(song.url));
        this.player.set(nextPlayer);
        nextPlayer.play();
        this.songName.set(song.name);
        this.isPlaying.set(true);
        this.songImageUrl.set(song.imageUrl);
        this.artists.set(song.artists);

    }
}
