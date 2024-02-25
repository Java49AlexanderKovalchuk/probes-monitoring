package telran.probes;

import org.springframework.http.ResponseEntity;

public interface GatewayService {
	ResponseEntity<byte[]> proxyRouting(ProxyExchange<byte[]> proxyExchange, HttpServletRequest request){
}
