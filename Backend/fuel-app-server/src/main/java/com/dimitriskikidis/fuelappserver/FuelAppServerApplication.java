package com.dimitriskikidis.fuelappserver;

import com.dimitriskikidis.fuelappserver.brand.Brand;
import com.dimitriskikidis.fuelappserver.brand.BrandRepository;
import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuel;
import com.dimitriskikidis.fuelappserver.brandfuel.BrandFuelRepository;
import com.dimitriskikidis.fuelappserver.consumer.Consumer;
import com.dimitriskikidis.fuelappserver.consumer.ConsumerRepository;
import com.dimitriskikidis.fuelappserver.fuel.Fuel;
import com.dimitriskikidis.fuelappserver.fuelstation.FuelStation;
import com.dimitriskikidis.fuelappserver.fuelstation.FuelStationRepository;
import com.dimitriskikidis.fuelappserver.fueltype.FuelType;
import com.dimitriskikidis.fuelappserver.fueltype.FuelTypeRepository;
import com.dimitriskikidis.fuelappserver.owner.Owner;
import com.dimitriskikidis.fuelappserver.owner.OwnerRepository;
import com.dimitriskikidis.fuelappserver.review.Review;
import com.dimitriskikidis.fuelappserver.review.ReviewRepository;
import com.dimitriskikidis.fuelappserver.user.Role;
import com.dimitriskikidis.fuelappserver.user.User;
import com.dimitriskikidis.fuelappserver.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class FuelAppServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuelAppServerApplication.class, args);
    }

    // Dummy data
    @Bean
    CommandLineRunner commandLineRunner(
            BrandRepository brandRepository,
            FuelTypeRepository fuelTypeRepository,
            BrandFuelRepository brandFuelRepository,
            FuelStationRepository fuelStationRepository,
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            OwnerRepository ownerRepository,
            ConsumerRepository consumerRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
//            if (true) return;
            Random random = new Random();

            // Brands
            List<Brand> brands = new ArrayList<>();
            List<List<String>> brandBuilder = List.of(
                    List.of("EKO", "eko.png"),
                    List.of("ELIN", "elin.png"),
                    List.of("BP", "bp.png")
//                    List.of("SHELL", "shell.png"),
            );
            for (List<String> brand : brandBuilder) {
                Resource resource = new ClassPathResource("static/" + brand.get(1));
                File file = resource.getFile();
                BufferedImage bufferedImage = ImageIO.read(file);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", output);
                byte[] data = output.toByteArray();
                brands.add(new Brand(brand.get(0), data));
            }
            brands = brandRepository.saveAll(brands);

            // Fuel types
            List<FuelType> fuelTypes = List.of(
                    new FuelType("Unleaded 95"),
                    new FuelType("Unleaded 100"),
                    new FuelType("Diesel"),
                    new FuelType("Diesel Extra")
            );
            fuelTypes = fuelTypeRepository.saveAll(fuelTypes);

            // Brand fuels
            List<BrandFuel> brandFuels = new ArrayList<>();
            Integer isEnabledLimit = 4;
            for (Brand brand : brands) {
                for (FuelType fuelType : fuelTypes) {
                    Boolean isEnabled = fuelType.getId() <= isEnabledLimit;
                    String name = String.format("%s %s", brand.getName(), fuelType.getName());
                    if (!isEnabled) {
                        name = String.format("%s", fuelType.getName());
                    }
                    BrandFuel brandFuel = new BrandFuel(
                            brand.getId(),
                            fuelType.getId(),
                            name,
                            isEnabled
                    );
                    brandFuels.add(brandFuel);
                }
            }
            brandFuels = brandFuelRepository.saveAll(brandFuels);

            // Fuel stations
            List<FuelStation> fuelStations = new ArrayList<>();
            double leftLatLimit = 37.978742D;
            double rightLatLimit = 38.003187D;
            double leftLonLimit = 23.332705D;
            double rightLonLimit = 23.352825D;
            int leftPriceLimit = 500;
            int rightPriceLimit = 2001;

            for (int i = 0; i < 8; i++) {
                Brand brand = brands.get(i % brands.size());

                FuelStation fuelStation = new FuelStation(
                        brand.getId(),
                        random.nextDouble(leftLatLimit, rightLatLimit),
                        random.nextDouble(leftLonLimit, rightLonLimit),
                        String.format("Fuel Station %d", i + 1),
                        "City",
                        String.format("Address %d", i + 1),
                        "12345",
                        "2101234567"
                );
                fuelStations.add(fuelStation);
            }
            fuelStations = fuelStationRepository.saveAll(fuelStations);

            // Fuels
            for (FuelStation fuelStation : fuelStations) {
                Integer brandId = fuelStation.getBrandId();
                List<BrandFuel> fuelStationBrandFuels = brandFuels.stream()
                        .filter(bf -> bf.getIsEnabled().equals(true) && bf.getBrandId().equals(brandId))
                        .toList();

                for (BrandFuel brandFuel : fuelStationBrandFuels) {
                    Integer fuelTypeId = brandFuel.getFuelTypeId();

                    Fuel fuel = new Fuel(
                            fuelTypeId,
                            brandFuel.getName(),
                            random.nextInt(leftPriceLimit, rightPriceLimit),
                            LocalDateTime.now(ZoneOffset.UTC),
                            fuelStation
                    );
                    fuelStation.addFuel(fuel);
                }
            }
            fuelStations = fuelStationRepository.saveAll(fuelStations);

            // Users - Admins
            User admin1 = new User(
                    "admin1@gmail.com",
                    passwordEncoder.encode("123456"),
                    Role.ADMIN
            );
            userRepository.save(admin1);

            // Users - Owners
            for (FuelStation fuelStation : fuelStations) {
                Integer fuelStationId = fuelStation.getId();
                User user = new User(
                        String.format("owner%d@gmail.com", fuelStationId),
                        passwordEncoder.encode("123456"),
                        Role.OWNER
                );
                user = userRepository.save(user);

                Owner owner = new Owner(
                        user.getId(),
                        String.format("First_name_%d", fuelStationId),
                        String.format("Last_name_%d", fuelStationId)
                );
                owner.setFuelStationId(fuelStationId);
                ownerRepository.save(owner);
            }

            // Users - Consumers
            for (int i = 1; i <= 10; i++) {
                User user = new User(
                        String.format("consumer%d@gmail.com", i),
                        passwordEncoder.encode("123456"),
                        Role.CONSUMER
                );
                user = userRepository.save(user);

                Consumer consumer = new Consumer(
                        user.getId(),
                        String.format("username_%d", i)
                );
                consumerRepository.save(consumer);
            }
            List<Consumer> consumers = consumerRepository.findAll();

            // Reviews
            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < consumers.size() / 2; i++) {
                Consumer consumer = consumers.get(i);
                int consumerId = consumer.getId();

                for (int j = 0; j < fuelStations.size() / 2; j++) {
                    int fuelStationId = fuelStations.get(j).getId();
                    String reviewText =
                            (i + j) % 2 == 0 ? String.format("Review from user %s", consumer.getUsername()) : "";

                    Review r = new Review(
                            fuelStationId,
                            consumerId,
                            5 - ((i + j) % 5),
                            reviewText,
                            LocalDateTime.now(ZoneOffset.UTC).minusDays(j - 1)
                    );
                    reviews.add(r);
                }
            }
            reviewRepository.saveAll(reviews);
        };
    }
}
