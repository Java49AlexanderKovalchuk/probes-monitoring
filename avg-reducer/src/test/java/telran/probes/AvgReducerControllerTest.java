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

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import telran.probes.dto.ProbeData;
import telran.probes.service.AvgValueService;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class AvgReducerControllerTest {

	static final ProbeData probeData = new ProbeData(123l, 50, 0);
	static final Long avgValue = (long) 52;
	static final Long nullAvgValue = null;
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
		when(avgValueService.getAvgValue(probeData)).thenReturn(nullAvgValue);
		producer.send(new GenericMessage<ProbeData>(probeData), bindingNameConsumer);
		Message<byte[]> message = consumer.receive(10, bindingNameProducer);
		assertNull(message);
	}

	@Test
	void normalAvgValueTest() throws Exception {
		when(avgValueService.getAvgValue(probeData)).thenReturn(avgValue);
		producer.send(new GenericMessage<ProbeData>(probeData), bindingNameConsumer);
		Message<byte[]> message = consumer.receive(10, bindingNameProducer);
		assertNotNull(message);
		Long actualAvg = mapper.readValue(message.getPayload(), Long.class);
		assertEquals(avgValue, actualAvg);
	}

}
