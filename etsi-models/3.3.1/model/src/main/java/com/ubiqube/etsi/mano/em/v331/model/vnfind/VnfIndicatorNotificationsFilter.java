package com.ubiqube.etsi.mano.em.v331.model.vnfind;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ubiqube.etsi.mano.em.v331.model.vnfind.VnfInstanceSubscriptionFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * This type represents a subscription filter for notifications related to VNF indicators. At a particular nesting level in the filter structure, the following applies: All attributes shall match in order for the filter to match (logical \&quot;and\&quot; between different filter attributes). If an attribute is an array, the attribute shall match if at least one of the values in the array matches (logical \&quot;or\&quot; between the values of one filter attribute). 
 */
@Schema(description = "This type represents a subscription filter for notifications related to VNF indicators. At a particular nesting level in the filter structure, the following applies: All attributes shall match in order for the filter to match (logical \"and\" between different filter attributes). If an attribute is an array, the attribute shall match if at least one of the values in the array matches (logical \"or\" between the values of one filter attribute). ")
@Validated


public class VnfIndicatorNotificationsFilter   {
  @JsonProperty("vnfInstanceSubscriptionFilter")
  private VnfInstanceSubscriptionFilter vnfInstanceSubscriptionFilter = null;

  /**
   * Match particular notification types. Permitted values: * VnfIndicatorValueChangeNotification * SupportedIndicatorsChangeNotification The permitted values of the \"notificationTypes\" attribute are spelled exactly as the names of the notification types to facilitate automated code generation systems. 
   */
  public enum NotificationTypesEnum {
    VNFINDICATORVALUECHANGENOTIFICATION("VnfIndicatorValueChangeNotification"),
    
    SUPPORTEDINDICATORSCHANGENOTIFICATION("SupportedIndicatorsChangeNotification");

    private String value;

    NotificationTypesEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static NotificationTypesEnum fromValue(String text) {
      for (NotificationTypesEnum b : NotificationTypesEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("notificationTypes")
  private NotificationTypesEnum notificationTypes = null;

  @JsonProperty("indicatorIds")
  @Valid
  private List<String> indicatorIds = null;

  public VnfIndicatorNotificationsFilter vnfInstanceSubscriptionFilter(VnfInstanceSubscriptionFilter vnfInstanceSubscriptionFilter) {
    this.vnfInstanceSubscriptionFilter = vnfInstanceSubscriptionFilter;
    return this;
  }

  /**
   * Get vnfInstanceSubscriptionFilter
   * @return vnfInstanceSubscriptionFilter
   **/
  @Schema(description = "")
  
    @Valid
    public VnfInstanceSubscriptionFilter getVnfInstanceSubscriptionFilter() {
    return vnfInstanceSubscriptionFilter;
  }

  public void setVnfInstanceSubscriptionFilter(VnfInstanceSubscriptionFilter vnfInstanceSubscriptionFilter) {
    this.vnfInstanceSubscriptionFilter = vnfInstanceSubscriptionFilter;
  }

  public VnfIndicatorNotificationsFilter notificationTypes(NotificationTypesEnum notificationTypes) {
    this.notificationTypes = notificationTypes;
    return this;
  }

  /**
   * Match particular notification types. Permitted values: * VnfIndicatorValueChangeNotification * SupportedIndicatorsChangeNotification The permitted values of the \"notificationTypes\" attribute are spelled exactly as the names of the notification types to facilitate automated code generation systems. 
   * @return notificationTypes
   **/
  @Schema(description = "Match particular notification types. Permitted values: * VnfIndicatorValueChangeNotification * SupportedIndicatorsChangeNotification The permitted values of the \"notificationTypes\" attribute are spelled exactly as the names of the notification types to facilitate automated code generation systems. ")
  
    public NotificationTypesEnum getNotificationTypes() {
    return notificationTypes;
  }

  public void setNotificationTypes(NotificationTypesEnum notificationTypes) {
    this.notificationTypes = notificationTypes;
  }

  public VnfIndicatorNotificationsFilter indicatorIds(List<String> indicatorIds) {
    this.indicatorIds = indicatorIds;
    return this;
  }

  public VnfIndicatorNotificationsFilter addIndicatorIdsItem(String indicatorIdsItem) {
    if (this.indicatorIds == null) {
      this.indicatorIds = new ArrayList<>();
    }
    this.indicatorIds.add(indicatorIdsItem);
    return this;
  }

  /**
   * Match particular VNF indicator identifiers. 
   * @return indicatorIds
   **/
  @Schema(description = "Match particular VNF indicator identifiers. ")
  
    public List<String> getIndicatorIds() {
    return indicatorIds;
  }

  public void setIndicatorIds(List<String> indicatorIds) {
    this.indicatorIds = indicatorIds;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VnfIndicatorNotificationsFilter vnfIndicatorNotificationsFilter = (VnfIndicatorNotificationsFilter) o;
    return Objects.equals(this.vnfInstanceSubscriptionFilter, vnfIndicatorNotificationsFilter.vnfInstanceSubscriptionFilter) &&
        Objects.equals(this.notificationTypes, vnfIndicatorNotificationsFilter.notificationTypes) &&
        Objects.equals(this.indicatorIds, vnfIndicatorNotificationsFilter.indicatorIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vnfInstanceSubscriptionFilter, notificationTypes, indicatorIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VnfIndicatorNotificationsFilter {\n");
    
    sb.append("    vnfInstanceSubscriptionFilter: ").append(toIndentedString(vnfInstanceSubscriptionFilter)).append("\n");
    sb.append("    notificationTypes: ").append(toIndentedString(notificationTypes)).append("\n");
    sb.append("    indicatorIds: ").append(toIndentedString(indicatorIds)).append("\n");
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
