package com.github.jsh32.itemecs.item.annotations;

import java.lang.annotation.*;

/**
 * This field is automatically injected into the NBT of the item.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PersistentField {
}
