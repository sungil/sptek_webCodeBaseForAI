package com._sptek.__webFramework.core.constant;

/**
 * 프레임워크가 애노테이션과 프로젝트 패키지 경계를 판별할 때 사용하는 패키지 이름 상수.
 */
public final class FrameworkPackageConstants {
    public static final String PROJECT_PACKAGE_NAME = "com._sptek.";
    public static final String FRAMEWORK_WEBCORE_PACKAGE_NAME = PROJECT_PACKAGE_NAME + "__webFramework.";
    public static final String FRAMEWORK_ANNOTATION_PACKAGE_NAME = FRAMEWORK_WEBCORE_PACKAGE_NAME;

    private FrameworkPackageConstants() {
    }
}
