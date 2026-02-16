package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // ==================== ТЕСТЫ НА ПОЛУЧЕНИЕ ВСЕХ ПОЛЬЗОВАТЕЛЕЙ ====================

    @Test
    void findAll_ShouldReturnAllUsers() throws Exception {
        // given
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));

        Collection<User> users = Arrays.asList(user1, user2);
        when(userService.findAll()).thenReturn(users);

        // when/then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user1@test.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("user2@test.com"));

        verify(userService, times(1)).findAll();
    }

    // ==================== ТЕСТЫ НА СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ ====================

    @Test
    void create_WithValidUser_ShouldReturnCreatedUser() throws Exception {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = new User();
        createdUser.setId(1);
        createdUser.setEmail("test@test.com");
        createdUser.setLogin("testlogin");
        createdUser.setName("Test User");
        createdUser.setBirthday(LocalDate.of(1990, 1, 1));

        when(userService.create(any(User.class))).thenReturn(createdUser);

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.login").value("testlogin"));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void create_WithBlankEmail_ShouldReturnBadRequest() throws Exception {
        // given
        User user = new User();
        user.setEmail("");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void create_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // given
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void create_WithBlankLogin_ShouldReturnBadRequest() throws Exception {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void create_WithLoginContainingSpaces_ShouldReturnBadRequest() throws Exception {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("test login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void create_WithNullBirthday_ShouldReturnBadRequest() throws Exception {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(null);

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void create_WithFutureBirthday_ShouldReturnBadRequest() throws Exception {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusYears(1));

        // when/then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(User.class));
    }

    // ==================== ТЕСТЫ НА ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ====================

    @Test
    void update_WithValidUser_ShouldReturnUpdatedUser() throws Exception {
        // given
        User user = new User();
        user.setId(1);
        user.setEmail("updated@test.com");
        user.setLogin("updatedlogin");
        user.setName("Updated User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userService.update(any(User.class))).thenReturn(user);

        // when/then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.login").value("updatedlogin"));

        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    void update_WithNonExistingUser_ShouldReturnNotFound() throws Exception {
        // given
        User user = new User();
        user.setId(999);
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userService.update(any(User.class))).thenThrow(new NotFoundException("Пользователь с ID 999 не найден"));

        // when/then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).update(any(User.class));
    }

    // ==================== ТЕСТЫ НА УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ====================

    @Test
    void remove_WithValidId_ShouldReturnOk() throws Exception {
        // given
        Integer id = 1;
        when(userService.remove(id)).thenReturn("Пользователь удален");

        // when/then
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());

        verify(userService, times(1)).remove(id);
    }

    @Test
    void remove_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 999;
        when(userService.remove(id)).thenThrow(new NotFoundException("Пользователь с ID 999 не найден"));

        // when/then
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).remove(id);
    }

    // ==================== ТЕСТЫ НА ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО ID ====================

    @Test
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        // given
        Integer id = 1;
        User user = new User();
        user.setId(id);
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userService.userById(id)).thenReturn(user);

        // when/then
        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("test@test.com"));

        verify(userService, times(1)).userById(id);
    }

    @Test
    void getUserById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 999;
        when(userService.userById(id)).thenThrow(new NotFoundException("Пользователь с ID 999 не найден"));

        // when/then
        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).userById(id);
    }

    // ==================== ТЕСТЫ НА ДРУЗЕЙ ====================

    @Test
    void addFriend_WithValidIds_ShouldReturnOk() throws Exception {
        // given
        Integer userId = 1;
        Integer friendId = 2;
        doNothing().when(userService).addFriend(userId, friendId);

        // when/then
        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        verify(userService, times(1)).addFriend(userId, friendId);
    }

    @Test
    void addFriend_WithInvalidUserId_ShouldReturnNotFound() throws Exception {
        // given
        Integer userId = 999;
        Integer friendId = 2;
        doThrow(new NotFoundException("Пользователь с ID 999 не найден"))
                .when(userService).addFriend(userId, friendId);

        // when/then
        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).addFriend(userId, friendId);
    }

    @Test
    void removeFriend_WithValidIds_ShouldReturnOk() throws Exception {
        // given
        Integer userId = 1;
        Integer friendId = 2;
        doNothing().when(userService).removeFriend(userId, friendId);

        // when/then
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", userId, friendId))
                .andExpect(status().isOk());

        verify(userService, times(1)).removeFriend(userId, friendId);
    }

    @Test
    void getFriends_WithValidUserId_ShouldReturnFriends() throws Exception {
        // given
        Integer userId = 1;
        User friend1 = new User();
        friend1.setId(2);
        friend1.setEmail("friend1@test.com");

        User friend2 = new User();
        friend2.setId(3);
        friend2.setEmail("friend2@test.com");

        List<User> friends = Arrays.asList(friend1, friend2);
        when(userService.getFriends(userId)).thenReturn(friends);

        // when/then
        mockMvc.perform(get("/users/{id}/friends", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3));

        verify(userService, times(1)).getFriends(userId);
    }

    @Test
    void getCommonFriends_WithValidIds_ShouldReturnCommonFriends() throws Exception {
        // given
        Integer userId = 1;
        Integer otherId = 2;

        User commonFriend = new User();
        commonFriend.setId(3);
        commonFriend.setEmail("common@test.com");

        List<User> commonFriends = List.of(commonFriend);
        when(userService.getCommonFriends(userId, otherId)).thenReturn(commonFriends);

        // when/then
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId, otherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));

        verify(userService, times(1)).getCommonFriends(userId, otherId);
    }
}