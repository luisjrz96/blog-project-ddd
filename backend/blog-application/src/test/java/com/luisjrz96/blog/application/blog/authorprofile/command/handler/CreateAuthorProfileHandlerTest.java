package com.luisjrz96.blog.application.blog.authorprofile.command.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.application.blog.authorprofile.command.CreateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.authorprofile.AuthorProfile;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.SocialNetwork;
import com.luisjrz96.blog.domain.shared.Url;

@ExtendWith(MockitoExtension.class)
class CreateAuthorProfileHandlerTest {

  @Mock private AuthorProfileRepository repository;
  @Mock private UserProvider userProvider;
  @Mock private TransactionalExecutor tx;
  @InjectMocks private CreateAuthorProfileHandler handler;

  @Test
  void handle_shouldCreateAuthorProfile_whenAuthorProfileDoesNotExist() {
    Actor user = new Actor("41dece32-24b0-4ce0-95e9-9eb77d113400", Set.of(Role.ROLE_AUTHOR.name()));
    AuthorProfile authorProfileInDb = mock(AuthorProfile.class);
    var cmd =
        new CreateAuthorProfileCommand(
            new AuthorId(UUID.fromString("41dece32-24b0-4ce0-95e9-9eb77d113400")),
            new Markdown("abcd"),
            new ImageUrl("https://images/img1.jpg"),
            new Url("https://resumes/myresume.pdf"),
            new Url("https://portfolio/myportfolio.jpg"),
            List.of(
                new SocialLink(SocialNetwork.LINKEDIN, new Url("https://linkedin.in/jhondoe"))));
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    when(userProvider.getCurrentUser()).thenReturn(user);
    when(repository.load(new AuthorId(UUID.fromString(user.userId()))))
        .thenReturn(authorProfileInDb);
    when(authorProfileInDb.getAuthorId()).thenReturn(null);

    handler.handle(cmd);

    verify(repository).save(any(AuthorProfile.class));
  }

  @Test
  void handle_shouldThrowException_whenAuthorProfileAlreadyExist() {
    Actor user = new Actor("41dece32-24b0-4ce0-95e9-9eb77d113400", Set.of(Role.ROLE_AUTHOR.name()));
    AuthorProfile authorProfileInDb = mock(AuthorProfile.class);
    AuthorId authorId = new AuthorId(UUID.fromString(user.userId()));
    var cmd =
        new CreateAuthorProfileCommand(
            new AuthorId(UUID.fromString("41dece32-24b0-4ce0-95e9-9eb77d113400")),
            new Markdown("abcd"),
            new ImageUrl("https://images/img1.jpg"),
            new Url("https://resumes/myresume.pdf"),
            new Url("https://portfolio/myportfolio.jpg"),
            List.of(
                new SocialLink(SocialNetwork.LINKEDIN, new Url("https://linkedin.in/jhondoe"))));
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    when(userProvider.getCurrentUser()).thenReturn(user);
    when(repository.load(authorId)).thenReturn(authorProfileInDb);
    when(authorProfileInDb.getAuthorId()).thenReturn(authorId);

    ApplicationException ex = assertThrows(ApplicationException.class, () -> handler.handle(cmd));

    assertTrue(ex.getMessage().contains("already exist"));
    verify(repository, times(0)).save(any(AuthorProfile.class));
  }
}
