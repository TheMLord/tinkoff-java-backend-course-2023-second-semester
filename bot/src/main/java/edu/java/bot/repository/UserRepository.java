package edu.java.bot.repository;

import edu.java.bot.models.db_entities.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
//TODO: переделать на CRUD
public class UserRepository {
    private final Map<Long, User> userDb = new HashMap<>();

    public Optional<User> findUserById(Long id) {
        return (userDb.containsKey(id)) ? Optional.of(userDb.get(id)) : Optional.empty();
    }

    public void saveUser(User user) {
        userDb.put(user.getId(), user);
    }
}
