package com.hotelerp.userservice.config;

import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.Department;
import com.hotelerp.userservice.entity.Role;
import com.hotelerp.userservice.entity.Shift;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.DepartmentRepository;
import com.hotelerp.userservice.repository.RoleRepository;
import com.hotelerp.userservice.repository.ShiftRepository;
import com.hotelerp.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CommonMasterRepository commonMasterRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${hms.bootstrap.admin.enabled:true}")
    private boolean adminBootstrapEnabled;

    @Value("${hms.bootstrap.admin.employee-id:EMP-ADMIN}")
    private String adminEmployeeId;

    @Value("${hms.bootstrap.admin.full-name:System Administrator}")
    private String adminFullName;

    @Value("${hms.bootstrap.admin.username:admin}")
    private String adminUsername;

    @Value("${hms.bootstrap.admin.email:admin@hmscloud.com}")
    private String adminEmail;

    @Value("${hms.bootstrap.admin.phone:9999999999}")
    private String adminPhone;

    @Value("${hms.bootstrap.admin.password:Hms@1234}")
    private String adminPassword;

    @Value("${hms.bootstrap.admin.department:Administration}")
    private String adminDepartmentName;

    @Value("${hms.bootstrap.admin.role:System Administrator}")
    private String adminRoleName;

    @Value("${hms.bootstrap.admin.shift-name:Morning Shift}")
    private String adminShiftName;

    @Value("${hms.bootstrap.admin.shift-code:MORN}")
    private String adminShiftCode;

    @Override
    @Transactional
    public void run(String... args) {
        seedCommonMasters();
        seedBootstrapAdmin();
    }

    private void seedCommonMasters() {
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
                CommonMaster.builder().category("LOST_FOUND_CAT").code("ELECTRONICS").value("Electronics").build(),

                // Table Sections
                CommonMaster.builder().category("TABLE_SECTION").code("INDOOR").value("Indoor").build(),
                CommonMaster.builder().category("TABLE_SECTION").code("OUTDOOR").value("Outdoor").build(),
                CommonMaster.builder().category("TABLE_SECTION").code("ROOFTOP").value("Rooftop").build(),

                // Table Status
                CommonMaster.builder().category("TABLE_STATUS").code("AVAILABLE").value("Available").build(),
                CommonMaster.builder().category("TABLE_STATUS").code("OCCUPIED").value("Occupied").build(),
                CommonMaster.builder().category("TABLE_STATUS").code("RESERVED").value("Reserved").build(),
                CommonMaster.builder().category("TABLE_STATUS").code("DIRTY").value("Dirty").build()
            );
            commonMasterRepository.saveAll(masters);
            log.info("Successfully seeded {} master records", masters.size());
        }
    }

    private void seedBootstrapAdmin() {
        if (!adminBootstrapEnabled) {
            log.info("Default admin bootstrap is disabled.");
            return;
        }

        Department department = departmentRepository.findByNameIgnoreCase(adminDepartmentName)
                .orElseGet(() -> departmentRepository.save(Department.builder()
                        .name(adminDepartmentName)
                        .description("Default administration department created during service bootstrap.")
                        .isActive(true)
                        .build()));

        Role role = roleRepository.findByNameIgnoreCase(adminRoleName)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(adminRoleName)
                        .department(department)
                        .accessLevel("ADMIN")
                        .status("ACTIVE")
                        .description("Default admin role with full application access.")
                        .build()));

        Shift shift = shiftRepository.findByShiftCodeIgnoreCase(adminShiftCode)
                .or(() -> shiftRepository.findByShiftNameIgnoreCase(adminShiftName))
                .orElseGet(() -> shiftRepository.save(Shift.builder()
                        .shiftName(adminShiftName)
                        .shiftCode(adminShiftCode)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(18, 0))
                        .status("ACTIVE")
                        .notes("Default shift created during service bootstrap.")
                        .build()));

        if (userRepository.existsByUsername(adminUsername)
                || userRepository.existsByEmail(adminEmail)
                || userRepository.existsByEmployeeId(adminEmployeeId)) {
            log.info("Default admin user bootstrap skipped because the admin user already exists.");
            return;
        }

        User adminUser = User.builder()
                .employeeId(adminEmployeeId)
                .fullName(adminFullName)
                .username(adminUsername)
                .email(adminEmail)
                .phone(adminPhone)
                .department(department)
                .role(role)
                .shift(shift)
                .status("ACTIVE")
                .floorAccess("All Floors")
                .notes("Default admin user created during service bootstrap.")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .build();

        userRepository.save(adminUser);
        log.info("Default admin user created with username '{}'.", adminUsername);
    }
}
