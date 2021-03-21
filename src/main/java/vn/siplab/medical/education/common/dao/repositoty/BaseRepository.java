package vn.siplab.medical.education.common.dao.repositoty;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import vn.siplab.medical.education.common.dao.model.BaseEntity;
import vn.siplab.medical.education.common.dto.BaseDTO;

@NoRepositoryBean
public interface BaseRepository<Entity extends BaseEntity, DTO extends BaseDTO, ID extends Long>
    extends CrudRepository<Entity, ID> {

  @Override
  @CacheEvict(allEntries = true)
  @Transactional
  @Modifying
  <S extends Entity> S save(S entity);

  @Override
  @CacheEvict(allEntries = true)
  <S extends Entity> List<S> saveAll(Iterable<S> entities);

  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e")
  List<Entity> findAll();

  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.id in ?1")
  List<Entity> findAllById(Iterable<ID> ids);

  @Override
  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e where e.id = ?1")
  Optional<Entity> findById(ID id);

  @Cacheable
  @Override
  @Transactional(readOnly = true)
  @Query("select case when count(e) > 0 then true else false end from #{#entityName} e"
      + " where e.id = ?1")
  boolean existsById(ID id);

  // Use delete entity to check permission
  @Override
  @Transactional
  @Modifying
  @PreAuthorize("1==2")
  void deleteById(ID id);

  @Override
  @CacheEvict(allEntries = true)
  @Query("update #{#entityName} e set e.deleted = e.id where e.id = ?#{#entity.id}")
  @Transactional
  @Modifying
  void delete(Entity entity);

  @Query("select case when count(e) > 0 then true else false end from #{#entityName} e"
      + " where e.id = :id"
      + " and 1=2")
  boolean existsForeignKeyConstraint(Long id);

  @Override
  @CacheEvict(allEntries = true)
  @Transactional
  default void deleteAll(Iterable<? extends Entity> entities) {
    entities.forEach(entity -> delete(entity));
  }

  @Override
  @CacheEvict(allEntries = true)
  @Query("update #{#entityName} e set e.deleted = e.id")
  @Transactional
  @Modifying
  void deleteAll();

  @Cacheable
  @Transactional(readOnly = true)
  @Query("select e.active from #{#entityName} e where e.id = ?1")
  Boolean getActiveById(ID id);

  @Transactional(readOnly = true)
  @Query("select e from #{#entityName} e"
      + " where 1 = 1"
      + " and (?#{@f.isNull(#dto.active)} = true or e.active = ?#{#dto.active})")
  Page<Entity> search(DTO dto, Pageable pageable);

  List<?> callProcedure(String name, Map<String, Object> params);
}