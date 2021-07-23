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
package com.ubiqube.etsi.mano.service.plan.contributors.v2;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.ChangeType;
import com.ubiqube.etsi.mano.dao.mano.Instance;
import com.ubiqube.etsi.mano.dao.mano.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.ScaleInfo;
import com.ubiqube.etsi.mano.dao.mano.VnfCompute;
import com.ubiqube.etsi.mano.dao.mano.VnfInstance;
import com.ubiqube.etsi.mano.dao.mano.VnfLiveInstance;
import com.ubiqube.etsi.mano.dao.mano.VnfPackage;
import com.ubiqube.etsi.mano.dao.mano.v2.Blueprint;
import com.ubiqube.etsi.mano.dao.mano.v2.ComputeTask;
import com.ubiqube.etsi.mano.dao.mano.v2.PlanOperationType;
import com.ubiqube.etsi.mano.dao.mano.v2.PlanStatusType;
import com.ubiqube.etsi.mano.dao.mano.v2.VnfBlueprint;
import com.ubiqube.etsi.mano.jpa.VnfLiveInstanceJpa;
import com.ubiqube.etsi.mano.orchestrator.Bundle;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Compute;
import com.ubiqube.etsi.mano.service.VnfInstanceService;
import com.ubiqube.etsi.mano.service.graph.VduNamingStrategy;
import com.ubiqube.etsi.mano.service.graph.VnfBundleAdapter;
import com.ubiqube.etsi.mano.service.plan.ScalingStrategy;
import com.ubiqube.etsi.mano.service.plan.ScalingStrategy.NumberOfCompute;
import com.ubiqube.etsi.mano.service.plan.contributors.v2.vt.ComputeVt;

@Service
public class VnfV2ComputeContributor extends AbstractContributorV2Base<ComputeTask, ComputeVt, VnfBlueprint> {
	private final ScalingStrategy scalingStrategy;
	private final VduNamingStrategy vduNamingStrategy;
	private final VnfInstanceService vnfInstanceService;
	private final VnfLiveInstanceJpa vnfLiveInstanceJpa;

	public VnfV2ComputeContributor(final ScalingStrategy _scalingStrategy, final VduNamingStrategy _vduNamingStrategy, final VnfInstanceService _vnfInstanceService, final VnfLiveInstanceJpa _vnfLiveInstanceJpa) {
		scalingStrategy = _scalingStrategy;
		vduNamingStrategy = _vduNamingStrategy;
		vnfInstanceService = _vnfInstanceService;
		vnfLiveInstanceJpa = _vnfLiveInstanceJpa;
	}

	@Override
	public List<ComputeVt> contribute(final Bundle bundle, final VnfBlueprint blueprint) {
		if (blueprint.getOperation() == PlanOperationType.TERMINATE) {
			return doTerminatePlan(blueprint.getVnfInstance());
		}
		final VnfPackage vnfPackage = ((VnfBundleAdapter) bundle).getVnfPackage();
		final Instance vnfInstance = vnfInstanceService.findById(blueprint.getInstance().getId());
		final Set<ScaleInfo> scaling = merge(blueprint, vnfInstance);
		final List<ComputeVt> ret = new ArrayList<>();
		vnfPackage.getVnfCompute().forEach(x -> {
			final NumberOfCompute numInst = scalingStrategy.getNumberOfCompute(blueprint, vnfPackage, scaling, x);
			if (numInst.getCurrent() < numInst.getWanted()) {
				ret.addAll(addInstance(x, blueprint, numInst.getScaleInfo(), numInst));
			} else if (numInst.getCurrent() > numInst.getWanted()) {
				ret.addAll(removeInstance(x, blueprint, numInst.getScaleInfo(), numInst));
			}
		});
		return ret;
	}

