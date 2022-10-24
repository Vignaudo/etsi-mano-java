package com.ubiqube.etsi.mano.service.vim;

import java.net.URI;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionInformation;
import com.ubiqube.etsi.mano.jpa.ConnectionInformationJpa;
import com.ubiqube.etsi.mano.service.rest.FluxRest;

@Service
public class CirConnectionManager {

	private ConnectionInformationJpa cirConnectionInformationJpa;

	public ConnectionInformation register(final ConnectionInformation vci) {
		checkConnectivity(vci);
		return save(vci);
	}

	public void deleteVim(final UUID id) {
		cirConnectionInformationJpa.deleteById(id);
	}

	public ConnectionInformation findVimById(final UUID id) {
		return cirConnectionInformationJpa.findById(id).orElseThrow();
	}

	public ConnectionInformation save(final ConnectionInformation vim) {
		return cirConnectionInformationJpa.save(vim);
	}

	public Iterable<ConnectionInformation> findAll() {
		return cirConnectionInformationJpa.findAll();
	}

	@SuppressWarnings("static-method")
	public boolean checkConnectivity(final ConnectionInformation vci) {
		final FluxRest fr = FluxRest.of(vci);
		final URI url = URI.create(vci.getUrl());
		fr.get(url, String.class, null);
		return true;
	}

	public void checkConnectivity(final UUID id) {
		final ConnectionInformation vci = findVimById(id);
		checkConnectivity(vci);
	}

}
