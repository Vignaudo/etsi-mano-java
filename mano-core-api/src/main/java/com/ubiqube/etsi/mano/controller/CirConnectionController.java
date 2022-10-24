package com.ubiqube.etsi.mano.controller;

import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionInformation;
import com.ubiqube.etsi.mano.exception.PreConditionException;
import com.ubiqube.etsi.mano.service.Patcher;
import com.ubiqube.etsi.mano.service.vim.CirConnectionManager;

import ma.glasnost.orika.MapperFacade;

@RestController("/admin/cir")
public class CirConnectionController {
	private final MapperFacade mapper;
	private final CirConnectionManager vimManager;
	private final Patcher patcher;

	public CirConnectionController(final MapperFacade mapper, final CirConnectionManager vimManager, final Patcher patcher) {
		this.mapper = mapper;
		this.vimManager = vimManager;
		this.patcher = patcher;
	}

	@PostMapping(value = "/register")
	public ResponseEntity<ConnectionInformation> registerVim(@RequestBody final ConnectionInformation body) {
		final ConnectionInformation vci = vimManager.save(body);
		return ResponseEntity.ok(mapper.map(vci, ConnectionInformation.class));
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteVim(@PathVariable("id") final String id) {
		vimManager.deleteVim(UUID.fromString(id));
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(value = "/{id}")
	public ResponseEntity<ConnectionInformation> patchVim(@PathVariable("id") final UUID id, @Nullable @RequestBody final String body,
			@RequestHeader(name = HttpHeaders.IF_MATCH, required = false) final String ifMatch) {
		final ConnectionInformation vim = vimManager.findVimById(id);
		if ((ifMatch != null) && !(vim.getVersion() + "").equals(ifMatch)) {
			throw new PreConditionException(ifMatch + " does not match " + vim.getVersion());
		}
		patcher.patch(body, vim);
		final ConnectionInformation newVim = vimManager.save(vim);
		return ResponseEntity.ok(newVim);
	}

	@GetMapping(value = "/{id}/connect")
	public ResponseEntity<Boolean> connectVim(@PathVariable("id") final UUID id) {
		vimManager.checkConnectivity(id);
		return ResponseEntity.ok(true);
	}

	@GetMapping(value = "/vim")
	public ResponseEntity<Iterable<ConnectionInformation>> listVim() {
		final Iterable<ConnectionInformation> vci = vimManager.findAll();
		return ResponseEntity.ok(vci);
	}

}
