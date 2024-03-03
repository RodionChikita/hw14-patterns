package ru.otus.appcontainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {
    private static final Logger logger = LoggerFactory.getLogger(AppComponentsContainerImpl.class);

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        var methods = List.of(configClass.getDeclaredMethods());
        List <Method> sortedMethods = methods.stream().sorted(Comparator.comparingInt(x -> x.getAnnotation(AppComponent.class).order())).toList();
        for (Method method : sortedMethods){
            try {
                List<Class<?>> params  =  List.of(method.getParameterTypes());
                List<Object> objects = new ArrayList<>();
                for (Class<?> param : params){
                    objects.add(getAppComponent(param));
                }
                Object component = method.invoke(configClass.getConstructor().newInstance(), objects.toArray());
                appComponents.add(component);
                appComponentsByName.put(method.getAnnotation(AppComponent.class).name(), component);
            } catch (Throwable ex){
                logger.info(String.valueOf(ex.getCause()));
            }
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        for (Object component : appComponents){
            if (componentClass.isInstance(component)){
                return (C) component;
            }
         }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }
}
