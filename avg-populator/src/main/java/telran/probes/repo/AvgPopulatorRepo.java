package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.probes.model.ProbeDataDoc;

public interface AvgPopulatorRepo extends MongoRepository<ProbeDataDoc, Long>{

}
