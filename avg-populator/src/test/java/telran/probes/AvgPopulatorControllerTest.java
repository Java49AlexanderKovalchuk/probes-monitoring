package telran.probes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.messaging.support.GenericMessage;

import telran.probes.dto.ProbeData;
import telran.probes.model.ProbeDataDoc;
import telran.probes.repo.AvgPopulatorRepo;

@SpringBootTest

class AvgPopulatorControllerTest {
	@Autowired 
	InputDestination producer;
	@Autowired
	AvgPopulatorRepo avgPepo;
	
	String bindingNameConsumer = "consumerProbeData-in-0";
		
	private static final ProbeData PROBE_DATA = new ProbeData(123l, 54, 0);
	@Test
	void poulatorTest() {
		producer.send(new GenericMessage<ProbeData>(PROBE_DATA), bindingNameConsumer);
		ProbeDataDoc actualData = avgPepo.findById(PROBE_DATA.sensorId()).orElseThrow();
				
		//float probeValue = probeDoc.getValue();
		System.out.println("actual data:" + actualData );
		
	}
}
