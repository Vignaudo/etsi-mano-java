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
package com.ubiqube.etsi.mano.vnfm.service.plan.contributors.v3;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.ubiqube.etsi.mano.dao.mano.ChangeType;
import com.ubiqube.etsi.mano.dao.mano.VnfInstance;
import com.ubiqube.etsi.mano.dao.mano.VnfPackage;
import com.ubiqube.etsi.mano.dao.mano.v2.PlanOperationType;
import com.ubiqube.etsi.mano.dao.mano.v2.PlanStatusType;
import com.ubiqube.etsi.mano.dao.mano.v2.VnfBlueprint;
import com.ubiqube.etsi.mano.dao.mano.v2.VnfTask;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.TemplateExtractorV3;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.vnfm.jpa.VnfLiveInstanceJpa;

/**
 *
 * @author olivier
 *
 */
public abstract class AbstractVnfmContributorV3<U> implements TemplateExtractorV3<U, VnfBlueprint, VnfPackage> {
	protected final VnfLiveInstanceJpa vnfInstanceJpa;

	protected AbstractVnfmContributorV3(final VnfLiveInstanceJpa vnfInstanceJpa) {
		this.vnfInstanceJpa = vnfInstanceJpa;
	}

	protected static <U extends VnfTask> U createTask(final Supplier<U> newInstance) {
		final U task = newInstance.get();
		task.setStartDate(LocalDateTime.now());
		task.setChangeType(ChangeType.ADDED);
		task.setStatus(PlanStatusType.NOT_STARTED);
		return task;
	}

	protected int countLive(final VnfInstance vnfInstance, final Class<?> clazz, final String toscaName) {
		return vnfInstanceJpa.countByVnfInstanceIdAndClassAndToscaName(vnfInstance, clazz.getSimpleName(), toscaName);
	}

	protected SclableResources<U> create(final Class<? extends Node> clazz, final String toscaName, final int want, final U param, final VnfInstance inst, final VnfBlueprint parameters) {
		int c = 0;
		if (parameters.getOperation() != PlanOperationType.TERMINATE) {
			c = countLive(inst, clazz, toscaName);
		}
		return SclableResources.of(clazz, toscaName, c, want, param);
	}
}
