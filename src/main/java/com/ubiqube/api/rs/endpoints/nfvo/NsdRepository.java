package com.ubiqube.api.rs.endpoints.nfvo;

import java.util.UUID;

import com.ubiqube.api.ejb.nfvo.nsdManagement.NsDescriptorsNsdInfo;

public class NsdRepository extends AbstractGenericRepository<NsDescriptorsNsdInfo> {
	private final static String REPOSITORY_NVFO_NSD_DATAFILE_BASE_PATH = "Datafiles/NFVO/nsd";

	@Override
	String getUriForId(String _id) {
		return REPOSITORY_NVFO_NSD_DATAFILE_BASE_PATH + "/" + _id + "/nsd.json";
	}

	@Override
	String setId(NsDescriptorsNsdInfo _entity) {
		final String id = _entity.getId();
		if (null == id) {
			_entity.setId(UUID.randomUUID().toString());
		}

		return _entity.getId();
	}

	@Override
	Class getClazz() {
		return NsDescriptorsNsdInfo.class;
	}

}
