package de.inventivegames.Murder;

import java.lang.reflect.Field;

public class Reflect {

	public static void set(Object obj, String field, Object value) throws Exception {
		Field f = obj.getClass().getDeclaredField(field);
		f.setAccessible(true);
		f.set(obj, value);
	}
}
