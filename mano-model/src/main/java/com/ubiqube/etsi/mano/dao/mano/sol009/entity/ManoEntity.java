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
package com.ubiqube.etsi.mano.dao.mano.sol009.entity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.ubiqube.etsi.mano.dao.mano.sol009.peers.PeerEntityEnum;

import lombok.Data;

@Data
@Entity
public class ManoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Enumerated(EnumType.STRING)
	private PeerEntityEnum type;

	@NotNull
	private String name;

	private String description;

	private String provider;

	private String softwareVersion;

	@ElementCollection
	private Map<String, String> softwareInfo;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ManoEntityComponent> manoEntityComponents;

	@OneToMany(cascade = CascadeType.ALL)
	private List<ManoService> manoServices;

	@Transient
	private ManoConfigurableParams manoConfigurableParams;

	private ManoEntityManoApplicationState manoApplicationState;

	@OneToOne(cascade = CascadeType.ALL)
	private NfvoSpecificInfo nfvoSpecificInfo;

	@OneToOne(cascade = CascadeType.ALL)
	private VnfmSpecificInfo vnfmSpecificInfo;

	@OneToOne(cascade = CascadeType.ALL)
	private VimSpecificInfo vimSpecificInfo;

	@OneToOne(cascade = CascadeType.ALL)
	private WimSpecificInfo wimSpecificInfo;

	@Transient
	private CismSpecificInfo cismSpecificInfo;

}