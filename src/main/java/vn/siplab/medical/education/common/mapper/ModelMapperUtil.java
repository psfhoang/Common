package vn.siplab.medical.education.common.mapper;

import java.util.Collection;
import org.apache.poi.util.StringUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.InheritingConfiguration;
import org.modelmapper.internal.converter.AssignableConverter;
import org.modelmapper.internal.util.MappingContextHelper;
import vn.siplab.medical.education.common.dao.model.Entity;
import vn.siplab.medical.education.common.dto.DTO;

public class ModelMapperUtil{
    public static ModelMapper getDefaultModelMapper() {
      ModelMapper modelMapper = new ModelMapper();

      InheritingConfiguration configuration = (InheritingConfiguration) modelMapper.getConfiguration();

      configuration.setMatchingStrategy(MatchingStrategies.STRICT);
      configuration.setDeepCopyEnabled(true);
      configuration.setFullTypeMatchingRequired(true);

      configuration.converterStore.getConverters().removeIf(x ->
          x.getClass().getName().equals("org.modelmapper.internal.converter.AssignableConverter")
              || x.getClass().getName().equals("org.modelmapper.internal.converter.CollectionConverter"));

      configuration.converterStore.addConverter(new AssignableConverter());
      configuration.converterStore.addConverter(new CollectionConverter());

      configuration.setPropertyCondition(context -> {

        Class<?> initialType = context.getMapping().getLastDestinationProperty().getInitialType();

        if (DTO.class.isAssignableFrom(initialType)
            && StringUtil.countMatches(context.getMapping().getPath(), '.') > 1) {
          return false;
        }

        Class<?> destinationType = context.getDestinationType();
        if (Collection.class.isAssignableFrom(destinationType)) {
          Class<?> elementType = MappingContextHelper.resolveDestinationGenericType(context);
          return !Entity.class.isAssignableFrom(elementType)
              && !DTO.class.isAssignableFrom(elementType)
              && !elementType.isAssignableFrom(Object.class);
        }

        return true;
      });

      return modelMapper;
    }

}
