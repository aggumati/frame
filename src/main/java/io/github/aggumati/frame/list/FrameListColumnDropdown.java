package io.github.aggumati.frame.list;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.aggumati.frame.formtype.ItemDropdown;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FrameListColumnDropdown {
	ItemDropdown[] value();
}
