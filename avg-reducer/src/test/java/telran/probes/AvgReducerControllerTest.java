package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.probes.dto.ProbeData;
import telran.probes.service.AvgValueService;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class AvgReducerControllerTest {

	private static final ProbeData PROBE_DATA = new ProbeData(123l, 50, 0);
	private static final Long AVG_VALUE = (long) 52;
	private static final Long NULL_AVG_VALUE = null;
	private static final ProbeData AVG_DATA = new ProbeData(123l, AVG_VALUE, 0);
	
	ObjectMapper mapper = new ObjectMapper();
	@Autowired
	InputDestination producer;
	@Autowired
	OutputDestination consumer;
	String bindingNameProducer = "avgValue-out-0";
	String bindingNameConsumer = "consumerProbeData-in-0";
	@MockBean
	AvgValueService avgValueService;
	@Test
	void nullAvgValueTest() {
		when(avgValueService.getAvgValue(PROBE_DATA)).thenReturn(NULL_AVG_VALUE);
		producer.send(new GenericMessage<ProbeData>(PROBE_DATA), bindingNameConsumer);
		Message<byte[]> message = consumer.receive(10, bindingNameProducer);
		assertNull(message);
	}

	@Test
	void normalAvgValueTest() throws Exception {
		when(avgValueService.getAvgValue(PROBE_DATA)).thenReturn(AVG_VALUE);
		producer.send(new GenericMessage<ProbeData>(PROBE_DATA), bindingNameConsumer);
		Message<byte[]> message = consumer.receive(10, bindingNameProducer);
		assertNotNull(message);
		System.out.println("control control control");
		ProbeData actualAvg = mapper.readValue(message.getPayload(), ProbeData.class);
		assertEquals(AVG_DATA, actualAvg);
	}

}
