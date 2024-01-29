package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import telran.probes.dto.SensorRange;
import telran.probes.service.SensorRangeProviderService;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AnalyzerServiceTest {
	String bindingNameConsumer = "consumerUpdatedRange-in-0";
	@Autowired
	InputDestination producer;
	@MockBean
	RestTemplate restTemplate;
	@Autowired
	SensorRangeProviderService providerService;
	static final long SENSOR_ID = 123l;
	private static final float MIN_VALUE = 10;
	private static final float MAX_VALUE = 20;
	
	static final long SENSOR_ID_NOT_EXIST = 125l;
	private static final float MIN_VALUE_UPDATED = 200;
	private static final float MAX_VALUE_UPDATED = 300;
	
	static final String DELIMITER = "#";
	static final String MESSAGE_UPDATE_RANGE = "range-update" + DELIMITER + SENSOR_ID;
	static final String RESPONSE_MESSAGE_NOT_FOUND = "sensor is not found";
	
	static final SensorRange SENSOR_RANGE = new SensorRange(MIN_VALUE, MAX_VALUE);
	final SensorRange SENSOR_RANGE_DEFAULT = new SensorRange(0, 100);
	final SensorRange SENSOR_RANGE_UPDATED = new SensorRange(MIN_VALUE_UPDATED, MAX_VALUE_UPDATED);
	
	@SuppressWarnings("unchecked")
	@Test
	@Order(1)
	void normalFlowWithNoMapData() {
		ResponseEntity<SensorRange> responseEntity = new ResponseEntity<SensorRange>(SENSOR_RANGE, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
				.thenReturn(responseEntity);
		SensorRange actual = providerService.getSensorRange(SENSOR_ID);
		assertEquals(SENSOR_RANGE, actual);
	}

	@Test
	@Order(2)
	void normalFlowWithMapData() {

		SensorRange actual = providerService.getSensorRange(SENSOR_ID);
		assertEquals(SENSOR_RANGE, actual);
	}

	@SuppressWarnings("unchecked")
	@Test
	@Order(3)
	void notFoundSensorTest() {
		ResponseEntity<String> responseEntityNotFound = new ResponseEntity<>(RESPONSE_MESSAGE_NOT_FOUND,
				HttpStatus.NOT_FOUND);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
				.thenReturn(responseEntityNotFound);
		SensorRange actual = providerService.getSensorRange(SENSOR_ID_NOT_EXIST);
		assertEquals(SENSOR_RANGE_DEFAULT, actual);
	}

	@SuppressWarnings("unchecked")
	@Test
	@Order(4)
	void errorOnServerTest() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
		.thenThrow(RestClientException.class);
		SensorRange actual = providerService.getSensorRange(SENSOR_ID_NOT_EXIST);
		assertEquals(SENSOR_RANGE_DEFAULT, actual);
	}

	//@SuppressWarnings("unchecked")
	@Test
	@Order(5)
	void normalFlowRangeUpdatedWithMap() throws Exception {
		ResponseEntity<SensorRange> responseEntityUpdated = new ResponseEntity<>(SENSOR_RANGE_UPDATED,
				HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
				.thenReturn(responseEntityUpdated);
		producer.send(new GenericMessage<String>(MESSAGE_UPDATE_RANGE), bindingNameConsumer);
		SensorRange actual = providerService.getSensorRange(SENSOR_ID);
		assertEquals(MIN_VALUE_UPDATED, actual.minValue());
	}

}
