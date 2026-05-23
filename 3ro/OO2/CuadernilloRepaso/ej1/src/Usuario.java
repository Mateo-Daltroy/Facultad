import java.util.List;
import java.util.stream.Collectors;

public class Usuario {
    private String screenName;
    private List<Tweet> tweets;

    public Usuario(String name) {
        this.screenName = name;
    }

    public boolean postTweet(final String content) {
        if (content.length() > 280 || content.isEmpty())
            return false;
        this.tweets.add(new Tweet(content, this));
        return true;
    }

    public void deleteAllTweets(String userName) {
        this.tweets.stream()
                .forEach(t -> t.eliminar());
        this.tweets.clear();
    }

    public void eliminarTweet(Tweet t) {
        this.tweets.remove(t);
    }

    public boolean isNamed(String name) {
        return this.screenName.equals(name);
    }

    public String getScreenName() {
        return screenName;
    }

}