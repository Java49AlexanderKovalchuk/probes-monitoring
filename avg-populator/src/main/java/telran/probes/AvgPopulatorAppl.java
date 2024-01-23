package telran.probes;

import java.time.Instant;
import java.time.ZoneId;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.repo.AvgPopulatorRepo;
import telran.probes.model.*;

@SpringBootApplication	
@Slf4j
public class AvgPopulatorAppl {
	@Autowired
	AvgPopulatorRepo avgRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(AvgPopulatorAppl.class, args);

	}
	
	@Bean
	public Consumer<ProbeData> consumerProbeData() {
		return this::consumeMethod;
	}
	void consumeMethod(ProbeData probeData) {
		log.debug("received probe: {}", probeData);
	    ProbeDataDoc dataDoc = new ProbeDataDoc(probeData.sensorId(), 
	    		Instant.ofEpochMilli(probeData.timestamp())
	    			.atZone(ZoneId.systemDefault()).toLocalDateTime(), 
	    			probeData.value());
		
		log.trace("data saving: {}", dataDoc );
		avgRepo.save(dataDoc);
		
	}

}
