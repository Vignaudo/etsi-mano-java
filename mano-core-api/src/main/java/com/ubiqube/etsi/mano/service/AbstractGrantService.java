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
package com.ubiqube.etsi.mano.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.dao.mano.BlueZoneGroupInformation;
import com.ubiqube.etsi.mano.dao.mano.ChangeType;
import com.ubiqube.etsi.mano.dao.mano.GrantInformationExt;
import com.ubiqube.etsi.mano.dao.mano.GrantResponse;
import com.ubiqube.etsi.mano.dao.mano.GrantVimAssetsEntity;
import com.ubiqube.etsi.mano.dao.mano.Instance;
import com.ubiqube.etsi.mano.dao.mano.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.VimComputeResourceFlavourEntity;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.VimSoftwareImageEntity;
import com.ubiqube.etsi.mano.dao.mano.VimTask;
import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionType;
import com.ubiqube.etsi.mano.dao.mano.common.GeoPoint;
import com.ubiqube.etsi.mano.dao.mano.v2.Blueprint;
import com.ubiqube.etsi.mano.dao.mano.v2.ComputeTask;
import com.ubiqube.etsi.mano.exception.NotFoundException;
import com.ubiqube.etsi.mano.jpa.ConnectionInformationJpa;
import com.ubiqube.etsi.mano.service.vim.VimManager;

