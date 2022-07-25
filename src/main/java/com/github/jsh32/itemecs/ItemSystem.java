package com.github.jsh32.itemecs;

import com.github.jsh32.itemecs.item.events.ItemCreateEvent;
import com.github.jsh32.itemecs.item.events.ItemInteractEvent;

import java.lang.reflect.ParameterizedType;

public abstract class ItemSystem<T> {
    private final String componentName;

    @SafeVarargs
    public ItemSystem(String componentName, Class<? extends ItemSystem<?>>... dependencies) {
        this.componentName = componentName;
    }

    /**
     * Get the component class for this system.
     *
     * @return component class
     */
    public Class<?> getComponentClass() {
        return (Class<?>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public String getComponentName() { return componentName; }

    /**
     * Called when the item is being created.
     *
     * @param event item creation event.
     */
    public abstract void onCreate(ItemCreateEvent<T> event);

    /**
     * Called when the interacting with the item.
     *
     * @param event item interact event.
     */
    public abstract void onInteract(ItemInteractEvent<T> event);
}
