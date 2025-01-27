package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

public class CategoryControllerTest
{

    WebTestClient webTestClient;
    CategoryController categoryController;
    CategoryRepository categoryRepository;
    @Before
    public void setUp() throws Exception {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void list() {
        BDDMockito.given(categoryRepository.findAll()).willReturn(Flux.just(Category.builder().id("1").description("Cat1").build(),
            Category.builder().id("2").description("Cat2").build()));

        webTestClient.get().uri("/api/v1/categories")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Category.class)
            .hasSize(2);
    }

    @Test
    public void getById() {
        BDDMockito.given(categoryRepository.findById("someId"))
            .willReturn(Mono.just(Category.builder().description("Cat").build()));

        webTestClient.get().uri("/api/v1/categories/someId")
            .exchange()
            .expectBody(Category.class);
    }

    @Test
    public void testCreateCategory() {
        BDDMockito.given(categoryRepository.saveAll(any(Publisher.class)))
            .willReturn(Flux.just(Category.builder().build()));

        Mono<Category> categoryToSaveMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.post().uri("/api/v1/categories")
            .body(categoryToSaveMono, Category.class)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    public void testUpdateCategory() {
        BDDMockito.given(categoryRepository.save(any(Category.class)))
            .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().build());

        webTestClient.put().uri("/api/v1/categories/someId")
            .body(categoryToUpdateMono, Category.class)
            .exchange()
            .expectStatus().isOk();
    }
    @Test
    public void testPatchWithChangesCategory() {
        BDDMockito.given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description("Some old cat").build()));
        BDDMockito.given(categoryRepository.save(any(Category.class)))
            .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.patch().uri("/api/v1/categories/someId")
            .body(categoryToUpdateMono, Category.class)
            .exchange()
            .expectStatus().isOk();

        BDDMockito.verify(categoryRepository).save(any());
    }

    @Test
    public void testPatchWithNoChangesCategory() {
        BDDMockito.given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().description("Some Cat").build()));
        BDDMockito.given(categoryRepository.save(any(Category.class)))
            .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());

        webTestClient.patch().uri("/api/v1/categories/someId")
            .body(categoryToUpdateMono, Category.class)
            .exchange()
            .expectStatus().isOk();

        BDDMockito.verify(categoryRepository, never()).save(any());
    }
}