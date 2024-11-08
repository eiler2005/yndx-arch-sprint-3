package ru.yandex.practicum.smarthome.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthome.dto.HeatingSystemDto;
import ru.yandex.practicum.smarthome.service.HeatingSystemService;

@RestController
@RequestMapping("/api/heating")
@RequiredArgsConstructor
public class HeatingSystemController {

    private final HeatingSystemService heatingSystemService;

    private static final Logger logger = LoggerFactory.getLogger(HeatingSystemController.class);

    @GetMapping("/{id}")
    public ResponseEntity<HeatingSystemDto> getHeatingSystem(@PathVariable("id") Long id) {
        logger.info("Fetching heating system with id {}", id);
        return ResponseEntity.ok(heatingSystemService.getHeatingSystem(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HeatingSystemDto> updateHeatingSystem(@PathVariable("id") Long id,
                                                                @RequestBody HeatingSystemDto heatingSystemDto) {
        logger.info("Updating heating system with id {}", id);
        return ResponseEntity.ok(heatingSystemService.updateHeatingSystem(id, heatingSystemDto));
    }

    @PostMapping("/{id}/turn-on")
    public ResponseEntity<Void> turnOn(@PathVariable("id") Long id) {
        logger.info("Turning on heating system with id {}", id);
        heatingSystemService.turnOn(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/turn-off")
    public ResponseEntity<Void> turnOff(@PathVariable("id") Long id) {
        logger.info("Turning off heating system with id {}", id);
        heatingSystemService.turnOff(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/set-temperature")
    public ResponseEntity<Void> setTargetTemperature(@PathVariable("id") Long id, @RequestParam double temperature) {
        logger.info("Setting target temperature to {} for heating system with id {}", temperature, id);
        heatingSystemService.setTargetTemperature(id, temperature);
        // TODO: Implement automatic temperature maintenance logic in the service layer
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/current-temperature")
    public ResponseEntity<Double> getCurrentTemperature(@PathVariable("id") Long id) {
        logger.info("Fetching current temperature for heating system with id {}", id);
        return ResponseEntity.ok(heatingSystemService.getCurrentTemperature(id));
    }

}
