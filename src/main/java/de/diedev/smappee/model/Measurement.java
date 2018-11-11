package de.diedev.smappee.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.diedev.smappee.model.serialization.MeasurementSerializer;

@Table(name = "t1")
@Entity
@JsonSerialize(using = MeasurementSerializer.class)
public class Measurement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "meas_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@NotNull
	private final ZonedDateTime zonedDateTime;

	@Column(name = "power")
	@NotNull
	private final int power;

	public Measurement(final ZonedDateTime dateWithTz, final int power) {
		this.zonedDateTime = dateWithTz;
		this.power = power;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "[" + id + "] at " + zonedDateTime + ", power: " + power;
	}
}
