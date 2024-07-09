package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
@Slf4j
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addUser(UserDto userDto) {
        log.debug("Sending request to add user: {}", userDto);
        ResponseEntity<Object> response = post("", userDto);
        log.debug("Received response: {}", response);
        return response;
    }

    public ResponseEntity<Object> updateUser(Long userId, UserUpdateDto userUpdateDto) {
        log.debug("Sending request to update user: {}", userUpdateDto);
        ResponseEntity<Object> response = patch("/" + userId, userId, null, userUpdateDto);
        log.debug("Received response: {}", response);
        return response;
    }

    public ResponseEntity<Object> getUser(Long userId) {
        log.debug("Sending request to get user with ID: {}", userId);
        ResponseEntity<Object> response = get("/" + userId, userId);
        log.debug("Received response: {}", response);
        return response;
    }

    public ResponseEntity<Object> getUsers() {
        log.debug("Sending request to get all users");
        ResponseEntity<Object> response = get("");
        log.debug("Received response: {}", response);
        return response;
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        log.debug("Sending request to delete user with ID: {}", userId);
        ResponseEntity<Object> response = delete("/" + userId, userId, null);
        log.debug("Received response: {}", response);
        return response;
    }
}
