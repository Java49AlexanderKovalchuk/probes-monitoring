package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import telran.probes.dto.ProbeData;
import telran.probes.dto.ProbeDataDeviation;
import telran.probes.dto.SensorRange;
import telran.probes.service.SensorRangeProviderService;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class AnalyzerControllerTest {
	private static final long SENSOR_ID = 123l;
	private static final float MIN_VALUE_NO_DEVIATION = 10;
	private static final float MAX_VALUE_NO_DEVIATION = 100;
	private static final float MIN_VALUE_DEVIATION = 60;
	private static final float MAX_VALUE_DEVIATION = 40;
	private static final SensorRange SENSOR_RANGE_NO_DEVIATION =
			new SensorRange(MIN_VALUE_NO_DEVIATION, MAX_VALUE_NO_DEVIATION );
	private static final SensorRange SENSOR_RANGE_MIN_DEVIATION =
			new SensorRange(MIN_VALUE_DEVIATION, MAX_VALUE_NO_DEVIATION );
	private static final SensorRange SENSOR_RANGE_MAX_DEVIATION =
			new SensorRange(MIN_VALUE_DEVIATION, MAX_VALUE_DEVIATION );
	private static final float VALUE = 50f;
	static final ProbeDataDeviation DATA_MIN_DEVIATION = new ProbeDataDeviation(SENSOR_ID, VALUE, VALUE - MIN_VALUE_DEVIATION, 0);
	
	static final ProbeData probeData = new ProbeData(SENSOR_ID, VALUE, 0);
	ObjectMapper mapper = new ObjectMapper();
	@MockBean
	AmazonSNS snsClient;
	@Autowired
InputDestination producer;
	
	
	String bindingNameConsumer="consumerProbeData-in-0";
	
	@Value("${app.deviation.notification.topic.arn}")
	String awsTopic;
	@MockBean
	Consumer<String> configChangeConsumer;
	@MockBean
	SensorRangeProviderService providerService;
	boolean flSnsSending;
	String arn;
	String message;
	String subject;
	@Test
	void noDeviationTest() {
		when(providerService.getSensorRange(SENSOR_ID))
		.thenReturn(SENSOR_RANGE_NO_DEVIATION);
		when(snsClient.publish(anyString(), anyString(), anyString()))
		.then(new Answer<PublishResult>() {

			@Override
			public PublishResult answer(InvocationOnMock invocation) throws Throwable {
				fail("no publishing must be");
				return null;
			}
		});
		producer.send(new GenericMessage<ProbeData>(probeData), bindingNameConsumer);
		
	}
	@Test
	void minDeviationTest() throws Exception {
		flSnsSending = false;
		when(providerService.getSensorRange(SENSOR_ID))
		.thenReturn(SENSOR_RANGE_MIN_DEVIATION);
		when(snsClient.publish(anyString(), anyString(), anyString()))
		.then(new Answer<PublishResult>() {

			@Override
			public PublishResult answer(InvocationOnMock invocation) throws Throwable {
				arn = invocation.getArgument(0);
				 message = invocation.getArgument(1);
				 subject = invocation.getArgument(2);
				
				flSnsSending=true;
				return new PublishResult();
			}
		});
		producer.send(new GenericMessage<ProbeData>(probeData), bindingNameConsumer);
		assertTrue(flSnsSending);
		assertEquals(awsTopic, arn);
		assertTrue(message.contains("" + SENSOR_ID));
		assertTrue(message.contains("" + VALUE));
		assertTrue(message.contains("" + (VALUE - MIN_VALUE_DEVIATION)));
		assertTrue(subject.contains("" + SENSOR_ID));
		
		
		
	}

}