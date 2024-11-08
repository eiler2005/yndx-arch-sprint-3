package ru.yandex.practicum.smarthome.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthome.entity.HeatingSystem;

@Repository
public interface HeatingSystemRepository extends JpaRepository<HeatingSystem, Long> {
}
