package telran.probes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

import lombok.Getter;

@Configuration
@Getter
public class SnsConfiguration {
	@Value("${app.deviation.notification.topic.arn}")
	String topicArn;
	@Bean
	AmazonSNS getSnsClient() {
		return AmazonSNSClient.builder().withRegion(Regions.US_EAST_1).build();
	}
}	