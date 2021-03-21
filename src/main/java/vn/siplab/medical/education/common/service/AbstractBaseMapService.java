package vn.siplab.medical.education.common.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.CastUtils;
import vn.siplab.medical.education.common.dao.model.BaseEntity;
import vn.siplab.medical.education.common.dto.BaseDTO;
import vn.siplab.medical.education.common.mapper.ModelMapperUtil;
import vn.siplab.medical.education.common.msg.Msg;

public abstract class AbstractBaseMapService<Entity extends BaseEntity, DTO extends BaseDTO> extends
    AbstractBaseEnvService {

  private final Class<Entity> entityClass;

  private final Class<DTO> dtoClass;

  private final ModelMapper modelMapper;

  public AbstractBaseMapService() {
    long startTime = 0;
    if (getLogger().isTraceEnabled()) {
      startTime = System.currentTimeMillis();
    }

    entityClass = CastUtils.cast(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    dtoClass = CastUtils.cast(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
    modelMapper = ModelMapperUtil.getDefaultModelMapper();
    configModelMapper(modelMapper);

    getModelMapper().map(getDTO(), getEntity());
    getModelMapper().map(getEntity(), getDTO());

    if (getLogger().isTraceEnabled()) {
      getLogger().info("Init: " + (System.currentTimeMillis() - startTime) + " ms");
    }
  }

  final protected ModelMapper getModelMapper() {
    return modelMapper;
  }

  protected void configModelMapper(ModelMapper modelMapper) {

  }

  final protected Class<Entity> getEntityClass() {
    return entityClass;
  }

  final protected Class<DTO> getDTOClass() {
    return dtoClass;
  }

  protected Entity getEntity() {
    try {
      return entityClass.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
        | NoSuchMethodException e) {
//      throw new DataException.InvalidConstructor(getName());
      return null;
    }
  }

  protected DTO getDTO() {
    try {
      return dtoClass.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
        | NoSuchMethodException e) {
//      throw new DataException.InvalidConstructor(getName());
      return null;
    }
  }

  final public String getName() {
    return Msg.getMessage(getSimpleName());
  }

  final public String getSimpleName() {
    return entityClass.getSimpleName();
  }

  final protected Entity mapToEntity(DTO dto) {
    if (dto == null) {
      return null;
    }

    long startTime = 0;
    if (getLogger().isTraceEnabled()) {
      startTime = System.currentTimeMillis();
    }

    if (dto.getActive() == null && dto.getId() != null && dto.getId() > 0) {
      dto.setActive(getActiveById(dto.getId()));
    }

    Entity entity = getModelMapper().map(dto, getEntityClass());

    specificMapToEntity(dto, entity);

    if (getLogger().isTraceEnabled()) {
      getLogger().info("Method mapToEntity(DTO) execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    return entity;
  }

  final protected void mapToEntity(DTO dto, Entity entity) {
    if (dto == null) {
      return;
    }

    long startTime = 0;
    if (getLogger().isTraceEnabled()) {
      startTime = System.currentTimeMillis();
    }

    if (dto.getActive() == null && entity.getActive() != null
        && entity.getId() != null && entity.getId() > 0) {
      dto.setActive(entity.getActive());
    }

    getModelMapper().map(dto, entity);

    specificMapToEntity(dto, entity);

    if (getLogger().isTraceEnabled()) {
      getLogger().info("Method mapToEntity(DTO, Entity) execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
  }

  final protected Set<Entity> mapToEntities(Set<DTO> dtos, Set<Entity> entities) {
    if (entities == null) {
      entities = new HashSet<>();
    }

    if (dtos == null) {
      dtos = new HashSet<>();
    }

    long startTime = 0;
    if (getLogger().isTraceEnabled()) {
      startTime = System.currentTimeMillis();
    }

    List<Entity> removeEntities = new ArrayList<>();

    for (Entity entity : entities) {
      DTO dto = dtos.stream().filter(d -> entity.getId().equals(d.getId())).findFirst().orElse(null);

      if (dto == null) {
        removeEntities.add(entity);
      } else {
        mapToEntity(dto, entity);

        dtos.remove(dto);
      }
    }

    for (Entity e: removeEntities) {
      entities.remove(e);
      deleteEntity(e);
    }

    for (DTO dto: dtos) {
      entities.add(mapToEntity(dto));
    }

    if (getLogger().isTraceEnabled()) {
      getLogger().info("Method mapToEntities(Set<DTO>, Set<Entity>) execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    return entities;
  }

  final public DTO mapToDTO(Entity entity) {
    if (entity == null) {
      return null;
    }

    long startTime = 0;
    if (getLogger().isTraceEnabled()) {
      startTime = System.currentTimeMillis();
    }

    DTO dto = getModelMapper().map(entity, getDTOClass());

    specificMapToDTO(entity, dto);

    if (getLogger().isTraceEnabled()) {
      getLogger().info("Method mapToDTO(Entity) execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    return dto;
  }

  final protected void mapToDTO(Entity entity, DTO dto) {
    if (entity == null) {
      return;
    }

    long startTime = 0;
    if (getLogger().isTraceEnabled()) {
      startTime = System.currentTimeMillis();
    }

    getModelMapper().map(entity, dto);

    specificMapToDTO(entity, dto);

    if (getLogger().isTraceEnabled()) {
      getLogger().info("Method mapToDTO(Entity, DTO) execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
  }

  protected void specificMapToDTO(Entity entity, DTO dto) {

  }

  protected void specificMapToEntity(DTO dto, Entity entity) {

  }

  protected Boolean getActiveById(Long id) {
    return null;
  }

  protected Entity deleteEntity(Entity model) {
    return null;
  }

}
