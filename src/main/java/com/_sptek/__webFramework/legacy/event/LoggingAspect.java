package com._sptek.__webFramework.legacy.event;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;

/**
 * 메서드 호출 추적을 Aspect 방식으로 실험하던 deprecated 코드.
 *
 * <p>{@code @Aspect}와 {@code @Component}가 비활성화되어 현재 런타임에는 등록되지 않는다.
 * 신규 호출 추적 기능은 filter, interceptor, logging 패키지의 활성 구현을 우선 사용한다.</p>
 */
@Slf4j
//@Aspect
//@Component
public class LoggingAspect {
//
//
//    private static final Map<String, Boolean> methodUsageMap = new ConcurrentHashMap<>();
//    private static final String LOG_FILE = "method_usage_log.txt";
//
//    // 특정 패키지(com.example) 하위의 메서드만 추적
//    //@Before("execution(* com.sptek..*(..)) && !execution(* com._sptek.__webFramework.web.filter..*(..))")
//    public Object trackMethodUsage(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
//        methodUsageMap.put(methodName, true);
//
//        return joinPoint.proceed();
//    }
//
//    //@PreDestroy
//    public void saveMethodUsageLog() {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
//            for (Map.Entry<String, Boolean> entry : methodUsageMap.entrySet()) {
//                writer.write(entry.getKey() + "=" + entry.getValue());
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Map<String, Boolean> getMethodUsageMap() {
//        return methodUsageMap;
//    }



//    private static final ThreadLocal<List<String>> callStack = ThreadLocal.withInitial(ArrayList::new);
//    private static final String TARGET_PACKAGE_PREFIX = "com.sptek";
//
//    @After("execution(* com.sptek..*(..)) && !execution(* com._sptek.__webFramework.web.filter..*(..)) && !execution(* com.sptek._projectCommon.filter..*(..))")
//    public Object trackMethodUsage(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
//
//        if (methodName.startsWith(TARGET_PACKAGE_PREFIX)) {
//            callStack.get().add(methodName);
//        }
//
//        try {
//            return joinPoint.proceed();
//        } finally {
//            // Remove method from call stack after execution
//            callStack.get().remove(methodName);
//        }
//    }
//
//    public static List<String> getCallStack() {
//        return new ArrayList<>(callStack.get());
//    }
//
//    public static void clearCallStack() {
//        callStack.get().clear();
//    }

    /**
     * Aspect가 다시 활성화된 경우 com.sptek 하위 메서드 호출명을 debug 로그로 남긴다.
     */
    @After("execution(* com.sptek..*(..)) && !within(com._sptek.__webFramework.web.filter..*) && !within(com.sptek._projectCommon.filter..*)")
    public void logMethodCall(JoinPoint joinPoint) {
        // Get the method signature
        String methodName = joinPoint.getSignature().toShortString();

        // Log the method name
        log.debug("Method called: " + methodName);
    }
}
