/**
 *     Copyright (C) 2019-2023 Ubiqube.
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.service.event;

import com.ubiqube.etsi.mano.service.rest.ServerAdapter;
import com.ubiqube.etsi.mano.service.rest.model.AuthentificationInformations;

/**
 * This class handle the notification callback.
 * <ul>
 * <li>Building the HTTP client.
 * <li>Crafting the request.
 * <li>Sending the request.
 * <li>Interpreting the result.
 * </ul>
 * This class should be compatible with Basic, OAuth2, TLS CERT
 * authentification. One the possiblities for OAuth authentification is group:
 * 'net.oauth.core', name: 'oauth-httpclient4', version: '20090913' you may also
 * need this: http://www.codedq.net/blog/articles/146.html
 *
 * @author ovi@ubiqube.com
 *
 */
public interface Notifications {

	/**
	 * Send a notification Object to the _uri
	 *
	 * @param obj    The JSON Onject.
	 * @param uri    The complete URL.
	 * @param server A Servers object.
	 */
	void doNotification(final Object obj, final String uri, final ServerAdapter server);

	void check(ServerAdapter server, final String _uri);

	void check(AuthentificationInformations authentication, String callbackUri);

}
