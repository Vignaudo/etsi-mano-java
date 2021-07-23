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
package com.ubiqube.etsi.mano.service.plan.contributors.v2.vt;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ubiqube.etsi.mano.dao.mano.v2.ComputeTask;
import com.ubiqube.etsi.mano.orchestrator.NamedDependency;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Storage;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class ComputeVt extends VnfVtBase<ComputeTask> {

	public ComputeVt(final ComputeTask nt) {
		super(nt);
	}

	@Override
	public List<NamedDependency> getNameDependencies() {
		final List<NamedDependency> ret = getParameters().getVnfCompute().getNetworks()
				.stream()
				.map(x -> new NamedDependency(Network.class, x))
				.collect(Collectors.toList());
		final List<NamedDependency> storages = getParameters().getVnfCompute().getStorages()
				.stream()
				.map(x -> new NamedDependency(Storage.class, x))
				.collect(Collectors.toList());
		ret.addAll(storages);
		return ret;
	}

	@Override
	public List<NamedDependency> getNamedProduced() {
		return Arrays.asList(new NamedDependency(Compute.class, getParameters().getToscaName()));
	}

	@Override
	public String getProviderId() {
		return "COMPUTE";
	}

}
