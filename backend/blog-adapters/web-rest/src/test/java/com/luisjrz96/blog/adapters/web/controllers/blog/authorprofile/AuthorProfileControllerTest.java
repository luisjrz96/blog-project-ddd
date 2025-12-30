package com.luisjrz96.blog.adapters.web.controllers.blog.authorprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.luisjrz96.blog.adapters.web.dto.AuthorProfileRequest;
import com.luisjrz96.blog.adapters.web.dto.AuthorProfileView;
import com.luisjrz96.blog.adapters.web.dto.SocialLink;
import com.luisjrz96.blog.application.blog.authorprofile.AuthorProfileService;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.application.shared.port.UserProvider;

@ExtendWith(MockitoExtension.class)
class AuthorProfileControllerTest {

  @Mock private AuthorProfileService service;
  @Mock private AuthorProfileViewMapper mapper;
  @Mock private UserProvider userProvider;
  @InjectMocks private AuthorProfileController controller;

  @Test
  void createAuthorProfile_callsService_andReturnsNoContent() {
    String userId = UUID.randomUUID().toString();
    when(userProvider.currentUserId()).thenReturn(userId);

    AuthorProfileRequest req = mock(AuthorProfileRequest.class);
    when(req.getBio()).thenReturn("bio");

    when(req.getAvatar()).thenReturn(URI.create("https://example.com/avatar.png"));
    when(req.getResumeUrl()).thenReturn(URI.create("https://example.com/resume.pdf"));
    when(req.getPortfolioUrl()).thenReturn(URI.create("https://example.com/portfolio"));

    var social1 = mock(com.luisjrz96.blog.adapters.web.dto.SocialLink.class);
    when(social1.getPlatform()).thenReturn(SocialLink.PlatformEnum.LINKEDIN);
    when(social1.getUrl()).thenReturn(URI.create("https://linkedin.com/in/jhondoe"));

    when(req.getSocialLinks()).thenReturn(List.of(social1));

    ResponseEntity<Void> resp = controller.createAuthorProfile(req);

    assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    verify(service, times(1)).create(any());
  }

  @Test
  void updateAuthorProfile_callsService_andReturnsNoContent() {
    String userId = UUID.randomUUID().toString();
    when(userProvider.currentUserId()).thenReturn(userId);

    AuthorProfileRequest req = mock(AuthorProfileRequest.class);
    when(req.getBio()).thenReturn("bio-updated");

    when(req.getAvatar()).thenReturn(URI.create("https://example.com/avatar2.png"));
    when(req.getResumeUrl()).thenReturn(URI.create("https://example.com/resume2.pdf"));
    when(req.getPortfolioUrl()).thenReturn(URI.create("https://example.com/portfolio2"));

    var social1 = mock(com.luisjrz96.blog.adapters.web.dto.SocialLink.class);
    when(social1.getPlatform()).thenReturn(SocialLink.PlatformEnum.GITHUB);
    when(social1.getUrl()).thenReturn(URI.create("https://github.com/jhondoe"));

    when(req.getSocialLinks()).thenReturn(List.of(social1));

    ResponseEntity<Void> resp = controller.updateAuthorProfile(req);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(service, times(1)).update(any());
  }

  @Test
  void getAuthorProfile_callsService_andReturnsOk_withMappedView() {
    String id = UUID.randomUUID().toString();

    AuthorProfileViewDto dto = mock(AuthorProfileViewDto.class);
    when(service.getById(any())).thenReturn(dto);

    AuthorProfileView view = mock(AuthorProfileView.class);
    when(mapper.toView(dto)).thenReturn(view);

    ResponseEntity<AuthorProfileView> resp = controller.getAuthorProfile(id);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    verify(service, times(1)).getById(any());
    verify(mapper, times(1)).toView(dto);
  }
}
