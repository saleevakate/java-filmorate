package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDbStorage userStorage;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void create_WithName_ShouldNotChangeName() {
        // given
        User user = new User();
        user.setLogin("testlogin");
        user.setName("Custom Name");

        User expectedUser = new User();
        expectedUser.setLogin("testlogin");
        expectedUser.setName("Custom Name");

        when(userStorage.create(any(User.class))).thenReturn(expectedUser);

        // when
        User result = userService.create(user);

        // then
        verify(userStorage).create(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getName()).isEqualTo("Custom Name");
        assertThat(result.getName()).isEqualTo("Custom Name");
    }

    @Test
    void create_ShouldReturnUserFromStorage() {
        // given
        User user = new User();
        user.setLogin("testlogin");
        user.setName("Custom Name");

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setLogin("testlogin");
        expectedUser.setName("Custom Name");

        when(userStorage.create(any(User.class))).thenReturn(expectedUser);

        // when
        User result = userService.create(user);

        // then
        assertThat(result).isEqualTo(expectedUser);
        assertThat(result.getId()).isEqualTo(1);
        verify(userStorage).create(any(User.class));
    }

    @Test
    void addFriend_ShouldCallStorage() {
        // given
        Integer userId = 1;
        Integer friendId = 2;

        doNothing().when(userStorage).addFriend(userId, friendId);

        // when
        userService.addFriend(userId, friendId);

        // then
        verify(userStorage).addFriend(userId, friendId);
    }

    @Test
    void removeFriend_ShouldCallStorage() {
        // given
        Integer userId = 1;
        Integer friendId = 2;

        doNothing().when(userStorage).removeFriend(userId, friendId);

        // when
        userService.removeFriend(userId, friendId);

        // then
        verify(userStorage).removeFriend(userId, friendId);
    }

    @Test
    void getFriends_ShouldCallStorage() {
        // given
        Integer userId = 1;
        List<User> expectedFriends = List.of(new User(), new User());
        when(userStorage.getFriends(userId)).thenReturn(expectedFriends);

        // when
        List<User> result = userService.getFriends(userId);

        // then
        assertThat(result).isEqualTo(expectedFriends);
        verify(userStorage).getFriends(userId);
    }

    @Test
    void getCommonFriends_ShouldCallStorage() {
        // given
        Integer userId = 1;
        Integer otherId = 2;
        List<User> expectedFriends = List.of(new User());
        when(userStorage.getCommonFriends(userId, otherId)).thenReturn(expectedFriends);

        // when
        List<User> result = userService.getCommonFriends(userId, otherId);

        // then
        assertThat(result).isEqualTo(expectedFriends);
        verify(userStorage).getCommonFriends(userId, otherId);
    }

    @Test
    void findAll_ShouldCallStorage() {
        // given
        Collection<User> expectedUsers = List.of(new User());
        when(userStorage.findAll()).thenReturn(expectedUsers);

        // when
        Collection<User> result = userService.findAll();

        // then
        assertThat(result).isEqualTo(expectedUsers);
        verify(userStorage).findAll();
    }

    @Test
    void update_ShouldCallStorage() {
        // given
        User user = new User();
        user.setId(1);
        user.setName("Updated Name");

        when(userStorage.update(user)).thenReturn(user);

        // when
        User result = userService.update(user);

        // then
        assertThat(result).isEqualTo(user);
        verify(userStorage).update(user);
    }

    @Test
    void remove_ShouldCallStorage() {
        // given
        Integer id = 1;
        String expected = "Пользователь удален";
        when(userStorage.remove(id)).thenReturn(expected);

        // when
        String result = userService.remove(id);

        // then
        assertThat(result).isEqualTo(expected);
        verify(userStorage).remove(id);
    }

    @Test
    void userById_ShouldCallStorage() {
        // given
        Integer id = 1;
        User expectedUser = new User();
        expectedUser.setId(1);
        when(userStorage.userById(id)).thenReturn(expectedUser);

        // when
        User result = userService.userById(id);

        // then
        assertThat(result).isEqualTo(expectedUser);
        verify(userStorage).userById(id);
    }

    @Test
    void validateUserExists_ShouldCallStorage() {
        // given
        Integer id = 1;
        User expectedUser = new User();
        expectedUser.setId(1);
        when(userStorage.validateUserExists(id)).thenReturn(expectedUser);

        // when
        User result = userService.validateUserExists(id);

        // then
        assertThat(result).isEqualTo(expectedUser);
        verify(userStorage).validateUserExists(id);
    }
}