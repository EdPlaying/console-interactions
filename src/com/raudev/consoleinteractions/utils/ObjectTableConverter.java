package com.raudev.consoleinteractions.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for converting collections of objects into tabular data.
 *
 * <p>The converter uses JavaBean properties to determine which values
 * should be included in the generated table. Property values are obtained
 * through their public getter methods.</p>
 *
 * <p>The requested properties determine both the columns included in the
 * table and their order. The first row of the resulting table contains the
 * property names, while each following row contains the corresponding
 * property values of an object.</p>
 *
 * <p>All non-null objects in the collection must belong to exactly the
 * same class.</p>
 */
public final class ObjectTableConverter {

    private ObjectTableConverter() {
        // Prevent instantiation.
    }

    /**
     * Converts a collection of objects into a table representation.
     *
     * <p>The provided property names determine which values are extracted
     * from each object. Properties are resolved according to the JavaBeans
     * naming conventions and are accessed through their public getter
     * methods.</p>
     *
     * <p>Null objects, invalid property names, duplicate properties, and
     * properties without readable getters are omitted while the remaining
     * valid data continues to be processed.</p>
     *
     * @param objects the objects to convert
     * @param properties the properties to include in the table
     * @return a table containing the selected property names and values
     * @throws IllegalArgumentException if the object collection is null or
     *                                  empty, if no properties are provided,
     *                                  or if the objects belong to different
     *                                  classes
     * @throws IllegalStateException if the object's JavaBean information
     *                               cannot be inspected
     */
    public static List<List<?>> convert(
            List<?> objects,
            String... properties
    ) {
        validateInput(objects, properties);

        List<?> validObjects = filterObjects(objects);

        if (validObjects.isEmpty()) {
            System.out.println(
                    "No valid objects are available to generate the table."
            );

            return new ArrayList<>();
        }

        Class<?> objectClass =
                validateObjectClasses(validObjects);

        List<String> validProperties =
                filterProperties(properties);

        if (validProperties.isEmpty()) {
            System.out.println(
                    "No valid properties are available to generate the table."
            );

            return new ArrayList<>();
        }

        List<ResolvedProperty> resolvedProperties =
                resolveProperties(
                        objectClass,
                        validProperties
                );

        if (resolvedProperties.isEmpty()) {
            System.out.println(
                    "No readable properties are available to generate the table."
            );

            return new ArrayList<>();
        }

        return buildTable(
                validObjects,
                resolvedProperties
        );
    }

    /**
     * Validates the information required to start the conversion process.
     *
     * @param objects the objects to validate
     * @param properties the requested properties
     * @throws IllegalArgumentException if the object collection is null or
     *                                  empty, or if the property array is
     *                                  null or empty
     */
    private static void validateInput(
            List<?> objects,
            String[] properties
    ) {
        if (objects == null) {
            throw new IllegalArgumentException(
                    "The object collection cannot be null."
            );
        }

        if (objects.isEmpty()) {
            throw new IllegalArgumentException(
                    "The object collection cannot be empty."
            );
        }

        if (properties == null) {
            throw new IllegalArgumentException(
                    "The property list cannot be null."
            );
        }

        if (properties.length == 0) {
            throw new IllegalArgumentException(
                    "At least one property must be specified."
            );
        }
    }

    /**
     * Creates a collection containing only non-null objects.
     *
     * @param objects the objects to filter
     * @return a list containing the valid objects
     */
    private static List<?> filterObjects(List<?> objects) {
        List<Object> validObjects = new ArrayList<>();

        for (int index = 0; index < objects.size(); index++) {
            Object object = objects.get(index);

            if (object == null) {
                System.out.println(
                        "Object at index "
                                + index
                                + " is null and will be omitted."
                );

                continue;
            }

            validObjects.add(object);
        }

        return validObjects;
    }

    /**
     * Ensures that all objects belong to exactly the same class.
     *
     * @param objects the objects to validate
     * @return the common class of the objects
     * @throws IllegalArgumentException if objects from different classes
     *                                  are found
     */
    private static Class<?> validateObjectClasses(
            List<?> objects
    ) {
        Class<?> expectedClass =
                objects.get(0).getClass();

        for (int index = 1; index < objects.size(); index++) {
            Class<?> currentClass =
                    objects.get(index).getClass();

            if (currentClass != expectedClass) {
                throw new IllegalArgumentException(
                        "All objects must belong to the same class. "
                                + "Expected "
                                + expectedClass.getName()
                                + " but found "
                                + currentClass.getName()
                                + " at index "
                                + index
                                + "."
                );
            }
        }

        return expectedClass;
    }

    /**
     * Filters the requested property names.
     *
     * <p>The first occurrence of each valid property is preserved so that
     * the resulting table maintains the order specified by the caller.</p>
     *
     * @param properties the property names to filter
     * @return the valid and unique property names
     */
    private static List<String> filterProperties(
            String[] properties
    ) {
        List<String> validProperties = new ArrayList<>();
        Set<String> registeredProperties = new HashSet<>();

        for (int index = 0; index < properties.length; index++) {
            String property = properties[index];

            if (property == null || property.isBlank()) {
                System.out.println(
                        "Property at index "
                                + index
                                + " is empty and will be omitted."
                );

                continue;
            }

            if (!registeredProperties.add(property)) {
                System.out.println(
                        "Property '"
                                + property
                                + "' is repeated and will be omitted."
                );

                continue;
            }

            validProperties.add(property);
        }

        return validProperties;
    }

