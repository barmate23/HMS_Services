package com.hotelerp.userservice.config;

import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CommonMasterRepository commonMasterRepository;

    @Override
    public void run(String... args) {
        if (commonMasterRepository.count() == 0) {
            log.info("Seeding common master data...");
            List<CommonMaster> masters = Arrays.asList(
                // SOP Frequency
                CommonMaster.builder().category("SOP_FREQUENCY").code("DAILY").value("Daily").build(),
                CommonMaster.builder().category("SOP_FREQUENCY").code("WEEKLY").value("Weekly").build(),
                CommonMaster.builder().category("SOP_FREQUENCY").code("MONTHLY").value("Monthly").build(),
                
                // Responsible Roles
                CommonMaster.builder().category("RESPONSIBLE_ROLE").code("HOUSEKEEPER").value("Housekeeper").build(),
                CommonMaster.builder().category("RESPONSIBLE_ROLE").code("INSPECTOR").value("Inspector").build(),
                CommonMaster.builder().category("RESPONSIBLE_ROLE").code("SUPERVISOR").value("Supervisor").build(),

                // Task Priority
                CommonMaster.builder().category("PRIORITY").code("LOW").value("Low").build(),
                CommonMaster.builder().category("PRIORITY").code("NORMAL").value("Normal").build(),
                CommonMaster.builder().category("PRIORITY").code("HIGH").value("High").build(),
                CommonMaster.builder().category("PRIORITY").code("URGENT").value("Urgent").build(),

                // POS Outlet Types
                CommonMaster.builder().category("OUTLET_TYPE").code("RESTAURANT").value("Restaurant").build(),
                CommonMaster.builder().category("OUTLET_TYPE").code("BAR").value("Bar").build(),
                CommonMaster.builder().category("OUTLET_TYPE").code("SPA").value("Spa").build(),
                CommonMaster.builder().category("OUTLET_TYPE").code("ROOM_SERVICE").value("Room Service").build(),

                // Lost & Found Categories
                CommonMaster.builder().category("LOST_FOUND_CAT").code("PERSONAL").value("Personal Items").build(),
                CommonMaster.builder().category("LOST_FOUND_CAT").code("JEWELLERY").value("Jewellery").build(),
                CommonMaster.builder().category("LOST_FOUND_CAT").code("ELECTRONICS").value("Electronics").build()
            );
            commonMasterRepository.saveAll(masters);
            log.info("Successfully seeded {} master records", masters.size());
        }
    }
}
