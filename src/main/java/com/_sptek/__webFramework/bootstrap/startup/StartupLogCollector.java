package com._sptek.__webFramework.bootstrap.startup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StartupLogCollector {

    // thread-safe Map
    private static final Map<String, List<String>> logMap = new ConcurrentHashMap<>();
    private StartupLogCollector() {} // 인스턴스 생성 방지

    public static void addLog(String key, String message) {
        // computeIfAbsent: 키가 없으면 새 리스트 생성
        logMap.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(message);
    }

    public static List<String> getLogMessages(String key) {
        return new ArrayList<>(logMap.getOrDefault(key, Collections.emptyList()));
    }

    public static Map<String, List<String>> getAllLogMessages() {
        Map<String, List<String>> copy = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : logMap.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    public static void clear(String key) {
        logMap.remove(key);
    }

    public static void clearAll() {
        logMap.clear();
    }
}


//-->HikariPool 로그 파일 적용 해야 함
