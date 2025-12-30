package com.luisjrz96.blog.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luisjrz96.blog.application.blog.authorprofile.AuthorProfileService;
import com.luisjrz96.blog.application.blog.authorprofile.command.handler.CreateAuthorProfileHandler;
import com.luisjrz96.blog.application.blog.authorprofile.command.handler.UpdateAuthorProfileHandler;
import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileRepository;
import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileViewReader;
import com.luisjrz96.blog.application.blog.authorprofile.query.handler.GetAuthorProfileByIdHandler;
import com.luisjrz96.blog.application.blog.category.CategoryService;
import com.luisjrz96.blog.application.blog.category.command.handler.ArchiveCategoryHandler;
import com.luisjrz96.blog.application.blog.category.command.handler.CreateCategoryHandler;
import com.luisjrz96.blog.application.blog.category.command.handler.UpdateCategoryHandler;
import com.luisjrz96.blog.application.blog.category.port.CategoryLookup;
import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.blog.category.port.CategoryViewReader;
import com.luisjrz96.blog.application.blog.category.query.handler.GetCategoriesPageHandler;
import com.luisjrz96.blog.application.blog.category.query.handler.GetCategoryByIdHandler;
import com.luisjrz96.blog.application.blog.post.PostService;
import com.luisjrz96.blog.application.blog.post.command.handler.ArchivePostHandler;
import com.luisjrz96.blog.application.blog.post.command.handler.CreatePostHandler;
import com.luisjrz96.blog.application.blog.post.command.handler.PublishPostHandler;
import com.luisjrz96.blog.application.blog.post.command.handler.UpdatePostHandler;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.blog.post.port.PostViewReader;
import com.luisjrz96.blog.application.blog.post.query.handler.GetPostByIdHandler;
import com.luisjrz96.blog.application.blog.post.query.handler.GetPostsPageHandler;
import com.luisjrz96.blog.application.blog.tag.TagService;
import com.luisjrz96.blog.application.blog.tag.command.handler.ArchiveTagHandler;
import com.luisjrz96.blog.application.blog.tag.command.handler.CreateTagHandler;
import com.luisjrz96.blog.application.blog.tag.command.handler.UpdateTagHandler;
import com.luisjrz96.blog.application.blog.tag.port.TagLookup;
import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.blog.tag.port.TagViewReader;
import com.luisjrz96.blog.application.blog.tag.query.handler.GetTagByIdHandler;
import com.luisjrz96.blog.application.blog.tag.query.handler.GetTagsPageHandler;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;

@Configuration
public class AppConfig {

  @Bean
  public CategoryService categoryService(
      CreateCategoryHandler createCategoryHandler,
      UpdateCategoryHandler updateCategoryHandler,
      ArchiveCategoryHandler archiveCategoryHandler,
      GetCategoriesPageHandler getCategoriesPageHandler,
      GetCategoryByIdHandler getCategoryByIdHandler) {
    return new CategoryService(
        createCategoryHandler,
        updateCategoryHandler,
        archiveCategoryHandler,
        getCategoriesPageHandler,
        getCategoryByIdHandler);
  }

  @Bean
  public TagService tagService(
      CreateTagHandler createTagHandler,
      UpdateTagHandler updateTagHandler,
      ArchiveTagHandler archiveTagHandler,
      GetTagsPageHandler getTagsPageHandler,
      GetTagByIdHandler getTagByIdHandler) {
    return new TagService(
        createTagHandler,
        updateTagHandler,
        archiveTagHandler,
        getTagsPageHandler,
        getTagByIdHandler);
  }

  @Bean
  public PostService postService(
      CreatePostHandler createPostHandler,
      UpdatePostHandler updatePostHandler,
      ArchivePostHandler archivePostHandler,
      PublishPostHandler publishPostHandler,
      GetPostByIdHandler getPostByIdHandler,
      GetPostsPageHandler getPostsPageHandler) {
    return new PostService(
        createPostHandler,
        updatePostHandler,
        archivePostHandler,
        publishPostHandler,
        getPostByIdHandler,
        getPostsPageHandler);
  }

