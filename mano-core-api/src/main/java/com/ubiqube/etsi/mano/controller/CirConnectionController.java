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
package com.ubiqube.etsi.mano.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.dto.ConnectionInformationDto;
import com.ubiqube.etsi.mano.exception.PreConditionException;
import com.ubiqube.etsi.mano.service.Patcher;
import com.ubiqube.etsi.mano.service.mapping.CirConnectionControllerMapping;
import com.ubiqube.etsi.mano.service.vim.CirConnectionManager;

import jakarta.annotation.Nullable;

@RestController
@RequestMapping(value = "/admin/cir", produces = "application/json")
public class CirConnectionController {
	private final CirConnectionControllerMapping cirConnectionControllerMapping;
	private final CirConnectionManager cirManager;
	private final Patcher patcher;

	public CirConnectionController(final CirConnectionControllerMapping cirConnectionControllerMapping, final CirConnectionManager vimManager, final Patcher patcher) {
		this.cirConnectionControllerMapping = cirConnectionControllerMapping;
		this.cirManager = vimManager;
		this.patcher = patcher;
	}

	@PostMapping
	public ResponseEntity<ConnectionInformation> registerVim(@RequestBody final ConnectionInformationDto body) {
		final ConnectionInformation nvci = cirConnectionControllerMapping.map(body);
		final ConnectionInformation vci = cirManager.save(nvci);
		return ResponseEntity.ok(vci);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteVim(@PathVariable("id") final String id) {
		cirManager.deleteVim(UUID.fromString(id));
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(value = "/{id}")
	public ResponseEntity<ConnectionInformation> patchVim(@PathVariable("id") final UUID id, @Nullable @RequestBody final String body,
			@RequestHeader(name = HttpHeaders.IF_MATCH, required = false) @Nullable final String ifMatch) {
		final ConnectionInformation vim = cirManager.findVimById(id);
		if ((ifMatch != null) && !(vim.getVersion() + "").equals(ifMatch)) {
			throw new PreConditionException(ifMatch + " does not match " + vim.getVersion());
		}
		patcher.patch(body, vim);
		final ConnectionInformation newVim = cirManager.save(vim);
		return ResponseEntity.ok(newVim);
	}

	@GetMapping(value = "/{id}/connect")
	public ResponseEntity<Boolean> connectVim(@PathVariable final UUID id) {
		cirManager.checkConnectivity(id);
		return ResponseEntity.ok(true);
	}

	@GetMapping()
	public ResponseEntity<Iterable<ConnectionInformation>> listVim() {
		final Iterable<ConnectionInformation> vci = cirManager.findAll();
		return ResponseEntity.ok(vci);
	}

}
