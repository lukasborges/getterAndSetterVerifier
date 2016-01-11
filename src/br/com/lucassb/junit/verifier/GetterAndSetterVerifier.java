package br.com.lucassb.junit.verifier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * This class tests getter/setter methods of a class using JUnit.
 * @author lukasborges
 *
 */
public class GetterAndSetterVerifier {

	private Object object;
	private Random random;
	private Map<Class<?>, Object> defaultInstances;
	private List<String> ignoredFields = new ArrayList<String>();
	
	/**
	 * Creates the GetterAndSetterVerifier
	 * @param class The class that will be instanced
	 * @return the instance
	 */
	public static GetterAndSetterVerifier forClass(Class<?> clazz){
		try {
			return new GetterAndSetterVerifier(clazz.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates the GetterAndSetterVerifier
	 * @param object The instance to test 
	 * @return the instance
	 */
	public static GetterAndSetterVerifier forObject(Object object){
		return new GetterAndSetterVerifier(object);
	}
	
	private GetterAndSetterVerifier(Object object){
		// creates random values
		random = new Random();
		defaultInstances = new HashMap<Class<?>, Object>();
		defaultInstances.put(boolean.class, random.nextBoolean());
		defaultInstances.put(Boolean.class, random.nextBoolean());
		defaultInstances.put(int.class,random.nextInt());
		defaultInstances.put(Integer.class,random.nextInt());
		defaultInstances.put(long.class, random.nextLong());
		defaultInstances.put(Long.class, random.nextLong());
		defaultInstances.put(float.class, random.nextFloat());
		defaultInstances.put(Float.class, random.nextFloat());
		defaultInstances.put(double.class, random.nextDouble());
		defaultInstances.put(Double.class, random.nextDouble());
		defaultInstances.put(String.class, UUID.randomUUID().toString());
		defaultInstances.put(Date.class, new Date(random.nextLong()));
		
		this.object = object;
	}
	
	/**
	 * Sets a list of the names of the fields that should be ignored
	 * @param ignoredFields The list of the names of the fields 
	 * @return this instance
	 */
	public GetterAndSetterVerifier exclude(String... ignoredFields){
		this.ignoredFields = Arrays.asList(ignoredFields);
		return this;
	}
	
	/**
	 * Adds a map of instances to use by default. 
	 * The test uses mocks for default. 
	 * @param defaultInstances The map with classes and its instances to use
	 * @return this instance
	 */
	public GetterAndSetterVerifier addDefaultInstances(Map<Class<?>, Object> defaultInstances){
		this.defaultInstances.putAll(defaultInstances);
		return this;
	}
	
	/**
	 * Tests the getter and setter method of the fields
	 */
	public void verify(){
		String fieldName = null;
		try {
			BeanInfo info = Introspector.getBeanInfo(this.object.getClass());  
			for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
				fieldName = propertyDescriptor.getName();
				Method setter = propertyDescriptor.getWriteMethod();
				Method getter = propertyDescriptor.getReadMethod();
				
				if(getter != null && setter != null && !this.ignoredFields.contains(fieldName)){
					Object value = this.getValue(propertyDescriptor.getPropertyType());
					setter.invoke(this.object, value);
					assertEquals(String.format("Fail to check getter/setter on field \"%s\"", fieldName), value, getter.invoke(object));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(String.format("Exception thrown on field: \"%s\"", fieldName));
		}
	}
	
	/**
	 * Generates a random value for type
	 * @param clazz The class type  
	 * @return the object value
	 */
	private Object getValue(Class<?> clazz){
		if(clazz.isEnum()){
			Enum<?>[] enumConstants= (Enum<?>[]) clazz.getEnumConstants();
			return enumConstants[random.nextInt(enumConstants.length)];
		}
		Object object = defaultInstances.get(clazz);
		if(object == null){
			object = mock(clazz);
		}
		return object;
	}
}
