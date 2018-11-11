package de.diedev.smappee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.diedev.smappee.model.Measurement;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

	// public List<Something> findByType(String type);

}
