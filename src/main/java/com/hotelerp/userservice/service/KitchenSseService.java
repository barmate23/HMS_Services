package com.hotelerp.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelerp.userservice.dto.KitchenOrderCardDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all active SSE connections for the Kitchen Display System (KDS).
 *
 * <p>Clients connect via GET /getKitchenOrdersStream and receive push updates
 * whenever a new order is created or a KOT status changes.</p>
 *
 * <p>Key: outletId (null = ALL outlets). Each key holds a list of emitters so
 * multiple KDS screens per outlet are supported simultaneously.</p>
 */
@Service
@Slf4j
public class KitchenSseService {

    /** outletId → list of active SSE emitters. Null key = "subscribe to all outlets". */
    private final Map<Long, List<SseEmitter>> outletEmitters  = new ConcurrentHashMap<>();
    private final List<SseEmitter>            globalEmitters  = new CopyOnWriteArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // registers JavaTimeModule for LocalDateTime

    // ──────────────────────────────────────────────────────────────────────
    //  SUBSCRIBE
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Register a new SSE connection for the given outlet (or all outlets if outletId is null).
     * Timeout = 30 minutes; the client should reconnect automatically (EventSource default).
     */
    public SseEmitter subscribe(Long outletId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30 minutes

        List<SseEmitter> target = getOrCreate(outletId);
        target.add(emitter);

        // Clean up on completion / timeout / error
        Runnable cleanup = () -> {
            target.remove(emitter);
            log.debug("SSE emitter removed for outletId={}", outletId);
        };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> {
            cleanup.run();
            log.debug("SSE error for outletId={}: {}", outletId, e.getMessage());
        });

        // Send an initial "connected" heartbeat so the client knows it's alive
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"status\":\"connected\",\"outletId\":" + outletId + "}"));
        } catch (IOException e) {
            log.warn("Failed to send SSE connection ack: {}", e.getMessage());
        }

        log.info("New SSE subscriber for outletId={}", outletId);
        return emitter;
    }

    // ──────────────────────────────────────────────────────────────────────
    //  BROADCAST
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Broadcast a KDS update to all subscribers of the given outlet AND all global subscribers.
     *
     * @param outletId   the outlet the order belongs to
     * @param eventType  "NEW_ORDER" | "KOT_STATUS_CHANGED" | "ORDER_UPDATED"
     * @param cards      the current active kitchen cards to push (same data as GET endpoint)
     */
    public void broadcast(Long outletId, String eventType, List<KitchenOrderCardDTO> cards) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(cards);
        } catch (Exception e) {
            log.error("Failed to serialize kitchen cards for SSE broadcast: {}", e.getMessage());
            return;
        }

        // Subscribers watching a specific outlet
        if (outletId != null) {
            sendToAll(getOrCreate(outletId), eventType, payload, outletId);
        }

        // Global subscribers (outletId = null)
        sendToAll(globalEmitters, eventType, payload, null);
    }

    // ──────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ──────────────────────────────────────────────────────────────────────

    private void sendToAll(List<SseEmitter> emitters, String eventType, String payload, Long outletId) {
        List<SseEmitter> dead = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventType)
                        .data(payload));
            } catch (IOException | IllegalStateException e) {
                dead.add(emitter);
                log.debug("Removing stale SSE emitter for outletId={}: {}", outletId, e.getMessage());
            }
        }
        emitters.removeAll(dead);
    }

    private List<SseEmitter> getOrCreate(Long outletId) {
        if (outletId == null) return globalEmitters;
        return outletEmitters.computeIfAbsent(outletId, k -> new CopyOnWriteArrayList<>());
    }
}
