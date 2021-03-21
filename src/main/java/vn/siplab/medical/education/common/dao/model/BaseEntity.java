package vn.siplab.medical.education.common.dao.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import vn.siplab.medical.education.common.dao.model.AuditEntity;

@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity extends AuditEntity {

  @Column(name = "active", nullable = false)
  @Audited
  private Boolean active;

  @Column(name = "active", insertable = false, updatable = false)
  private Boolean oldActive;

  @Column(name = "deleted", nullable = false)
  @Audited
  private Long deleted = 0L;

  @Transient
  private Boolean mapAllProperties;

  @Transient
  private Integer code;

  @Transient
  private String message;

  public BaseEntity(Long id) {
    setId(id);
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setMapAllProperties(Boolean mapAllProperties) {
    this.mapAllProperties = mapAllProperties;
  }

  public boolean isMapAllProperties() {
    return Boolean.TRUE.equals(mapAllProperties);
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public Integer getCode() {
    return code;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @PrePersist
  public void prePersist() {
    if (active == null) {
      active = true;
    }

    setNewRecord(true);
  }

  @PreUpdate
  public void preUpdate() {
    if (active == null) {
      active = oldActive;
    }

    setNewRecord(false);
  }

  @PostPersist
  public void postPersist() {
  }

  @PostUpdate
  public void postUpdate() {
  }
}
