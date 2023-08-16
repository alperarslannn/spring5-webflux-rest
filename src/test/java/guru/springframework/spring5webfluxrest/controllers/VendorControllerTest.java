package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

public class VendorControllerTest
{
    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        BDDMockito.given(vendorRepository.findAll()).willReturn(Flux.just(
            Vendor.builder().firstName("George").lastName("Leber").build(),
            Vendor.builder().firstName("Hannah").lastName("Montana").build()));

        webTestClient.get().uri("/api/v1/vendors")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Category.class)
            .hasSize(2);
    }

    @Test
    public void getById() {
        BDDMockito.given(vendorRepository.findById("someId")).willReturn(
            Mono.just(Vendor.builder().firstName("George").lastName("Leber").build())
        );

        webTestClient.get().uri("/api/v1/vendors/someId")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Vendor.class);
    }

    @Test
    public void testCreateVendor() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
            .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("myName").lastName("myLastName").build());

        webTestClient.post().uri("/api/v1/vendors")
            .body(vendorToSaveMono, Vendor.class)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    public void testUpdateVendor() {
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
            .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToUpdateMono = Mono.just(Vendor.builder().build());

        webTestClient.put().uri("/api/v1/vendors/someId")
            .body(vendorToUpdateMono, Vendor.class)
            .exchange()
            .expectStatus().isOk();
    }
}