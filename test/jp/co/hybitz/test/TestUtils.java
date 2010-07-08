/**
 * Copyright (C) 2010 Hybitz.co.ltd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 */
package jp.co.hybitz.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TestUtils {

    public static Object getFieldValue(Object object, String fieldName) {
        try {
            Field f = object.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(object);
        } catch (Exception e) {
            throw new IllegalStateException("failed to get field value for object=" + object.getClass().getName() + " fieldName=" + fieldName, e);
        }
    }

    public static Object invoke(Object object, String methodName, Object... args) {
        try {
            Method m = object.getClass().getDeclaredMethod(methodName);
            m.setAccessible(true);
            return m.invoke(object, args);
        } catch (Exception e) {
            throw new IllegalStateException("failed to invoke method for object=" + object.getClass().getName() + " methodName=" + methodName, e);
        }
    }
}
