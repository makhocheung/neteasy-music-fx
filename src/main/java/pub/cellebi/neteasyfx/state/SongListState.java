package pub.cellebi.neteasyfx.state;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pub.cellebi.neteasyfx.Song;

public class SongListState {
    public int currentIndex = -1;
    public final ObservableList<Song> songs = FXCollections.observableArrayList();

    {
        songs.addListener((ListChangeListener<Song>) c -> {
            if (c.next()) {
                currentIndex = c.getFrom();
            }
        });
    }

    public boolean canPre() {
        return currentIndex > 0;
    }

    public boolean canNext() {
        return currentIndex < songs.size() - 1;
    }
}
