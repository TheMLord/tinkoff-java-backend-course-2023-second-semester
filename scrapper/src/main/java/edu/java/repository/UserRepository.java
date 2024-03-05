package edu.java.repository;

import edu.java.models.User;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {
    private final Map<Long, User> userTable = new HashMap<>();

    public Optional<User> findUserById(long id) {
        return (userTable.containsKey(id)) ? Optional.of(userTable.get(id)) : Optional.empty();
    }

    public List<URI> findUserSites(long id) {
        return userTable.get(id).getUriList();
    }

    public void saveUser(User user) {
        userTable.put(user.getId(), user);
    }

    public void deleteByIdUser(long id) {
        userTable.remove(id);
    }

    public void appendLink(long id, URI link) {
        var user = userTable.get(id);
        var listUri = new ArrayList<>(user.getUriList());
        listUri.add(link);
        user.setUriList(listUri);
        saveUser(user);
    }

    public void deleteUserLink(long id, URI link) {
        var user = userTable.get(id);
        var listUri = new ArrayList<>(user.getUriList());
        listUri.remove(link);
        user.setUriList(listUri);
        saveUser(user);
    }
}