import ma.glasnost.orika.MapperFacade;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public abstract class AbstractGrantService implements VimResourceService {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractGrantService.class);

	private final MapperFacade mapper;

	private final ResourceAllocate nfvo;

	private final VimManager vimManager;

	private final ConnectionInformationJpa connectionJpa;

	protected AbstractGrantService(final MapperFacade mapper, final ResourceAllocate nfvo, final VimManager vimManager, final ConnectionInformationJpa connectionJpa) {
		this.mapper = mapper;
		this.nfvo = nfvo;
		this.vimManager = vimManager;
		this.connectionJpa = connectionJpa;
	}

	@Override
	public final void allocate(final Blueprint<? extends VimTask, ? extends Instance> plan) {
		final GrantResponse grantRequest = mapper.map(plan, GrantResponse.class);
		final Predicate<? super VimTask> isManoClass = x -> (x.getType() == ResourceTypeEnum.COMPUTE) ||
				(x.getType() == ResourceTypeEnum.LINKPORT) ||
				(x.getType() == ResourceTypeEnum.STORAGE) ||
				(x.getType() == ResourceTypeEnum.VL);
		plan.getTasks().stream()
				.filter(isManoClass)
				.map(VimTask.class::cast)
				.forEach(x -> {
					if (x.getChangeType() == ChangeType.ADDED) {
						final GrantInformationExt obj = mapper.map(x, GrantInformationExt.class);
						obj.setResourceTemplateId(x.getToscaName());
						grantRequest.addAddResources(obj);
					} else {
						final GrantInformationExt obj = mapper.map(x, GrantInformationExt.class);
						obj.setResourceId(Objects.requireNonNull(x.getVimResourceId()));
						obj.setVimConnectionId(Objects.requireNonNull(x.getVimConnectionId()));
						grantRequest.addRemoveResources(obj);
					}
				});
		final GrantResponse returnedGrant = nfvo.sendSyncGrantRequest(grantRequest);
		// Merge resources.
		Optional.ofNullable(returnedGrant.getAddResources()).orElseGet(LinkedHashSet::new)
				.forEach(x -> {
					// Get VNFM Grant Resource information ID.
					final UUID grantUuid = UUID.fromString(x.getResourceDefinitionId());
					final VimTask task = findTask(plan, grantUuid);
					task.setVimReservationId(x.getReservationId());
					task.setResourceGroupId(x.getResourceGroupId());
					task.setZoneId(x.getZoneId());
					task.setResourceProviderId(x.getResourceProviderId());
					task.setVimConnectionId(x.getVimConnectionId());
				});
		returnedGrant.getVimConnections().forEach(plan::addVimConnection);
		plan.setZoneGroups(mapper.mapAsSet(returnedGrant.getZoneGroups(), BlueZoneGroupInformation.class));
		plan.setZones(returnedGrant.getZones());
		plan.addExtManagedVirtualLinks(returnedGrant.getExtManagedVirtualLinks());
		Optional.ofNullable(returnedGrant.getExtVirtualLinks()).ifPresent(plan::addExtVirtualLinks);
		plan.setGrantsRequestId(returnedGrant.getId().toString());
		mapVimAsset(plan.getTasks(), returnedGrant.getVimAssets());
		fixUnknownTask(plan.getTasks(), plan.getVimConnections());
		plan.setVimConnections(fixVimConnections(plan.getVimConnections()));
		fixContainerBefore431(plan);
		check(plan);
	}

	private void fixContainerBefore431(final Blueprint<? extends VimTask, ? extends Instance> plan) {
		if (plan.getTasks().stream().anyMatch(x -> x.getType() == ResourceTypeEnum.HELM)) {
			fixGenericConnection(plan, ConnectionType.HELM, plan::setMciopConnectionInfo);
		}
		if (plan.getTasks().stream().anyMatch(x -> x.getType() == ResourceTypeEnum.OS_CONTAINER)) {
			fixGenericConnection(plan, ConnectionType.OCI, plan::setCirConnectionInfo);
		}
	}

	private void fixGenericConnection(final Blueprint<? extends VimTask, ? extends Instance> plan, final ConnectionType type, final Consumer<Map<String, ConnectionInformation>> cons) {
		final Map<String, ConnectionInformation> cirConn = plan.getCirConnectionInfo();
		if ((null == cirConn) || cirConn.isEmpty()) {
			return;
		}
		final List<ConnectionInformation> cirs = connectionJpa.findByConnType(type);
		if (cirs.isEmpty()) {
			LOG.warn("No {} declared in VNFM", type);
		}
		final Map<String, ConnectionInformation> map = cirs.stream().collect(Collectors.toMap(ConnectionInformation::getName, Function.identity()));
		cons.accept(map);
	}

	protected abstract void check(Blueprint<? extends VimTask, ? extends Instance> plan);

	private Set<VimConnectionInformation> fixVimConnections(final Set<VimConnectionInformation> vimConnections) {
		return vimConnections.stream().map(x -> {
			x.setGeoloc(new GeoPoint(10, 10));
			return vimManager.registerIfNeeded(x);
		}).collect(Collectors.toSet());
	}

	private static void fixUnknownTask(final Set<? extends VimTask> tasks, final Set<VimConnectionInformation> vimConnections) {
		final VimConnectionInformation vimConn = vimConnections.iterator().next();
		tasks.stream()
				.filter(x -> x.getVimConnectionId() == null)
				.forEach(x -> x.setVimConnectionId(vimConn.getVimId()));
	}

	private static void mapVimAsset(final Set<? extends VimTask> tasks, final GrantVimAssetsEntity vimAssets) {
		tasks.stream()
				.filter(ComputeTask.class::isInstance)
				.filter(x -> x.getChangeType() != ChangeType.REMOVED)
				.map(ComputeTask.class::cast)
				.forEach(x -> {
					x.setFlavorId(findFlavor(vimAssets, x.getVnfCompute().getToscaName()));
					x.setImageId(findImage(vimAssets, x.getVnfCompute().getSoftwareImage().getName()));
				});
	}

	private static String findImage(final GrantVimAssetsEntity vimAssets, final String imageName) {
		return vimAssets.getSoftwareImages().stream()
				.filter(x -> x.getVnfdSoftwareImageId().equals(imageName))
				.map(VimSoftwareImageEntity::getVimSoftwareImageId)
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Could not find Image for vdu: " + imageName));
	}

	private static String findFlavor(final GrantVimAssetsEntity vimAssets, final String vduId) {
		return vimAssets.getComputeResourceFlavours().stream()
				.filter(x -> x.getVnfdVirtualComputeDescId().equals(vduId))
				.map(VimComputeResourceFlavourEntity::getVimFlavourId)
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Could not find flavor for vdu: " + vduId));
	}

	private static VimTask findTask(final Blueprint<? extends VimTask, ? extends Instance> plan, final UUID grantUuid) {
		return plan.getTasks().stream()
				.filter(x -> x.getId().compareTo(grantUuid) == 0)
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Could not find task: " + grantUuid));
	}
}
