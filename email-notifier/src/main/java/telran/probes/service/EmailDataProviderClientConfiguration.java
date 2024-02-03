package telran.probes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Configuration
@Getter
public class EmailDataProviderClientConfiguration {
	@Value("${app.sensor.email.provider.host:localhost}")
	String host;
	@Value("${app.sensor.email.provider.port:8282}")
	int port;
	@Value("${app.sensor.email.provider.url:/sensor/emails}")
	String url;
	@Value("${app.sensor.email.provider.default:name_default@gmail.com}")
	String[] emailsDefault;
	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
