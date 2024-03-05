package edu.java.services;

import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.RemoveUserException;
import edu.java.models.User;
import edu.java.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepository;

    public void registerUser(long id) {
        log.info("регистрация");
        userRepository.findUserById(id)
            .ifPresentOrElse(
                user -> {
                    throw new DoubleRegistrationException();
                },
                () -> userRepository.saveUser(new User(id, List.of()))
            );
    }

    public void deleteUser(long id) {
        try {
            userRepository.deleteByIdUser(id);
        } catch (Exception e) {
            throw new RemoveUserException(e);
        }
    }
}
