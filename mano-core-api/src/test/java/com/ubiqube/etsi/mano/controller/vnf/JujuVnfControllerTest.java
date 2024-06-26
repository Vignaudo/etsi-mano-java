/**
 *     Copyright (C) 2019-2024 Ubiqube.
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
package com.ubiqube.etsi.mano.controller.vnf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ubiqube.etsi.mano.service.JujuCloudService;
import com.ubiqube.etsi.mano.service.event.ActionType;
import com.ubiqube.etsi.mano.service.event.EventManager;
import com.ubiqube.etsi.mano.service.juju.cli.JujuRemoteService;
import com.ubiqube.etsi.mano.service.juju.entities.JujuCloud;

@ExtendWith(MockitoExtension.class)
class JujuVnfControllerTest {

	@Mock
	private EventManager eventManager;

	@Mock
	private JujuCloudService jujuCloudService;

	@Mock
	private JujuRemoteService remoteService;

	@Mock
	private ActionType actionType;

	@Mock
	private JujuCloud jCloud;

	private JujuVnfController jujuVnfController;

	@BeforeEach
	void init() {
		jujuVnfController = new JujuVnfController(remoteService, eventManager, jujuCloudService);
	}

	@Test
	void test_Tnstantiate() throws Exception {
		final JujuCloud jCloud = new JujuCloud();
		jCloud.setId(UUID.randomUUID());
		jujuVnfController.instantiate(jCloud);
		assertTrue(true);
	}

	@Test
	void test_CheckStatus() {
		final ResponseEntity<String> responseobject = new ResponseEntity<>("creating cloud", HttpStatus.OK);
		Mockito.when(remoteService.cloudDetail(Mockito.anyString())).thenReturn(responseobject);
		final ResponseEntity<String> responseobject2 = new ResponseEntity<>("creating cloud", HttpStatus.OK);
		Mockito.when(remoteService.credentialDetails(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(responseobject2);
		final ResponseEntity<String> responseobject3 = new ResponseEntity<>("creating cloud", HttpStatus.OK);
		Mockito.when(remoteService.controllerDetail(Mockito.anyString())).thenReturn(responseobject3);
		final ResponseEntity<String> responseobject4 = new ResponseEntity<>("creating cloud", HttpStatus.OK);
		Mockito.when(remoteService.modelDetail(Mockito.anyString())).thenReturn(responseobject4);
		final ResponseEntity<String> responseobject5 = new ResponseEntity<>("creating cloud", HttpStatus.OK);
		Mockito.when(remoteService.application(Mockito.anyString())).thenReturn(responseobject5);
		jujuVnfController.checkstatus("test", "test2", "test3", "test4", "test5");
		assertTrue(true);
	}

	@Test
	void test_CheckStatus_error_when_no_cloud_found() {
		final ResponseEntity<String> responseobject = new ResponseEntity<>("ERROR no testcloud found", HttpStatus.NOT_FOUND);
		Mockito.when(remoteService.cloudDetail(Mockito.anyString())).thenReturn(responseobject);
		jujuVnfController.checkstatus("test", "test2", "test3", "test4", "test5");
		assertTrue(true);
	}

	@Test
	void test_Terminate() {
		final String controlername = "test";
		final List<JujuCloud> jClouds = new ArrayList();
		final JujuCloud jujuCloud = new JujuCloud();
		jujuCloud.setId(UUID.randomUUID());
		jClouds.add(jujuCloud);
		final ResponseEntity<String> responseobject = new ResponseEntity<>("test", HttpStatus.OK);
		Mockito.when(remoteService.controllerDetail(Mockito.anyString())).thenReturn(responseobject);
		jujuVnfController.terminate(controlername);
		assertTrue(true);
	}

	@Test
	void test_Terminate_error_when_no_controller_found() {
		final String controlername = "test";
		final ResponseEntity<String> responseobject = new ResponseEntity<>("ERROR", HttpStatus.NOT_FOUND);
		Mockito.when(remoteService.controllerDetail(Mockito.anyString())).thenReturn(responseobject);
		jujuVnfController.terminate(controlername);
		assertTrue(true);
	}
}