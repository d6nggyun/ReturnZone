package _ITHON.ReturnZone.domain.lostpost.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {

    public static String getTimeAgo(LocalDateTime createdAt) {

        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        if (minutes < 60) return minutes + "분 전";
        long hours = duration.toHours();
        if (hours < 24) return hours + "시간 전";
        long days = duration.toDays();
        return days + "일 전";
    }
}
