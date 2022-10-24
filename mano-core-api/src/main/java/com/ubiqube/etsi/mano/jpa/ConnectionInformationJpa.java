package com.ubiqube.etsi.mano.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionType;

public interface ConnectionInformationJpa extends CrudRepository<ConnectionInformation, UUID> {
	List<ConnectionInformation> findByConnType(ConnectionType type);

	ConnectionInformation findByName(String repository);

}
