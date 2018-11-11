package de.diedev.smappee.model.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import de.diedev.smappee.model.Measurement;

public class MeasurementSerializer extends JsonSerializer<Measurement> {

	@Override
	public void serialize(final Measurement value, final JsonGenerator gen, final SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeStartObject();
		// gen.writeNumberField("amount", value.getAmount());
		gen.writeEndObject();
	}
}