    /**
     * Resolves the requested JavaBean properties for a class.
     *
     * <p>Only readable properties with public getter methods are included
     * in the result. The returned properties maintain the same order as
     * the property names requested by the caller.</p>
     *
     * @param objectClass the class to inspect
     * @param properties the requested property names
     * @return the readable properties
     * @throws IllegalStateException if the class cannot be inspected
     */
    private static List<ResolvedProperty> resolveProperties(
            Class<?> objectClass,
            List<String> properties
    ) {
        BeanInfo beanInfo = getBeanInfo(objectClass);

        PropertyDescriptor[] descriptors =
                beanInfo.getPropertyDescriptors();

        List<ResolvedProperty> resolvedProperties =
                new ArrayList<>();

        for (String property : properties) {
            PropertyDescriptor descriptor =
                    findPropertyDescriptor(
                            descriptors,
                            property
                    );

            if (descriptor == null) {
                System.out.println(
                        "Property '"
                                + property
                                + "' does not exist in class "
                                + objectClass.getName()
                                + " and will be omitted."
                );

                continue;
            }

            Method getter =
                    descriptor.getReadMethod();

            if (getter == null) {
                System.out.println(
                        "Property '"
                                + property
                                + "' does not have a readable getter "
                                + "and will be omitted."
                );

                continue;
            }

            resolvedProperties.add(
                    new ResolvedProperty(
                            property,
                            descriptor.getPropertyType(),
                            getter
                    )
            );
        }

        return resolvedProperties;
    }

    /**
     * Retrieves the JavaBean information associated with a class.
     *
     * @param objectClass the class to inspect
     * @return the JavaBean information for the class
     * @throws IllegalStateException if the class cannot be inspected
     */
    private static BeanInfo getBeanInfo(
            Class<?> objectClass
    ) {
        try {
            return Introspector.getBeanInfo(
                    objectClass,
                    Object.class
            );

        } catch (IntrospectionException exception) {
            throw new IllegalStateException(
                    "Unable to inspect class "
                            + objectClass.getName()
                            + ".",
                    exception
            );
        }
    }

    /**
     * Finds a JavaBean property descriptor by its property name.
     *
     * @param descriptors the available property descriptors
     * @param property the property to find
     * @return the matching descriptor, or {@code null} if the property
     *         does not exist
     */
    private static PropertyDescriptor findPropertyDescriptor(
            PropertyDescriptor[] descriptors,
            String property
    ) {
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equals(property)) {
                return descriptor;
            }
        }

        return null;
    }

    /**
     * Builds the table using the resolved properties.
     *
     * <p>The first row contains the property names. Each following row
     * represents one object and contains the values returned by the
     * corresponding getter methods.</p>
     *
     * @param objects the objects represented by the table
     * @param properties the resolved properties
     * @return the generated table
     */
    private static List<List<?>> buildTable(
            List<?> objects,
            List<ResolvedProperty> properties
    ) {
        List<List<?>> table = new ArrayList<>();

        table.add(createHeader(properties));

        for (int objectIndex = 0;
             objectIndex < objects.size();
             objectIndex++) {

            Object object =
                    objects.get(objectIndex);

            List<Object> row =
                    createRow(
                            object,
                            objectIndex,
                            properties
                    );

            table.add(row);
        }

        return table;
    }

    /**
     * Creates the header row of the table.
     *
     * @param properties the properties represented by the columns
     * @return the generated header row
     */
    private static List<String> createHeader(
            List<ResolvedProperty> properties
    ) {
        List<String> header = new ArrayList<>();

        for (ResolvedProperty property : properties) {
            header.add(property.name());
        }

        return header;
    }

    /**
     * Creates a table row from an object.
     *
     * @param object the object represented by the row
     * @param objectIndex the object's position in the filtered collection
     * @param properties the properties represented by the columns
     * @return the generated row
     */
    private static List<Object> createRow(
            Object object,
            int objectIndex,
            List<ResolvedProperty> properties
    ) {
        List<Object> row = new ArrayList<>();

        for (ResolvedProperty property : properties) {
            Object value =
                    readProperty(
                            object,
                            objectIndex,
                            property
                    );

            row.add(value);
        }

        return row;
    }

    /**
     * Reads the value of a property from an object.
     *
     * <p>If the getter cannot provide a value, {@code null} is returned so
     * that the table structure remains aligned with the selected columns.</p>
     *
     * @param object the object containing the property
     * @param objectIndex the object's position in the filtered collection
     * @param property the property to read
     * @return the value returned by the getter, or {@code null} when the
     *         value cannot be obtained
     */
    private static Object readProperty(
            Object object,
            int objectIndex,
            ResolvedProperty property
    ) {
        try {
            return property.getter().invoke(object);

        } catch (IllegalAccessException exception) {
            System.out.println(
                    "Getter '"
                            + property.getter().getName()
                            + "()' for property '"
                            + property.name()
                            + "' could not be accessed for object at index "
                            + objectIndex
                            + ". The cell will remain empty."
            );

            return null;

        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            String causeMessage =
                    cause != null && cause.getMessage() != null
                            ? cause.getMessage()
                            : "No additional information is available.";

            System.out.println(
                    "Getter '"
                            + property.getter().getName()
                            + "()' for property '"
                            + property.name()
                            + "' failed for object at index "
                            + objectIndex
                            + ". The cell will remain empty. Cause: "
                            + causeMessage
            );

            return null;

        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Getter '"
                            + property.getter().getName()
                            + "()' could not be invoked for property '"
                            + property.name()
                            + "' on object at index "
                            + objectIndex
                            + ". The cell will remain empty."
            );

            return null;
        }
    }

    /**
     * Represents a readable property that can be included in the table.
     *
     * @param name the property name
     * @param type the property data type
     * @param getter the method used to retrieve the property value
     */
    private record ResolvedProperty(
            String name,
            Class<?> type,
            Method getter
    ) {
    }
}