  @Bean
  public AuthorProfileService authorProfileService(
      CreateAuthorProfileHandler createAuthorProfileHandler,
      UpdateAuthorProfileHandler updateAuthorProfileHandler,
      GetAuthorProfileByIdHandler getAuthorProfileByIdHandler) {
    return new AuthorProfileService(
        createAuthorProfileHandler, updateAuthorProfileHandler, getAuthorProfileByIdHandler);
  }

  @Bean
  public CreateTagHandler createTagHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      TagRepository tagRepository) {
    return new CreateTagHandler(transactionalExecutor, userProvider, tagRepository);
  }

  @Bean
  public UpdateTagHandler updateTagHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      TagRepository tagRepository) {
    return new UpdateTagHandler(transactionalExecutor, userProvider, tagRepository);
  }

  @Bean
  public ArchiveTagHandler archiveTagHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      TagRepository tagRepository) {
    return new ArchiveTagHandler(transactionalExecutor, userProvider, tagRepository);
  }

  @Bean
  GetTagByIdHandler getTagByIdHandler(TagViewReader reader) {
    return new GetTagByIdHandler(reader);
  }

  @Bean
  public GetTagsPageHandler getTagsPageHandler(TagViewReader tagViewReader) {
    return new GetTagsPageHandler(tagViewReader);
  }

  @Bean
  public CreateCategoryHandler createCategoryHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      CategoryRepository categoryRepository) {
    return new CreateCategoryHandler(transactionalExecutor, userProvider, categoryRepository);
  }

  @Bean
  public UpdateCategoryHandler updateCategoryHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      CategoryRepository categoryRepository) {
    return new UpdateCategoryHandler(transactionalExecutor, userProvider, categoryRepository);
  }

  @Bean
  public ArchiveCategoryHandler archiveCategoryHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      CategoryRepository categoryRepository) {
    return new ArchiveCategoryHandler(transactionalExecutor, userProvider, categoryRepository);
  }

  @Bean
  public GetCategoryByIdHandler getCategoryByIdHandler(CategoryViewReader categoryViewReader) {
    return new GetCategoryByIdHandler(categoryViewReader);
  }

  @Bean
  public GetCategoriesPageHandler getCategoriesPageHandler(CategoryViewReader categoryViewReader) {
    return new GetCategoriesPageHandler(categoryViewReader);
  }

  @Bean
  public CreatePostHandler createPostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository postRepository,
      TagLookup tagLookup,
      CategoryLookup categoryLookup) {
    return new CreatePostHandler(
        transactionalExecutor, userProvider, postRepository, tagLookup, categoryLookup);
  }

  @Bean
  public UpdatePostHandler updatePostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository postRepository,
      TagLookup tagLookup,
      CategoryLookup categoryLookup) {
    return new UpdatePostHandler(
        transactionalExecutor, userProvider, postRepository, tagLookup, categoryLookup);
  }

  @Bean
  public PublishPostHandler publishPostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository postRepository) {
    return new PublishPostHandler(transactionalExecutor, userProvider, postRepository);
  }

  @Bean
  public ArchivePostHandler archivePostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository postRepository) {
    return new ArchivePostHandler(transactionalExecutor, userProvider, postRepository);
  }

  @Bean
  public GetPostByIdHandler getPostByIdHandler(PostViewReader postViewReader) {
    return new GetPostByIdHandler(postViewReader);
  }

  @Bean
  public GetPostsPageHandler getPostsPageHandler(PostViewReader postViewReader) {
    return new GetPostsPageHandler(postViewReader);
  }

  @Bean
  public CreateAuthorProfileHandler createAuthorProfileHandler(
      TransactionalExecutor tx, UserProvider userProvider, AuthorProfileRepository repository) {
    return new CreateAuthorProfileHandler(tx, userProvider, repository);
  }

  @Bean
  public UpdateAuthorProfileHandler updateAuthorProfileHandler(
      TransactionalExecutor tx, UserProvider userProvider, AuthorProfileRepository repository) {
    return new UpdateAuthorProfileHandler(tx, userProvider, repository);
  }

  @Bean
  public GetAuthorProfileByIdHandler getAuthorProfileByIdHandler(AuthorProfileViewReader reader) {
    return new GetAuthorProfileByIdHandler(reader);
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new JsonNullableModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}
