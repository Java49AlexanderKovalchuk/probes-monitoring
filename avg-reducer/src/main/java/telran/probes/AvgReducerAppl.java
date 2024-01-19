package telran.probes;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.service.AvgValueService;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class AvgReducerAppl {
	final AvgValueService avgReducer;
	final StreamBridge streamBreadge;
	@Value("${app.deviation.bindingName:avgValue-out-0}")
	String avgValueBindingName;
	public static void main(String[] args) {
		SpringApplication.run(AvgReducerAppl.class, args);
	}
	
	@Bean
	public Consumer<ProbeData> consumerProbeData() {
		return this::consumeMethod;
	}
	
	void consumeMethod(ProbeData probeData) {
		log.trace("received probe: {}", probeData);
		Long avgValue = avgReducer.getAvgValue(probeData);
		if (avgValue != null) {
			log.trace("average value: {}", avgValue);
			streamBreadge.send(avgValueBindingName, avgValue);
			log.trace("average value {} sent to {}", avgValue, avgValueBindingName);
		}
		
	}
	

}
