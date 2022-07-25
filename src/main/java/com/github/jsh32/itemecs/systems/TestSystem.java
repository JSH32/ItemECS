package com.github.jsh32.itemecs.systems;

import com.github.jsh32.itemecs.ItemSystem;
import com.github.jsh32.itemecs.item.events.ItemCreateEvent;
import com.github.jsh32.itemecs.item.events.ItemInteractEvent;

public class TestSystem extends ItemSystem<TestComponent> {
    public TestSystem() {
        super("test");
    }

    @Override
    public void onCreate(ItemCreateEvent<TestComponent> event) {
        event.getComponentStore().put("testDataHi", event.getConfig().data);
    }

    @Override
    public void onInteract(ItemInteractEvent<TestComponent> event) {

    }
}
