package ru.yandex.practicum.smarthome.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;
import ru.yandex.practicum.smarthome.repository.HeatingSystemRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class HeatingSystemControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HeatingSystemRepository heatingSystemRepository;

    @Test
    void testGetHeatingSystem() throws Exception {
        HeatingSystem heatingSystem = new HeatingSystem();
        heatingSystem.setOn(false);
        heatingSystem.setTargetTemperature(20.0);
        heatingSystem = heatingSystemRepository.save(heatingSystem);

        mockMvc.perform(get("/api/heating/{id}", heatingSystem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(heatingSystem.getId()))
                .andExpect(jsonPath("$.on").value(false))
                .andExpect(jsonPath("$.targetTemperature").value(20.0));
    }

    @Test
    void testUpdateHeatingSystem() throws Exception {
        HeatingSystem heatingSystem = new HeatingSystem();
        heatingSystem.setOn(false);
        heatingSystem.setTargetTemperature(20.0);
        heatingSystem = heatingSystemRepository.save(heatingSystem);

        HeatingSystemDto updateDto = new HeatingSystemDto();
        updateDto.setOn(true);
        updateDto.setTargetTemperature(22.5);

        mockMvc.perform(put("/api/heating/{id}", heatingSystem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(heatingSystem.getId()))
                .andExpect(jsonPath("$.on").value(true))
                .andExpect(jsonPath("$.targetTemperature").value(22.5));
    }

    @Test
    void testTurnOn() throws Exception {
        HeatingSystem heatingSystem = new HeatingSystem();
        heatingSystem.setOn(false);
        heatingSystem.setTargetTemperature(20.0);
        heatingSystem = heatingSystemRepository.save(heatingSystem);

        mockMvc.perform(post("/api/heating/{id}/turn-on", heatingSystem.getId()))
                .andExpect(status().isNoContent());

        HeatingSystem updatedHeatingSystem = heatingSystemRepository.findById(heatingSystem.getId())
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        assertTrue(updatedHeatingSystem.isOn());
    }

    @Test
    void testTurnOff() throws Exception {
        HeatingSystem heatingSystem = new HeatingSystem();
        heatingSystem.setOn(true);
        heatingSystem.setTargetTemperature(20.0);
        heatingSystem = heatingSystemRepository.save(heatingSystem);

        mockMvc.perform(post("/api/heating/{id}/turn-off", heatingSystem.getId()))
                .andExpect(status().isNoContent());

        HeatingSystem updatedHeatingSystem = heatingSystemRepository.findById(heatingSystem.getId())
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        assertFalse(updatedHeatingSystem.isOn());
    }

    @Test
    void testSetTargetTemperature() throws Exception {
        HeatingSystem heatingSystem = new HeatingSystem();
        heatingSystem.setOn(true);
        heatingSystem.setTargetTemperature(20.0);
        heatingSystem = heatingSystemRepository.save(heatingSystem);

        mockMvc.perform(post("/api/heating/{id}/set-temperature", heatingSystem.getId())
                .param("temperature", "23.5"))
                .andExpect(status().isNoContent());

        HeatingSystem updatedHeatingSystem = heatingSystemRepository.findById(heatingSystem.getId())
                .orElseThrow(() -> new RuntimeException("HeatingSystem not found"));
        assertEquals(23.5, updatedHeatingSystem.getTargetTemperature(), 0.01);
    }

    @Test
    void testGetCurrentTemperature() throws Exception {
        HeatingSystem heatingSystem = new HeatingSystem();
        heatingSystem.setOn(true);
        heatingSystem.setTargetTemperature(20.0);
        heatingSystem.setCurrentTemperature(19.5);
        heatingSystem = heatingSystemRepository.save(heatingSystem);

        mockMvc.perform(get("/api/heating/{id}/current-temperature", heatingSystem.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("19.5"));
    }
}