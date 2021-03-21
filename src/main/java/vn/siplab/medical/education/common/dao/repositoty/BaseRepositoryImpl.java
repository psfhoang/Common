package vn.siplab.medical.education.common.dao.repositoty;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import vn.siplab.medical.education.common.dao.model.BaseEntity;
import vn.siplab.medical.education.common.dto.BaseDTO;

public class BaseRepositoryImpl<Entity extends BaseEntity, DTO extends BaseDTO, ID extends Long>
    extends SimpleJpaRepository<Entity, ID> implements BaseRepository<Entity, DTO, ID> {

  private EntityManager entityManager;

  public BaseRepositoryImpl(JpaEntityInformation<Entity, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public boolean existsForeignKeyConstraint(Long id) {
    return false;
  }

  @Override
  public Boolean getActiveById(ID id) {
    return null;
  }

  @Override
  public Page<Entity> search(DTO dto, Pageable pageable) {
    return null;
  }

  @Override
  public List<?> callProcedure(String name, Map<String, Object> params) {
    StoredProcedureQuery procedureQuery = entityManager.createNamedStoredProcedureQuery(name);

    if (params != null) {
      params.forEach(procedureQuery::setParameter);
    }

    return procedureQuery.getResultList();
  }
}