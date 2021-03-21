package vn.siplab.medical.education.common.dao.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor
public abstract class Entity implements Serializable {

  @Transient
  private Boolean newRecord = false;

  public Entity(Long id) {
    setId(id);
  }

  public abstract Long getId();

  public abstract void setId(Long id);

  public boolean isNewRecord() {
    return newRecord || getId() == null || getId().compareTo(0L) == 0;
  }

  public void setNewRecord(Boolean newRecord) {
    this.newRecord = newRecord;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Entity)) {
      return false;
    }

    Entity entity = (Entity) obj;

    if (entity.getId() == null) {
      return false;
    }

    return entity.getId().equals(getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}