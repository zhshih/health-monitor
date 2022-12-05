package com.example.notification.repository;

import com.example.notification.model.Notification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Repository
// FIXME
public class NotificationRepository {

    List<Notification> notificationList = new ArrayList<>();

    public NotificationRepository() {
        List<Notification.Status> statusList = Arrays.asList(Notification.Status.ONGOING, Notification.Status.CLOSED);
        List<Notification.Severity> severityList = Arrays.asList(
                Notification.Severity.MODERATE, Notification.Severity.SEVERE, Notification.Severity.CRITICAL
        );
        for (int i = 1; i < 1000; i++) {
            Notification notification = Notification.builder()
                    .id(Integer.toUnsignedLong(i))
                    .name("test notification " + i)
                    .status(statusList.get(ThreadLocalRandom.current().nextInt(0, statusList.size())))
                    .severity(severityList.get(ThreadLocalRandom.current().nextInt(0, severityList.size())))
                    .issuedDatetime(LocalDateTime.now())
                    .description("this is a test notification for id = " + i)
                    .build();
            notificationList.add(notification);
        }
    }

    public List<Notification> findAll() {
        return notificationList;
    }

    public List<Notification> findByOngoing() {
        return notificationList.stream()
                .filter(notification -> notification.getStatus() == Notification.Status.ONGOING)
                .collect(Collectors.toList());
    }

    public List<Notification> findByOngoingAmount(int amount) {
        List<Notification> subNotifications = notificationList.stream()
                .filter(notification -> notification.getStatus() == Notification.Status.ONGOING)
                .collect(Collectors.toList());
        if (amount > subNotifications.size()) amount = subNotifications.size();
        return subNotifications
                .subList(0, amount);
    }

    public List<Notification> findByClosed() {
        return notificationList.stream()
                .filter(notification -> notification.getStatus() == Notification.Status.CLOSED)
                .collect(Collectors.toList());
    }

    public List<Notification> findByClosedAmount(int amount) {
        List<Notification> subNotifications = notificationList.stream()
                .filter(notification -> notification.getStatus() == Notification.Status.CLOSED)
                .collect(Collectors.toList());
        if (amount > subNotifications.size()) amount = subNotifications.size();
        return subNotifications
                .subList(0, amount);
    }

    public List<Notification> findByModerate() {
        return notificationList.stream()
                .filter(notification -> notification.getSeverity() == Notification.Severity.MODERATE)
                .collect(Collectors.toList());
    }

    public List<Notification> findByModerateAmount(int amount) {
        List<Notification> subNotifications = notificationList.stream()
                .filter(notification -> notification.getSeverity() == Notification.Severity.MODERATE)
                .collect(Collectors.toList());
        if (amount > subNotifications.size()) amount = subNotifications.size();
        return subNotifications
                .subList(0, amount);
    }

    public List<Notification> findBySevere() {
        return notificationList.stream()
                .filter(notification -> notification.getSeverity() == Notification.Severity.SEVERE)
                .collect(Collectors.toList());
    }

    public List<Notification> findBySevereAmount(int amount) {
        List<Notification> subNotifications = notificationList.stream()
                .filter(notification -> notification.getSeverity() == Notification.Severity.SEVERE)
                .collect(Collectors.toList());
        if (amount > subNotifications.size()) amount = subNotifications.size();
        return subNotifications
                .subList(0, amount);
    }

    public List<Notification> findByCritical() {
        return notificationList.stream()
                .filter(notification -> notification.getSeverity() == Notification.Severity.CRITICAL)
                .collect(Collectors.toList());
    }

    public List<Notification> getFindByCriticalAmount(int amount) {
        List<Notification> subNotifications = notificationList.stream()
                .filter(notification -> notification.getSeverity() == Notification.Severity.CRITICAL)
                .collect(Collectors.toList());
        if (amount > subNotifications.size()) amount = subNotifications.size();
        return subNotifications
                .subList(0, amount);
    }

    public List<Notification> findFirstN(int amount) {
        if (amount > notificationList.size()) amount = notificationList.size();
        return notificationList.subList(0, amount);
    }
}
