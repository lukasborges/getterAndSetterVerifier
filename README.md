# GetterAndSetterVerifier

**GetterAndSetterVerifier** can be used in Java unit tests to automate tests getters/setters methods. This project uses Mockito and JUnit dependencies. 

### Simple Class

Usually, you only need to call the static method "forClass" with class that you want to test. Then, you need to call the method "verify()". This example works well if the class is a JavaBean implemetation.

```java
    GetterAndSetterVerifier.forClass(Foo.class).verify();
```

### Object

Sometimes the test cannot instantiate the class. In this case, you need call the static method "forObject()" with instance.


```java
    GetterAndSetterVerifier.forObject(object).verify();
```

### Exclude fields

You can exclude fields that you don't want to test, using "exclude" method.

```java
    GetterAndSetterVerifier.forClass(Foo.class).exclude("shoes").verify();
```

### Add default instances

The project uses Mockito to mock objects. You can replace it by instances.

```java
    Map<Class<?>, Object> defautInstances = new HashMap<Class<?>, Object>();
    defautInstances.put(Foo.class, new Foo());
    GetterAndSetterVerifier.forClass(Foo.class).addDefaultInstances(defaultInstances).verify();
```
