import java.util.List;

public class Tweet {
    private String content;
    private Tweet retweet;
    private Usuario poster;
    private List<Tweet> retweets;

    public Tweet(String content, Usuario poster) {
        this.content = content;
        this.poster = poster;
    }

    public Tweet(Tweet rt, Usuario poster) {
        this.retweet = rt;
        rt.agregarRetweet(this);
    }

    public void agregarRetweet(Tweet tw) {
        this.retweets.add(tw);
    }

    public void eliminar() {
        this.retweets.stream().forEach(t-> t.eliminarBase());
    }

    public void eliminarBase() {
        this.poster.eliminarTweet(this);
    }

    public boolean isBasedOn(String userName) {
        return this.retweet == null
                ? poster.isNamed(userName)
                : this.retweet.isBasedOn(userName);
    }
}