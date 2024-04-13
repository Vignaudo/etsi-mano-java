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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ubiqube.etsi.mano.dao.mano.dto.VimConnectionInfoDto;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VimConnectionInformationMapping {
	@Mapping(target = "audit", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "tenantId", ignore = true)
	@Mapping(target = "version", ignore = true)
	VimConnectionInformation map(VimConnectionInfoDto o);

	default List<VimConnectionInformation> mapAsList(final List<VimConnectionInformation> vimConnectionInfo) {
		return vimConnectionInfo.stream().map(this::map).toList();
	}

	VimConnectionInformation map(VimConnectionInformation x);
}
