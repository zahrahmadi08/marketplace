package com.ap.marketplace.config;

import com.ap.marketplace.domain.*;
import com.ap.marketplace.domain.enums.AdStatus;
import com.ap.marketplace.domain.enums.Role;
import com.ap.marketplace.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/** داده اولیه: ادمین، کاربر تست، دسته‌ها، شهرها و چند آگهی نمونه. فقط بار اول. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CityRepository cityRepository;
    private final AdvertisementRepository adRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedEnabled;

    public DataSeeder(UserRepository userRepository, CategoryRepository categoryRepository,
                      CityRepository cityRepository, AdvertisementRepository adRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed.enabled:true}") boolean seedEnabled) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.adRepository = adRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) return;

        // کاربران
        User admin = new User("admin", passwordEncoder.encode("admin123"),
                "مدیر سامانه", "admin@example.com", "09120000000");
        admin.setRole(Role.ADMIN);
        User ali = new User("ali", passwordEncoder.encode("test123"),
                "علی رضایی", "ali@example.com", "09121111111");
        User sara = new User("sara", passwordEncoder.encode("test123"),
                "سارا محمدی", "sara@example.com", "09122222222");
        userRepository.saveAll(List.of(admin, ali, sara));

        // دسته‌ها (سلسله‌مراتبی)
        Category vehicles = categoryRepository.save(new Category("وسایل نقلیه", null));
        Category cars = categoryRepository.save(new Category("خودرو", vehicles));
        Category realestate = categoryRepository.save(new Category("املاک", null));
        Category apartments = categoryRepository.save(new Category("آپارتمان", realestate));
        Category digital = categoryRepository.save(new Category("کالای دیجیتال", null));
        Category mobiles = categoryRepository.save(new Category("موبایل", digital));

        // شهرها
        City tehran = cityRepository.save(new City("تهران"));
        City mashhad = cityRepository.save(new City("مشهد"));
        City shiraz = cityRepository.save(new City("شیراز"));

        // آگهی‌های نمونه (تأییدشده تا در لیست عمومی دیده شوند)
        VehicleAd car = new VehicleAd("پژو ۲۰۶ تیپ ۵", "بسیار تمیز، بیمه کامل، بدون رنگ.",
                4200000000L, ali, cars, tehran, "Peugeot", "206", 1399, 62000);
        car.setStatus(AdStatus.ACTIVE);
        car.addImage(new AdImage("https://picsum.photos/seed/car/600/400", 0));

        PropertyAd flat = new PropertyAd("آپارتمان ۸۵ متری", "دو خوابه، نورگیر، طبقه سوم.",
                6500000000L, sara, apartments, mashhad, 85, 2, "بلوار وکیل‌آباد", false);
        flat.setStatus(AdStatus.ACTIVE);
        flat.addImage(new AdImage("https://picsum.photos/seed/flat/600/400", 0));

        GeneralAd phone = new GeneralAd("گوشی سامسونگ S21", "کارکرده، سالم، همراه با جعبه.",
                180000000L, ali, mobiles, shiraz, "کارکرده");
        phone.setStatus(AdStatus.ACTIVE);
        phone.addImage(new AdImage("https://picsum.photos/seed/phone/600/400", 0));

        // یک آگهی در انتظار بررسی برای تست پنل ادمین
        GeneralAd pending = new GeneralAd("دوچرخه کوهستان", "سایز ۲۷.۵، دنده شیمانو.",
                95000000L, sara, digital, tehran, "نو");
        // وضعیت پیش‌فرض PENDING باقی می‌ماند

        adRepository.saveAll(List.of(car, flat, phone, pending));

        System.out.println(">> Seed complete. Login: admin/admin123 or ali/test123");
    }
}
