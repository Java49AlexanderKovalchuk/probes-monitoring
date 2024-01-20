package telran.probes;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.model.ProbeDataDoc;
import telran.probes.repo.AvgPopulatorRepo;

@SpringBootApplication	
@RequiredArgsConstructor
@Slf4j
public class AvgPopulatorAppl {
	@Autowired
	final AvgPopulatorRepo avgRepo;
	@Autowired
	final ProbeDataDoc probeDoc;
	public static void main(String[] args) {
		SpringApplication.run(AvgPopulatorAppl.class, args);

	}
	@Bean
	public Consumer<ProbeData> consumerProbeData() {
		return this::consumeMethod;
	}
	void consumeMethod(ProbeData probeData) {
		log.trace("received probe: {}", probeData);
		avgRepo.deleteAll(); // ???
		avgRepo.save(probeDoc.of(probeData));
	}

}
