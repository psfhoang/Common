package vn.siplab.medical.education.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.util.CastUtils;
import org.springframework.transaction.annotation.Transactional;
import vn.siplab.medical.education.common.dao.model.BaseEntity;
import vn.siplab.medical.education.common.dao.repositoty.BaseRepository;
import vn.siplab.medical.education.common.dto.BaseDTO;
import vn.siplab.medical.education.common.exception.BaseException;
import vn.siplab.medical.education.common.exception.DataException;
import vn.siplab.medical.education.common.until.ObjectMapperUtil;

@Transactional
public abstract class AbstractBaseService<Entity extends BaseEntity, DTO extends BaseDTO, Repository extends BaseRepository<Entity, DTO, Long>> extends
    AbstractBaseMapService<Entity, DTO> implements BaseService<DTO> {

  protected abstract Repository getRepository();


  final public String getLanguage() {
    return LocaleContextHolder.getLocale().getLanguage();
  }

  protected Entity getById(Long id) {
    return getRepository().findById(id).orElseThrow(
        () -> new DataException.NotFoundEntityById(id, getName()));
  }

  @Override
  protected Boolean getActiveById(Long id) {
    return getRepository().getActiveById(id);
  }

  @Override
  public DTO save(DTO dto) {
    if (dto == null) {
      throw new DataException.NotExistsData();
    }

    Entity model;

    if (dto.getId() != null) {
      model = getById(dto.getId());
      mapToEntity(dto, model);
      model.setId(dto.getId());
    } else {
      model = mapToEntity(dto);
    }

    return save(model, dto);
  }

  @Override
  public List<DTO> save(List<DTO> dtos) {
    if (dtos == null || dtos.isEmpty()) {
      throw new DataException.NotExistsData();
    }

    List<Entity> entities = new ArrayList<>();

    dtos.forEach(dto -> {
      Entity model;
      if (dto.getId() != null) {
        model = getById(dto.getId());
        mapToEntity(dto, model);
        model.setId(dto.getId());
      } else {
        model = mapToEntity(dto);
      }
      entities.add(model);
    });


    return save(entities, dtos);
  }

  @Override
  public DTO save(Long id, DTO dto) {
    if (id == null || id.compareTo(0L) <= 0) {
      throw new DataException.NotFoundEntityById(id, getName());
    }

    if (dto == null) {
      throw new DataException.NotExistsData();
    }

    Entity model = getById(id);

    dto.setId(id);
    mapToEntity(dto, model);
    model.setId(id);

    return save(model, dto);
  }

  @Override
  public DTO save(Long id, Map<String, Object> map) {
    if (id == null || id.compareTo(0L) <= 0) {
      throw new DataException.NotFoundEntityById(id, getName());
    }

    if (map == null) {
      throw new DataException.NotExistsData();
    }

    Entity model = getById(id);
    model.setMapAllProperties(true);

    map = mergeMap(map, ObjectMapperUtil.convertValue(mapToDTO(model)));

    DTO dto = ObjectMapperUtil.convertValue(map, getDTOClass());

    dto.setId(id);
    mapToEntity(dto, model);
    model.setId(id);

    return save(model, dto);
  }

  private Map<String, Object> mergeMap(Map<String, Object> from, Map<String, Object> to) {
    from.forEach((key, newValue) -> {
      Object oldValue = to.get(key);
      if ((oldValue instanceof Map) && (newValue instanceof Map)) {
        to.put(key, mergeMap(CastUtils.cast(newValue), CastUtils.cast(oldValue)));
      } else {
        to.put(key, newValue);
      }
    });

    return to;
  }

  protected DTO save(Entity model, DTO dto) {
    model = beforeSave(model, dto);

    model = saveEntity(model);

    dto = afterSave(model, dto);

    mapToDTO(model, dto);

    return dto;
  }

  protected List<DTO> save(List<Entity> entities, List<DTO> dtos) {
    entities = beforeSave(entities, dtos);

    entities = saveEntity(entities);

    int size = entities.size();
    for (int i = 0; i < size; i++) {
      Entity entity = entities.get(i);

      if (entity.getCode() != null && entity.getCode() > 0) {
        continue;
      }
    }

    dtos = afterSave(entities, dtos);

    for (int i = 0; i < size; i++) {
      DTO dto = dtos.get(i);
      Entity entity = entities.get(i);

      mapToDTO(entity, dto);
      dto.setCode(entity.getCode());
      dto.setMessage(entity.getMessage());
    }

    return dtos;
  }

  protected Entity saveEntity(Entity model) {
    long startTime = 0;
    if (getLogger().isTraceEnabled()) {
      startTime = System.currentTimeMillis();
    }

    model =  getRepository().save(model);
    model.setMapAllProperties(true);

    if (getLogger().isTraceEnabled()) {
      getLogger().info("Method saveEntity(Entity) execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    return model;
  }

  protected List<Entity> saveEntity(List<Entity> model) {
    return getRepository().saveAll(model);
  }

  @Override
  public void delete(Long id) {

    validateForeignKeyConstraint(id);

    Entity model = getById(id);

    model = beforeDelete(model);

    deleteEntity(model);

    afterDelete(model);
  }

  @Override
  public List<DTO> delete(List<Long> ids) {
    List<Entity> entities = ids.stream().map(this::getById).collect(Collectors.toList());
    List<DTO> dtos = entities.stream().map(this::mapToDTO).collect(Collectors.toList());

    entities.forEach(e -> {
      try {
        validateForeignKeyConstraint(e.getId());
      } catch (BaseException ex) {
        e.setCode(ex.getCode());
        e.setMessage(ex.getMessage());
      }
    });

    entities = beforeDelete(entities);

    deleteEntity(entities);

    entities = afterDelete(entities);

    int size = entities.size();
    for (int i = 0; i < size; i++) {
      DTO dto = dtos.get(i);
      Entity entity = entities.get(i);

      dto.setCode(entity.getCode());
      dto.setMessage(entity.getMessage());
    }

    return dtos;
  }

  protected Entity deleteEntity(Entity model) {
    getRepository().delete(model);
    return model;
  }

  protected List<Entity> deleteEntity(List<Entity> model) {
    getRepository().deleteAll(model);
    return model;
  }

  @Override
  @Cacheable
  public DTO findById(Long id) {
    return findById(id, true);
  }

  @Override
  @Cacheable
  public DTO findById(Long id, boolean mapAllProperties) {
    if (id == null || id.compareTo(0L) <= 0) {
      return null;
    }

    Entity model = getById(id);
    model.setMapAllProperties(mapAllProperties);
    return mapToDTO(model);
  }

  @Override
  @Cacheable
  public List<DTO> findAll() {
    return getRepository().findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
  }

  protected Page<Entity> searchEntity(DTO dto, Pageable pageable) {
    dto.setLanguage(getLanguage());

    dto = beforeSearch(dto);

    return getRepository().search(dto, pageable);
  }

  @Override
  @Cacheable
  public Page<DTO> search(DTO dto, Pageable pageable) {
    return searchEntity(dto, pageable).map(this::mapToDTO);
  }

  protected Entity searchEntity2(DTO dto, Pageable pageable) {
    dto.setLanguage(getLanguage());

    dto = beforeSearch(dto);

    Sort sort = null;
    if (pageable != null) {
      sort = pageable.getSort();
    }

    if (sort == null) {
      sort = Sort.by(Direction.DESC, "id");
    }

    pageable = PageRequest.of(0, 1, sort);

    List<Entity> data = getRepository().search(dto, pageable).getContent();
    if (data.isEmpty()) {
      return null;
    }

    return data.get(0);
  }

  @Override
  @Cacheable
  public DTO search2(DTO dto, Pageable pageable) {
    Entity e = searchEntity2(dto, pageable);

    if (e != null) {
      e.setMapAllProperties(true);
    }

    return mapToDTO(e);
  }

  @Override
  @Cacheable
  public DTO search2(DTO dto) {
    return search2(dto, null);
  }

  @Override
  public boolean existsById(Long id) {
    return getRepository().existsById(id);
  }

  protected Map<String, Object> getByKeys(Map<String, Object> newObj, Map<String, Object> keys) {
    for (String key : keys.keySet()) {
      if ("id".equals(key)) {
        Long id = (Long) keys.get("id");
        Entity e = getRepository().findById(id).orElse(null);
        if (e == null) {
          return null;
        }
        e.setMapAllProperties(true);

        return ObjectMapperUtil.convertValue(mapToDTO(e));
      }
    }
    return null;
  }

  protected Entity beforeSave(Entity entity, DTO dto) {
    return entity;
  }

  protected DTO afterSave(Entity entity, DTO dto) {
    return dto;
  }

  protected List<Entity> beforeSave(List<Entity> entities, List<DTO> dtos) {
    int size = entities.size();
    for (int i = 0; i < size; i++) {
      Entity entity = entities.get(i);
      DTO dto = dtos.get(i);

      if (entity.getCode() != null && entity.getCode() > 0) {
        continue;
      }

      try {
        entity = beforeSave(entity, dto);
      } catch (BaseException ex) {
        entity.setCode(ex.getCode());
        entity.setMessage(ex.getMessage());
      }

      entities.set(i, entity);
    }

    return entities;
  }

  protected List<DTO> afterSave(List<Entity> entities, List<DTO> dtos) {
    int size = entities.size();
    for (int i = 0; i < size; i++) {
      Entity entity = entities.get(i);
      DTO dto = dtos.get(i);

      if (entity.getCode() != null && entity.getCode() > 0) {
        continue;
      }

      try {
        dto = afterSave(entity, dto);
      } catch (BaseException ex) {
        entity.setCode(ex.getCode());
        entity.setMessage(ex.getMessage());
      }

      dtos.set(i, dto);
    }

    return dtos;
  }

  protected DTO beforeSearch(DTO dto) {
    return dto;
  }

  protected Entity beforeDelete(Entity entity) {
    return entity;
  }

  protected Entity afterDelete(Entity entity) {
    return entity;
  }

  protected List<Entity> beforeDelete(List<Entity> model) {
    return model.stream().map(e -> {
      if (e.getCode() != null && e.getCode() > 0) {
        return e;
      }

      try {
        e = beforeDelete(e);
      } catch (BaseException ex) {
        e.setCode(ex.getCode());
        e.setMessage(ex.getMessage());
      }
      return e;
    }).collect(Collectors.toList());
  }

  protected List<Entity> afterDelete(List<Entity> model) {
    return model.stream().map(e -> {
      if (e.getCode() != null && e.getCode() > 0) {
        return e;
      }

      try {
        e = afterDelete(e);
      } catch (BaseException ex) {
        e.setCode(ex.getCode());
        e.setMessage(ex.getMessage());
      }
      return e;
    }).collect(Collectors.toList());
  }

  protected void validateForeignKeyConstraint(Long id) {
    if (getRepository().existsForeignKeyConstraint(id)) {
      throw new DataException.ExistsForeignKeyConstraint();
    }
  }

  private void putValue(Map<String, Object> obj, Object value, String columnName) {
    if (columnName.contains(".")) {
      String[] names = columnName.split("\\.");
      int length = names.length;

      Map<String, Object> map = obj;
      for (int k = 0; k < length - 1; k++) {
        String name = names[k];

        map.computeIfAbsent(name, m -> new HashMap<>());

        if (!(map.get(name) instanceof Map)) {
          throw new DataException.InvalidDataType(columnName);
        }

        map = CastUtils.cast(map.get(name));
      }

      map.put(names[length - 1], value);
    } else {
      obj.put(columnName, value);
    }
  }

  private Map<String, Object> getDuplicate(List<Map<String, Object>> objects, Map<String, Object> keys) {
    return objects.stream().filter(e -> {
      if (e == null) {
        return false;
      }

      for (String key : keys.keySet()) {
        Object data = keys.get(key);
        if (data == null) {
          return false;
        }

        if (!data.equals(e.get(key))) {
          return false;
        }
      }
      return true;
    }).findFirst().orElse(null);
  }
}