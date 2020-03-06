package pub.cellebi.neteasyfx;

public final class Song {
    public final String url;
    public final String name;
    public final String imageUrl;
    public final String artists;

    public Song(String url, String name, String imageUrl, String artists) {
        this.url = url;
        this.name = name;
        this.imageUrl = imageUrl;
        this.artists = artists;
    }
}
