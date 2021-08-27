/**
 *     Copyright (C) 2019-2020 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.rest;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import com.ubiqube.etsi.mano.config.properties.ManoVnfmProperties;

@Service
public class NfvoRestImpl extends AbstractRest {
	private final String url;
	private final MultiValueMap<String, String> auth = new HttpHeaders();

	public NfvoRestImpl(final ManoVnfmProperties props) {
		url = props.getNfvoUrl();
		final String user = props.getNfvoUser();
		if (null != user) {
			final String password = Optional.of(props.getNfvoPassword()).orElse("");
			auth.add("Authorization", authBasic(user, password));
		}
		Assert.notNull(url, "nfvo.url is not declared in property file.");
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected MultiValueMap<String, String> getAutorization() {
		return auth;
	}

}
