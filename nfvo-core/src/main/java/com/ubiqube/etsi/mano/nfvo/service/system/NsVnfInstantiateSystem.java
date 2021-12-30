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
package com.ubiqube.etsi.mano.nfvo.service.system;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsVnfTask;
import com.ubiqube.etsi.mano.nfvo.service.graph.nfvo.VnfInstantiateUow;
import com.ubiqube.etsi.mano.orchestrator.OrchestrationService;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWork;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTask;
import com.ubiqube.etsi.mano.service.VnfmInterface;
import com.ubiqube.etsi.mano.service.system.AbstractVimSystem;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@Service
public class NsVnfInstantiateSystem extends AbstractVimSystem<NsVnfTask> {
	private final VnfmInterface vnfm;

	public NsVnfInstantiateSystem(final VnfmInterface vnfm) {
		super();
		this.vnfm = vnfm;
	}

	@Override
	public String getProviderId() {
		return "VNF";
	}

	@Override
	protected SystemBuilder<UnitOfWork<NsVnfTask>> getImplementation(final OrchestrationService<NsVnfTask> orchestrationService, final VirtualTask<NsVnfTask> virtualTask, final VimConnectionInformation vimConnectionInformation) {
		return orchestrationService.systemBuilderOf(new VnfInstantiateUow(virtualTask, vnfm));
	}

}