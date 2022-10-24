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
package com.ubiqube.etsi.mano.service.event;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.ubiqube.etsi.mano.dao.mano.GrantResponse;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.VnfCompute;
import com.ubiqube.etsi.mano.dao.mano.VnfStorage;
import com.ubiqube.etsi.mano.dao.mano.pkg.OsContainer;
import com.ubiqube.etsi.mano.service.vim.Vim;

/**
 *
 * @author olivier
 *
 */
public interface GrantSupport {
	@Nonnull
	Set<VnfCompute> getVnfCompute(UUID objectId);

	@Nonnull
	Set<VnfStorage> getVnfStorage(UUID objectId);

	Set<OsContainer> getOsContainer(UUID objectId);

	List<VimConnectionInformation> getVims(GrantResponse grants);

	void getUnmanagedNetworks(GrantResponse grants, Vim vim, VimConnectionInformation vimInfo);

	UUID convertVnfdToId(String vnfdId);
}
