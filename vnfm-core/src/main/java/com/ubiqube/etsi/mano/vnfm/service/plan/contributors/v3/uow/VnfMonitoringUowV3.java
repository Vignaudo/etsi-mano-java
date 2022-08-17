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
package com.ubiqube.etsi.mano.vnfm.service.plan.contributors.v3.uow;

import java.util.List;

import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.v2.MonitoringTask;
import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Monitoring;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.vnfm.service.VnfMonitoringService;

public class VnfMonitoringUowV3 extends AbstractVnfmUowV3<MonitoringTask> {
	private final VnfMonitoringService vnfMonitoringService;
	private final VimConnectionInformation vimConnectionInformation;
	private final MonitoringTask task;

	public VnfMonitoringUowV3(final VirtualTaskV3<MonitoringTask> task, final VnfMonitoringService vnfMonitoringService, final VimConnectionInformation vimConnectionInformation) {
		super(task, Monitoring.class);
		this.vnfMonitoringService = vnfMonitoringService;
		this.vimConnectionInformation = vimConnectionInformation;
		this.task = task.getTemplateParameters();
	}

	@Override
	public String execute(final Context3d context) {
		final List<String> l = context.getParent(Compute.class, task.getVnfCompute().getToscaName());
		final String instanceId = l.get(0);
		return vnfMonitoringService.registerMonitoring(instanceId, task.getMonitoringParams(), vimConnectionInformation);
	}

	@Override
	public String rollback(final Context3d context) {
		final MonitoringTask params = getTask().getTemplateParameters();
		vnfMonitoringService.unregister(params.getVimResourceId());
		return null;
	}

}
