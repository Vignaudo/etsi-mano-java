package com.ubiqube.etsi.mano.nfvo.v331.model.nslcm;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.ubiqube.etsi.mano.nfvo.v331.model.nslcm.CpProtocolData;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * This type represents the information related to a SAP of a NS. It shall comply with the provisions defined in Table 6.5.3.10-1. 
 */
@Schema(description = "This type represents the information related to a SAP of a NS. It shall comply with the provisions defined in Table 6.5.3.10-1. ")
@Validated


public class SapData   {
  @JsonProperty("sapdId")
  private String sapdId = null;

  @JsonProperty("sapName")
  private String sapName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("sapProtocolData")
  @Valid
  private List<CpProtocolData> sapProtocolData = null;

  public SapData sapdId(String sapdId) {
    this.sapdId = sapdId;
    return this;
  }

  /**
   * Get sapdId
   * @return sapdId
   **/
  @Schema(required = true, description = "")
      @NotNull

    public String getSapdId() {
    return sapdId;
  }

  public void setSapdId(String sapdId) {
    this.sapdId = sapdId;
  }

  public SapData sapName(String sapName) {
    this.sapName = sapName;
    return this;
  }

  /**
   * Human readable name for the SAP. 
   * @return sapName
   **/
  @Schema(required = true, description = "Human readable name for the SAP. ")
      @NotNull

    public String getSapName() {
    return sapName;
  }

  public void setSapName(String sapName) {
    this.sapName = sapName;
  }

  public SapData description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Human readable description for the SAP. 
   * @return description
   **/
  @Schema(required = true, description = "Human readable description for the SAP. ")
      @NotNull

    public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public SapData sapProtocolData(List<CpProtocolData> sapProtocolData) {
    this.sapProtocolData = sapProtocolData;
    return this;
  }

  public SapData addSapProtocolDataItem(CpProtocolData sapProtocolDataItem) {
    if (this.sapProtocolData == null) {
      this.sapProtocolData = new ArrayList<>();
    }
    this.sapProtocolData.add(sapProtocolDataItem);
    return this;
  }

  /**
   * Parameters for configuring the network protocols on the SAP. 
   * @return sapProtocolData
   **/
  @Schema(description = "Parameters for configuring the network protocols on the SAP. ")
      @Valid
    public List<CpProtocolData> getSapProtocolData() {
    return sapProtocolData;
  }

  public void setSapProtocolData(List<CpProtocolData> sapProtocolData) {
    this.sapProtocolData = sapProtocolData;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SapData sapData = (SapData) o;
    return Objects.equals(this.sapdId, sapData.sapdId) &&
        Objects.equals(this.sapName, sapData.sapName) &&
        Objects.equals(this.description, sapData.description) &&
        Objects.equals(this.sapProtocolData, sapData.sapProtocolData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sapdId, sapName, description, sapProtocolData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SapData {\n");
    
    sb.append("    sapdId: ").append(toIndentedString(sapdId)).append("\n");
    sb.append("    sapName: ").append(toIndentedString(sapName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    sapProtocolData: ").append(toIndentedString(sapProtocolData)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
