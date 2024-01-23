package telran.probes.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Document(collection = "probe_values")
@AllArgsConstructor

@Getter
public class ProbeDataDoc {

	@Id
	Long sensorId;
	LocalDateTime timestamp;
	Float value;

//	public ProbeDataDoc of(ProbeData probeData) {
//		return new ProbeDataDoc(probeData.sensorId(),
//				//LocalDateTime.ofInstant(Instant.ofEpochMilli(probeData.timestamp()), TimeZone.getDefault().toZoneId()),
//				Instant.ofEpochMilli(probeData.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
//				probeData.value());
//	}

//	private ProbeDataDoc(Long sensorId, LocalDateTime timestamp, Float value) {
//		this.sensorId = sensorId;
//		this.timestamp = timestamp;
//		this.value = value;
//	}
}
