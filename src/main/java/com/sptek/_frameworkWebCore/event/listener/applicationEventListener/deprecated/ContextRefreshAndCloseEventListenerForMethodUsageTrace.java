package com.sptek._frameworkWebCore.event.listener.applicationEventListener.deprecated;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * context refresh 시 클래스와 메서드 목록을 파일로 생성하던 deprecated 추적 코드.
 *
 * <p>{@code @Component}와 {@code @EventListener}가 비활성화되어 현재 런타임에는 동작하지 않는다.
 * 파일 시스템을 직접 생성하는 방식이므로 신규 사용보다 logging 패키지의 활성 진단 기능을 우선 검토한다.</p>
 */
@Slf4j
//@Component
public class ContextRefreshAndCloseEventListenerForMethodUsageTrace {

    private static final String BASE_DIR = "/methodUsageLogging/";

    //@EventListener
    /**
     * 활성화된 경우 com.sptek 하위 classpath를 스캔해 메서드 추적용 파일 구조를 생성한다.
     */
    public void handleContextRefresh(ContextRefreshedEvent event) {
        try {
            List<Class<?>> classes = getClasses("com.sptek");
            log.debug("Classes under com.sptek package:");
            createDirectoriesAndFiles(BASE_DIR, classes);

        } catch (Exception e) {
            log.error("An error occurred while scanning for classes", e);
        }
    }

    private List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

    public void createDirectoriesAndFiles(String basePath, List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            String packagePath = clazz.getPackage().getName().replace('.', File.separatorChar);
            String className = clazz.getSimpleName();
            String fullClassPath = basePath + File.separator + packagePath + File.separator + className;

            // Create the directory for the class
            File classDir = new File(fullClassPath);
            if (!classDir.exists()) {
                boolean dirsCreated = classDir.mkdirs();
                if (dirsCreated) {
                    System.out.println("Directory created: " + classDir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create directory: " + classDir.getAbsolutePath());
                    continue;
                }
            }

            // Create files for each method in the class
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                String params = getParameterTypesAsString(method);
                String fileName = className + "_" + methodName + "_" + params + ".txt";
                File methodFile = new File(classDir, fileName);
                try {
                    boolean fileCreated = methodFile.createNewFile();
                    if (fileCreated) {
                        System.out.println("File created: " + methodFile.getAbsolutePath());
                    } else {
                        System.err.println("File already exists: " + methodFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    System.err.println("Failed to create file: " + methodFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Converts the parameter types of a method to a string representation.
     *
     * @param method The method whose parameter types are to be converted.
     * @return A string representation of the parameter types.
     */
    private String getParameterTypesAsString(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        StringBuilder params = new StringBuilder();
        for (Class<?> paramType : paramTypes) {
            if (params.length() > 0) {
                params.append(",");
            }
            params.append(paramType.getSimpleName());
        }
        return params.toString();
    }

}
