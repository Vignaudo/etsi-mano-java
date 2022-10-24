package com.ubiqube.etsi.mano.dao.mano.cnf;

public enum ConnectionType {
	OCI("OCI"),
	MAVEN2("MAVEN2"),
	HELM("HELM");

	private String value;

	ConnectionType(final String string) {
		value = string;
	}

	@Override
	public String toString() {
		return value;
	}

}
