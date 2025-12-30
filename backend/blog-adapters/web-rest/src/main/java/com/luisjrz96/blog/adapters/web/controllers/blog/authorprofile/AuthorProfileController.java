package com.luisjrz96.blog.adapters.web.controllers.blog.authorprofile;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.luisjrz96.blog.adapters.web.api.AuthorsApi;
import com.luisjrz96.blog.adapters.web.dto.AuthorProfileRequest;
import com.luisjrz96.blog.adapters.web.dto.AuthorProfileView;
import com.luisjrz96.blog.application.blog.authorprofile.AuthorProfileService;
import com.luisjrz96.blog.application.blog.authorprofile.command.CreateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.command.UpdateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.query.GetAuthorProfileByIdQuery;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.SocialNetwork;
import com.luisjrz96.blog.domain.shared.Url;

@RestController
public class AuthorProfileController implements AuthorsApi {

  private final AuthorProfileService service;
  private final AuthorProfileViewMapper mapper;
  private final UserProvider userProvider;

  public AuthorProfileController(
      AuthorProfileService service, AuthorProfileViewMapper mapper, UserProvider userProvider) {
    this.service = service;
    this.mapper = mapper;
    this.userProvider = userProvider;
  }

  @Override
  public ResponseEntity<Void> createAuthorProfile(AuthorProfileRequest request) {
    var cmd =
        new CreateAuthorProfileCommand(
            new AuthorId(UUID.fromString(userProvider.currentUserId())),
            new Markdown(request.getBio()),
            new ImageUrl(request.getAvatar().toString()),
            new Url(request.getResumeUrl().toString()),
            new Url(request.getPortfolioUrl().toString()),
            socialLinkDomain(request.getSocialLinks()));
    service.create(cmd);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  public ResponseEntity<AuthorProfileView> getAuthorProfile(String id) {
    var dto = service.getById(new GetAuthorProfileByIdQuery(new AuthorId(UUID.fromString(id))));
    return ResponseEntity.ok(mapper.toView(dto));
  }

  @Override
  public ResponseEntity<Void> updateAuthorProfile(AuthorProfileRequest request) {
    var cmd =
        new UpdateAuthorProfileCommand(
            new AuthorId(UUID.fromString(userProvider.currentUserId())),
            new Markdown(request.getBio()),
            new ImageUrl(request.getAvatar().toString()),
            new Url(request.getResumeUrl().toString()),
            new Url(request.getPortfolioUrl().toString()),
            socialLinkDomain(request.getSocialLinks()));
    service.update(cmd);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private List<SocialLink> socialLinkDomain(
      List<com.luisjrz96.blog.adapters.web.dto.SocialLink> socialLinkReq) {
    return socialLinkReq.stream()
        .map(
            e ->
                new SocialLink(
                    SocialNetwork.valueOf(e.getPlatform().getValue()),
                    new Url(e.getUrl().toString())))
        .toList();
  }
}
