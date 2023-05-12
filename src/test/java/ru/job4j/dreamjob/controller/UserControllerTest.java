package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegisterPageThenGetRegisterPage() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterUserThenSameUserAndRedirectToVacanciesPage() {
        var user = new User(1, "test@mail.ru", "name", "password");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenRegisterUserWithExistingEmailThenGetErrorPage() {
        when(userService.findUserByEmail(any())).thenReturn(Optional.of(new User()));

        var model = new ConcurrentModel();
        var view = userController.register(new User(), model);

        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenRequestLoginPageThenGetLoginPage() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserThenRedirectToVacanciesPage() {
        var request = mock(HttpServletRequest.class);
        var user = new User(1, "test@mail.ru", "name", "password");
        when(userService.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(new MockHttpSession());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");

    }

    @Test
    public void whenRequestLogoutThenGetLoginPage() {
        var view = userController.logout(new MockHttpSession());

        assertThat(view).isEqualTo("redirect:/users/login");
    }
}