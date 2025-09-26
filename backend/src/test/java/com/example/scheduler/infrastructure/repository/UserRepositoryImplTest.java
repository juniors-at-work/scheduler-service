package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.exception.EmailAlreadyExistException;
import com.example.scheduler.domain.exception.UsernameAlreadyExistException;
import com.example.scheduler.domain.fixture.TestUsers;
import com.example.scheduler.domain.model.User;
import com.example.scheduler.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@JdbcTest
@ContextConfiguration(classes = UserRepositoryImpl.class)
class UserRepositoryImplTest {

    @Autowired
    private UserRepository repository;

    @Test
    void givenUserExist_WhenFindByUsername_ThenReturnOptionalWithUser() {
        String username = TestUsers.ALICE.username();

        Optional<User> userO = repository.findByUsername(username);

        then(userO).hasValue(TestUsers.ALICE);
    }

    @Test
    void givenUserExist_WhenFindByUsernameInDifferentCase_ThenReturnOptionalWithUser() {
        String username = TestUsers.ALICE.username();

        Optional<User> userO = repository.findByUsername(username.toUpperCase());

        then(userO).hasValue(TestUsers.ALICE);
    }

    @Test
    void givenUserNotExist_WhenFindByUsername_ThenReturnEmptyOptional() {
        String username = TestUsers.CHARLIE.username();

        Optional<User> userO = repository.findByUsername(username);

        then(userO).isEmpty();
    }

    @Test
    void givenUsernameAndEmailNotExist_WhenInsert_ThenInsertUserIntoDatabaseAndReturnInsertedUser() {
        User user = TestUsers.CHARLIE;

        User inserted = repository.insert(user);

        then(repository.findByUsername(user.username())).hasValue(user);
        then(inserted).isEqualTo(user);
    }

    @Test
    void givenUsernameAlreadyExist_WhenInsert_ThenThrowUsernameAlreadyExistException() {
        User user = TestUsers.CHARLIE.toBuilder()
                .username(TestUsers.ALICE.username())
                .build();

        Throwable throwable = catchThrowable(() -> repository.insert(user));

        then(throwable)
                .isInstanceOf(UsernameAlreadyExistException.class)
                .hasMessage("Username already exists: " + user.username());
    }

    @Test
    void givenUsernameInDifferentCaseAlreadyExist_WhenInsert_ThenThrowUsernameAlreadyExistException() {
        User user = TestUsers.CHARLIE.toBuilder()
                .username(TestUsers.ALICE.username().toUpperCase())
                .build();

        Throwable throwable = catchThrowable(() -> repository.insert(user));

        then(throwable)
                .isInstanceOf(UsernameAlreadyExistException.class)
                .hasMessage("Username already exists: " + user.username());
    }

    @Test
    void givenEmailAlreadyExist_WhenInsert_ThenThrowEmailAlreadyExistException() {
        User user = TestUsers.CHARLIE.toBuilder()
                .email(TestUsers.ALICE.email())
                .build();

        Throwable throwable = catchThrowable(() -> repository.insert(user));

        then(throwable)
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessage("Email already exists: " + user.email());
    }

    @Test
    void givenEmailInDifferentCaseAlreadyExist_WhenInsert_ThenThrowEmailAlreadyExistException() {
        User user = TestUsers.CHARLIE.toBuilder()
                .email(TestUsers.ALICE.email().toUpperCase())
                .build();

        Throwable throwable = catchThrowable(() -> repository.insert(user));

        then(throwable)
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessage("Email already exists: " + user.email());
    }
}
