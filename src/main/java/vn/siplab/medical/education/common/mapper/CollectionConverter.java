package vn.siplab.medical.education.common.mapper;

import java.util.Collection;
import java.util.Iterator;
import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.modelmapper.spi.MappingContext;

public class CollectionConverter implements ConditionalConverter<Object, Collection<Object>> {

  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    return Iterables.isIterable(sourceType) && Collection.class.isAssignableFrom(destinationType) ? MatchResult.FULL
        : MatchResult.NONE;
  }

  @Override
  public Collection<Object> convert(MappingContext<Object, Collection<Object>> context) {
    Object source = context.getSource();
    if (source == null)
      return null;

    Collection<Object> destination = MappingContextHelper.createCollection(context);
    Class<?> elementType = MappingContextHelper.resolveDestinationGenericType(context);

    int index = 0;
    for (Iterator<Object> iterator = Iterables.iterator(source); iterator.hasNext(); index++) {
      Object sourceElement = iterator.next();
      Object element = null;
      if (sourceElement != null) {
        MappingContext<?, ?> elementContext = element == null
            ? context.create(sourceElement, elementType)
            : context.create(sourceElement, element);
        element = context.getMappingEngine().map(elementContext);
      }
      destination.add(element);
    }

    return destination;
  }
}
