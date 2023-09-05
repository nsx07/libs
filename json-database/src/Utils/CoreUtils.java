package Utils;

import java.net.URL;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class CoreUtils {


    public ArrayList<Class<?>> getClasses(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resoures = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resoures.hasMoreElements()) {
            URL resource = resoures.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directoru: dirs) {
            classes.addAll(findClasses(directoru, packageName));
        }

        return classes;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws IOException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

}