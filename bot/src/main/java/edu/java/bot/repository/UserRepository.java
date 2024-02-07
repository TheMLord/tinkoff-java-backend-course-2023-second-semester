package edu.java.bot.repository;

import edu.java.bot.model.db_entities.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
//TODO: переделать на CRUD
public class UserRepository {
//    private static final Logger REPOSITORY_LOGGER = LogManager.getLogger(UserRepository.class.getName());
    private final Map<Long, User> userDb = new HashMap<>();

    public Optional<User> findUserById(Long id) {
        return (userDb.containsKey(id)) ? Optional.of(userDb.get(id)) : Optional.empty();
    }

    public void saveUser(User user) {
        userDb.put(user.getId(), user);
//        REPOSITORY_LOGGER.info("Сохранен пользователь %s %s %s: ".formatted(
//            user.getId(),
//            user.getSites().toString(),
//            user.getState().toString()
//        ));
//        REPOSITORY_LOGGER.info("Текущие пользователи: " + userDb.keySet());
    }
}
