package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}