	private List<ComputeVt> doTerminatePlan(final VnfInstance vnfInstance) {
		final List<VnfLiveInstance> instances = vnfLiveInstanceJpa.findByVnfInstanceIdAndClass(vnfInstance, ComputeTask.class.getSimpleName());
		return instances.stream().map(x -> {
			final ComputeTask computeTask = new ComputeTask();
			computeTask.setChangeType(ChangeType.REMOVED);
			computeTask.setStatus(PlanStatusType.NOT_STARTED);
			computeTask.setType(ResourceTypeEnum.COMPUTE);
			computeTask.setToscaName(x.getTask().getToscaName());
			computeTask.setAlias(x.getTask().getAlias());
			computeTask.setVimResourceId(x.getResourceId());
			computeTask.setVimConnectionId(x.getVimConnectionId());
			computeTask.setRemovedVnfLiveInstance(x.getId());
			computeTask.setVnfCompute(((ComputeTask) x.getTask()).getVnfCompute());
			return new ComputeVt(computeTask);
		}).collect(Collectors.toList());
	}

	private List<ComputeVt> removeInstance(final VnfCompute vnfCompute, final VnfBlueprint plan, final ScaleInfo scaleInfo, final NumberOfCompute numInst) {
		final int toDelete = numInst.getCurrent() - numInst.getWanted();
		final Deque<VnfLiveInstance> instantiated = vnfInstanceService.getLiveComputeInstanceOf(plan, vnfCompute);
		final List<ComputeVt> ret = new ArrayList<>();
		for (int i = 0; i < toDelete; i++) {
			final ComputeTask computeTask = new ComputeTask();
			final VnfLiveInstance inst = instantiated.pop();
			computeTask.setVnfCompute(vnfCompute);
			computeTask.setChangeType(ChangeType.REMOVED);
			computeTask.setStatus(PlanStatusType.NOT_STARTED);
			computeTask.setType(ResourceTypeEnum.COMPUTE);
			computeTask.setScaleInfo(scaleInfo);
			computeTask.setToscaName(vnfCompute.getToscaName());
			computeTask.setAlias(inst.getTask().getAlias());
			computeTask.setVimResourceId(inst.getResourceId());
			computeTask.setVimConnectionId(inst.getVimConnectionId());
			computeTask.setRemovedVnfLiveInstance(inst.getId());
			ret.add(new ComputeVt(computeTask));
		}
		return ret;
	}

	private List<ComputeVt> addInstance(final VnfCompute vnfCompute, final VnfBlueprint plan, final ScaleInfo scaleInfo, final NumberOfCompute numInst) {
		final int toCreate = numInst.getWanted() - numInst.getCurrent();
		final List<ComputeVt> ret = new ArrayList<>();
		for (int i = 0; i < toCreate; i++) {
			final ComputeTask computeTask = new ComputeTask();
			computeTask.setVnfCompute(vnfCompute);
			computeTask.setChangeType(ChangeType.ADDED);
			computeTask.setStatus(PlanStatusType.NOT_STARTED);
			computeTask.setType(ResourceTypeEnum.COMPUTE);
			computeTask.setScaleInfo(scaleInfo);
			computeTask.setAlias(vduNamingStrategy.nameVdu(plan, vnfCompute.getToscaName(), numInst.getCurrent() + i));
			computeTask.setToscaName(vnfCompute.getToscaName());
			ret.add(new ComputeVt(computeTask));
		}
		return ret;
	}

	private static Set<ScaleInfo> merge(final Blueprint plan, final Instance vnfInstance) {
		final Set<ScaleInfo> tmp = vnfInstance.getInstantiatedVnfInfo().getScaleStatus().stream().filter(x -> notIn(x.getAspectId(), plan.getParameters().getScaleStatus())).map(x -> new ScaleInfo(x.getAspectId(), x.getScaleLevel())).collect(Collectors.toSet());
		tmp.addAll(plan.getParameters().getScaleStatus());
		return tmp;
	}

	private static boolean notIn(final String aspectId, final Set<? extends ScaleInfo> scaleInfos) {
		return scaleInfos.stream().noneMatch(x -> x.getAspectId().equals(aspectId));
	}

	@Override
	public Class<? extends Node> getNode() {
		return Compute.class;
	}

}
