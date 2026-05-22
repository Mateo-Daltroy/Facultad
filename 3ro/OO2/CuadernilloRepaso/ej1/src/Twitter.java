import java.util.ArrayList;
import java.util.stream.Collectors;

public class Twitter {
    private ArrayList<Usuario> users;

    public void deleteUser(Usuario user) {
        this.users.stream().forEach(u -> u.deleteAllTweets(user.getScreenName()));
    }

    public boolean createUser(String userName) {
        if (this.users.stream()
                .anyMatch(u -> u.getScreenName().equals(userName))
        ) {
            return false;
        }
        this.users.add(new Usuario(userName));
        return true;
    }
}