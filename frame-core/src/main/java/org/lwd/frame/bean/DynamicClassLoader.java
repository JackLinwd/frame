package org.lwd.frame.bean;


import org.lwd.frame.util.Io;
import org.lwd.frame.util.Logger;

import java.io.*;

/**
 * @author lwd
 */
public class DynamicClassLoader extends ClassLoader {
    private Io io;
    private Logger logger;
    private ClassReloader reloader;

    DynamicClassLoader(ClassLoader parent) {
        super(parent);

        io = BeanFactory.getBean(Io.class);
        logger = BeanFactory.getBean(Logger.class);
        reloader = BeanFactory.getBean(ClassReloader.class);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (!reloader.isReloadEnable(name))
            return findLoadedClassByName(name);

        Class<?> clazz = findLoadedClass(name);
        if (clazz != null)
            return clazz;

        try {
            byte[] by = read(name);
            clazz = defineClass(name, by, 0, by.length);
            resolveClass(clazz);

            return clazz;
        } catch (Exception e) {
            logger.warn(e, "加载类[{}]时发生异常！", name);

            return null;
        }
    }

    private Class<?> findLoadedClassByName(String name) throws ClassNotFoundException {
        Class<?> clazz = getParent().loadClass(name);
        if (clazz != null)
            return clazz;

        return findSystemClass(name);
    }

    private byte[] read(String name) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = new FileInputStream(reloader.getClassPath() + File.separatorChar + name.replace('.', File.separatorChar) + ".class");
        io.copy(input, output);
        input.close();
        output.close();

        return output.toByteArray();
    }
}
