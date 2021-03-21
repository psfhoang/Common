package vn.siplab.medical.education.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.annotations.ApiModelProperty;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"language", "message", "code"}, callSuper = true)
public class BaseDTO extends DTO {

  @JsonInclude(Include.NON_NULL)
  @ApiModelProperty(hidden = true)
  private Long id;

  @JsonInclude(Include.NON_NULL)
  private Boolean active;

  @JsonInclude(Include.NON_NULL)
  @ApiModelProperty(hidden = true)
  private ZonedDateTime createdAt;

  @JsonInclude(Include.NON_NULL)
  @ApiModelProperty(hidden = true)
  private ZonedDateTime updatedAt;

  @JsonInclude(Include.NON_NULL)
  @ApiModelProperty(hidden = true)
  private Long createdBy;

  @JsonInclude(Include.NON_NULL)
  @ApiModelProperty(hidden = true)
  private Long updatedBy;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty(access = Access.READ_ONLY)
  @ApiModelProperty(hidden = true)
  private Integer code;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty(access = Access.READ_ONLY)
  @ApiModelProperty(hidden = true)
  private String message;

  @JsonProperty(access = Access.WRITE_ONLY)
  @ApiModelProperty(hidden = true)
  private Boolean strictlySearch;

  @JsonIgnore
  @ApiModelProperty(hidden = true)
  private String language;

  public BaseDTO(Long id, Boolean active) {
    this.id = id;
    this.active = active;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof BaseDTO)) {
      return false;
    }

    BaseDTO dto = (BaseDTO) obj;

    if (dto.getId() == null) {
      return false;
    }

    return dto.getId().equals(getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}