package telran.probes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.probes.dto.ProbeData;
import telran.probes.model.ProbeDataDoc;
import telran.probes.repo.AvgPopulatorRepo;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class AvgPopulatorControllerTest {
	@Autowired 
	InputDestination producer;
	@Autowired
	AvgPopulatorRepo avgRepo;
	
	String bindingNameConsumer = "consumerProbeData-in-0";
		
	private static final ProbeData PROBE_DATA = new ProbeData(123l, 54, 0);
	@Test
	void poulatorTest() {
		avgRepo.deleteAll();
		producer.send(new GenericMessage<ProbeData>(PROBE_DATA), bindingNameConsumer);
		ProbeDataDoc actualDataDoc = avgRepo.findById(PROBE_DATA.sensorId()).orElse(null);
		assertNotNull(actualDataDoc);
		ProbeData actual = new ProbeData(actualDataDoc.getSensorId(), actualDataDoc.getValue(), 0);
		assertEquals(PROBE_DATA, actual);
		
	}

}
