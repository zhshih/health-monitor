package com.example.notification.web;

import com.example.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class NotificationRequestHandler {

    @Autowired
    private NotificationRepository notificationRepository;

    Mono<ServerResponse> handleNotification(ServerRequest r) {
        var limit = Integer.parseInt(r.queryParam("limit").orElse("1000"));
        var status = r.queryParam("status");
        var severity = r.queryParam("severity");
        log.info("limit = {}", limit);
        log.info("status = {}", status);
        log.info("severity = {}", severity);
        if (!status.isPresent() && !severity.isPresent())
            return ok().bodyValue(notificationRepository.findFirstN(limit));
        else if (!status.isPresent() && severity.isPresent()) {
            if (severity.get().equals("moderate"))
                return ok().bodyValue(notificationRepository.findByModerateAmount(limit));
            else if  (severity.get().equals("severe"))
                return ok().bodyValue(notificationRepository.findBySevereAmount(limit));
            else
                return ok().bodyValue(notificationRepository.getFindByCriticalAmount(limit));
        }
        else if (status.isPresent() && !severity.isPresent()) {
            if (status.get().equals("ongoing"))
                return ok().bodyValue(notificationRepository.findByOngoingAmount(limit));
            else
                return ok().bodyValue(notificationRepository.findByClosedAmount(limit));
        }
        else {
            // FIXME
            return ok().bodyValue(notificationRepository.findFirstN(limit));
        }
    }

    Mono<ServerResponse> handleWithModerate(ServerRequest r) {
        return ok().bodyValue(notificationRepository.findByModerate());
    }

    Mono<ServerResponse> handleWithModerateAmount(ServerRequest r) {
        var amount = Integer.parseInt(r.pathVariable("amount"));
        return ok().bodyValue(notificationRepository.findByModerateAmount(amount));
    }

    Mono<ServerResponse> handleWithSevere(ServerRequest r) {
        return ok().bodyValue(notificationRepository.findBySevere());
    }

    Mono<ServerResponse> handleWithSevereAmount(ServerRequest r) {
        var amount = Integer.parseInt(r.pathVariable("amount"));
        return ok().bodyValue(notificationRepository.findBySevereAmount(amount));
    }

    Mono<ServerResponse> handleWithCritical(ServerRequest r) {
        return ok().bodyValue(notificationRepository.findByCritical());
    }

    Mono<ServerResponse> handleWithCriticalAmount(ServerRequest r) {
        var amount = Integer.parseInt(r.pathVariable("amount"));
        return ok().bodyValue(notificationRepository.getFindByCriticalAmount(amount));
    }

    Mono<ServerResponse> handleByNotificationId(ServerRequest r) {
        var id = r.pathVariable("notificationId");
        return ok().bodyValue(id+"\n");
    }
}
