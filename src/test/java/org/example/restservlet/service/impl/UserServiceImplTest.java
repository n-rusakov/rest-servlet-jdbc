package org.example.restservlet.service.impl;

import org.example.restservlet.entity.Game;
import org.example.restservlet.entity.Publisher;
import org.example.restservlet.entity.User;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.repository.GameRepository;
import org.example.restservlet.repository.UserRepository;
import org.example.restservlet.service.GameService;
import org.example.restservlet.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class UserServiceImplTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final UserService userService = new UserServiceImpl(userRepository);

    @Test
    public void whenGetAll_thenCorrectCount() {
        List<User> expected = List.of(
                new User(1L, "U1", "mail1"),
                new User(2L, "U2", "mail2"),
                new User(3L, "U3", "mail3"));

        Mockito.when(userRepository.findAll()).thenReturn(expected);

        List<User> actual = userService.findAll();

        Assert.assertEquals(expected, actual);
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void whenGetById_thenUsedRepository() {
        User expected = new User(5L, "testname", "testmail");
        Mockito.when(userRepository.findById(5L)).thenReturn(Optional.of(expected));

        User actual = userService.findById(5L);

        Assert.assertEquals(expected, actual);
        Mockito.verify(userRepository, Mockito.times(1)).findById(5L);
    }

    @Test
    public void whenGetNotExistedId_thenThrow(){
        Mockito.when(userRepository.findById(5L)).thenReturn(Optional.empty());

        Assert.assertThrows(EntityNotFoundException.class, () -> userService.findById(5L));
    }

    @Test
    public void whenInsert_thenUsedRepository() {
        User inserted = new User();
        inserted.setName("testname");
        inserted.setEmail("testmail");

        User expected = new User(1L, "testname", "testmail");
        Mockito.when(userRepository.insert(inserted)).thenReturn(expected);

        User actual = userService.insert(inserted);

        Assert.assertEquals(expected, actual);
        Mockito.verify(userRepository, Mockito.times(1)).insert(Mockito.any());
    }

    @Test
    public void whenUpdate_thenUseRepository() {
        User updated = new User(2L, "testname", "testmail");
        User expected = new User(2L, "testname", "testmail");

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(updated));
        Mockito.when(userRepository.update(updated)).thenReturn(expected);

        User actual = userService.update(2L, updated);

        Assert.assertEquals(expected, actual);
        Mockito.verify(userRepository, Mockito.times(1)).update(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1)).findById(2L);
    }

    @Test
    public void whenDelete_thenUserRepository() {
        Mockito.doNothing().when(userRepository).deleteById(Mockito.any());
        userService.deleteById(2L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(2L);
    }

    @Test
    public void whenAddSubscription() {
        User expected = new User(2L, "testname", "testmail");
        Mockito.doNothing().when(userRepository).addSubscription(Mockito.anyLong(), Mockito.anyLong());
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(expected));

        User actual = userService.addGame(2L, 3L);

        Assert.assertEquals(expected, actual);
        Mockito.verify(userRepository, Mockito.times(1)).
                addSubscription(2L, 3L);
        Mockito.verify(userRepository, Mockito.times(1)).
                findById(2L);
    }

    @Test
    public void whenDeleteSubscription() {
        User expected = new User(2L, "testname", "testmail");

        Mockito.doNothing().when(userRepository).deleteSubscription(Mockito.anyLong(), Mockito.anyLong());
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(expected));

        userService.deleteGame(2L, 3L);
        Mockito.verify(userRepository, Mockito.times(1)).
                deleteSubscription(2L, 3L);
    }

}
