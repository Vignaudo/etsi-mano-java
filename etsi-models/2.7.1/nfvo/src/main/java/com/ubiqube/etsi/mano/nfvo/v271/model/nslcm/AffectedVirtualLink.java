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

package com.ubiqube.etsi.mano.nfvo.v271.model.nslcm;

import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * This type provides information about added, deleted, modified and temporary
 * VLs.
 */
@ApiModel(description = "This type provides information about added, deleted, modified and temporary VLs. ")
@Validated
public class AffectedVirtualLink {
	@JsonProperty("id")
	private String id = null;

	@JsonProperty("virtualLinkDescId")
	private String virtualLinkDescId = null;

	/**
	 * Signals the type of change. Permitted values: * ADDED * REMOVED * MODIFIED *
	 * TEMPORARY * LINK_PORT_ADDED * LINK_PORT_REMOVED For a temporary resource, an
	 * AffectedVirtualLink structure exists as long as the temporary resource
	 * exists.
	 */
	public enum ChangeTypeEnum {
		ADDED("ADDED"),

		REMOVED("REMOVED"),

		MODIFIED("MODIFIED"),

		TEMPORARY("TEMPORARY"),

		LINK_PORT_ADDED("LINK_PORT_ADDED"),

		LINK_PORT_REMOVED("LINK_PORT_REMOVED");

		private final String value;

		ChangeTypeEnum(final String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static ChangeTypeEnum fromValue(final String text) {
			for (final ChangeTypeEnum b : ChangeTypeEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	@JsonProperty("changeType")
	private ChangeTypeEnum changeType = null;

	@JsonProperty("networkResource")
	private ResourceHandle networkResource = null;

	@JsonProperty("metadata")
	private Map<String, Object> metadata = null;

	public AffectedVirtualLink id(final String id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 *
	 * @return id
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public AffectedVirtualLink virtualLinkDescId(final String virtualLinkDescId) {
		this.virtualLinkDescId = virtualLinkDescId;
		return this;
	}

	/**
	 * Get virtualLinkDescId
	 *
	 * @return virtualLinkDescId
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull

	public String getVirtualLinkDescId() {
		return virtualLinkDescId;
	}

	public void setVirtualLinkDescId(final String virtualLinkDescId) {
		this.virtualLinkDescId = virtualLinkDescId;
	}

	public AffectedVirtualLink changeType(final ChangeTypeEnum changeType) {
		this.changeType = changeType;
		return this;
	}

	/**
	 * Signals the type of change. Permitted values: * ADDED * REMOVED * MODIFIED *
	 * TEMPORARY * LINK_PORT_ADDED * LINK_PORT_REMOVED For a temporary resource, an
	 * AffectedVirtualLink structure exists as long as the temporary resource
	 * exists.
	 *
	 * @return changeType
	 **/
	@ApiModelProperty(required = true, value = "Signals the type of change. Permitted values: * ADDED * REMOVED * MODIFIED * TEMPORARY * LINK_PORT_ADDED * LINK_PORT_REMOVED For a temporary resource, an AffectedVirtualLink structure exists as long as the temporary resource exists. ")
	@NotNull

	public ChangeTypeEnum getChangeType() {
		return changeType;
	}

	public void setChangeType(final ChangeTypeEnum changeType) {
		this.changeType = changeType;
	}

	public AffectedVirtualLink networkResource(final ResourceHandle networkResource) {
		this.networkResource = networkResource;
		return this;
	}

	/**
	 * Get networkResource
	 *
	 * @return networkResource
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull

	@Valid
	public ResourceHandle getNetworkResource() {
		return networkResource;
	}

	public void setNetworkResource(final ResourceHandle networkResource) {
		this.networkResource = networkResource;
	}

	public AffectedVirtualLink metadata(final Map<String, Object> metadata) {
		this.metadata = metadata;
		return this;
	}

	/**
	 * Get metadata
	 *
	 * @return metadata
	 **/
	@ApiModelProperty(value = "")

	@Valid
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(final Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean equals(final java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		final AffectedVirtualLink affectedVirtualLink = (AffectedVirtualLink) o;
		return Objects.equals(this.id, affectedVirtualLink.id) &&
				Objects.equals(this.virtualLinkDescId, affectedVirtualLink.virtualLinkDescId) &&
				Objects.equals(this.changeType, affectedVirtualLink.changeType) &&
				Objects.equals(this.networkResource, affectedVirtualLink.networkResource) &&
				Objects.equals(this.metadata, affectedVirtualLink.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, virtualLinkDescId, changeType, networkResource, metadata);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("class AffectedVirtualLink {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    virtualLinkDescId: ").append(toIndentedString(virtualLinkDescId)).append("\n");
		sb.append("    changeType: ").append(toIndentedString(changeType)).append("\n");
		sb.append("    networkResource: ").append(toIndentedString(networkResource)).append("\n");
		sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(final java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
