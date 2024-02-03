package telran.probes.service;

import java.net.URI;
import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDataProviderClientImpl implements EmailDataProviderClient {
	@Getter
	HashMap<Long, String[]> mapEmails = new HashMap<>();
	@Value("${app.update.message.delimiter:#}")
	String delimiter;
	@Value("${app.update.token.emails:emails-update}")
	String emailsUpdateToken;
	final EmailDataProviderClientConfiguration providerConfiguration;
	final RestTemplate restTemplate;

	@Override
	public String[] getEmails(long sensorId) {
		String[] emails = mapEmails.get(sensorId);

		return emails == null ? getEmailsFromService(sensorId) : emails;
	}

	@Bean
	Consumer<String> configChangeConsumer() {
		return this::checkConfigurationUpdate;
	}

	void checkConfigurationUpdate(String message) {
		String[] tokens = message.split(delimiter);
		if (tokens[0].equals(emailsUpdateToken)) {
			updateEmails(tokens[1]);
		}
	}

	private void updateEmails(String sensorIdString) {
		long id = Long.parseLong(sensorIdString);
		if (mapEmails.containsKey(id)) {
			mapEmails.put(id, getEmailsFromService(id));
		}
	}

	private String[] getEmailsFromService(long id) {
		String[] res = null;
		try {
			ResponseEntity<?> responseEntity = 
				restTemplate.exchange(getFullUrl(id), HttpMethod.GET, null, String[].class);
				if(!responseEntity.getStatusCode().is2xxSuccessful()) {
					throw new Exception((String) responseEntity.getBody());
				}
			res = (String[])responseEntity.getBody();
			mapEmails.put(id, res);
		} catch (Exception e) {
			log.error("no sensor emails provided for sensor {}, reason: {}",
					id, e.getMessage());
			res = getDefaultEmails();
			log.warn("Taken default emails {}", res);
		}
		log.debug("Emails for sensor {} is {}", id, res);
		return res;
	}
	
	private String[] getDefaultEmails() {
		return providerConfiguration.emailsDefault;
	}
	private String getFullUrl(long id) {
		String res = String.format("http://%s:%d%s/%d",
				providerConfiguration.getHost(),
				providerConfiguration.getPort(),
				providerConfiguration.getUrl(),
				id);
		log.debug("url:{}", res);
		return res;
	}

	
}
