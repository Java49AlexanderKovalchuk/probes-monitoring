package telran.probes.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import telran.probes.dto.ProbeData;

@Document(collection = "probe_values")
@Getter
public class ProbeDataDoc {

	@Id
	Long sensorId;
	LocalDateTime timestamp;
	Float value;

	public ProbeDataDoc of(ProbeData probeData) {
		return new ProbeDataDoc(probeData.sensorId(),
				LocalDateTime.ofInstant(Instant.ofEpochMilli(probeData.timestamp()), TimeZone.getDefault().toZoneId()),
				probeData.value());
	}

	private ProbeDataDoc(Long sensorId, LocalDateTime timestamp, Float value) {
		this.sensorId = sensorId;
		this.timestamp = timestamp;
		this.value = value;
	}
}
