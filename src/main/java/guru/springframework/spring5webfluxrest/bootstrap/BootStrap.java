package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class BootStrap implements CommandLineRunner
{
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;
    @Override
    public void run(String... args) {
        loadCategories();
        loadVendors();
    }

    private void loadCategories() {
        categoryRepository
            .deleteAll()
            .thenMany(
                Flux
                    .just("Fruits", "Nuts", "Breads", "Meats", "Eggs")
                    .map(name -> new Category(null, name))
                    .flatMap(categoryRepository::save)
            )
            .then(categoryRepository.count())
            .subscribe(categories -> System.out.println(categories + " categories saved"));
    }

    private void loadVendors() {
        vendorRepository
            .deleteAll()
            .thenMany(
                Flux.just(
                        Vendor.builder().firstName("Joe").lastName("Buck").build(),
                        Vendor.builder().firstName("Michael").lastName("Weston").build(),
                        Vendor.builder().firstName("Jessie").lastName("Waters").build(),
                        Vendor.builder().firstName("Jimmy").lastName("Buffet").build()
                    )
                    .flatMap(vendorRepository::save)
            )
            .then(vendorRepository.count())
            .subscribe(vendors -> System.out.println(vendors + " vendors saved"));
    }
}
