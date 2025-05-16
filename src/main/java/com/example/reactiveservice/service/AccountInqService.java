package com.example.reactiveservice.service;

public class AccountInqService {

    private final RabbitTemplate rabbitTemplate;

    private final ConcurrentMap<String, CompletableFuture<Object>> pendingMap = new ConcurrentHashMap<>();

    public Mono<Object> sendAsync(String payload) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingMap.put(requestId, future);

        // Khởi tạo message: chạy ngay trong thread WebFlux (non-blocking)
        MessageProperties props = new MessageProperties();
        props.setCorrelationId(requestId.getBytes(StandardCharsets.UTF_8));
        props.setReplyTo("queue.rpc.reply");
        Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);

        // Chỉ gửi RabbitMQ là blocking → đẩy sang boundedElastic
        Mono<Void> sendMono = Mono.fromRunnable(() -> {
            rabbitTemplate.send("exchange.rpc", "queue.rpc", message);
        }).subscribeOn(Schedulers.boundedElastic());

        return sendMono.then(Mono.fromFuture(future));
        return Mono.fromFuture(future);
    }
    public void completeResponse(String requestId, Object result) {
        CompletableFuture<Object> future = pendingMap.remove(requestId);
        if (future != null) {
            future.complete(result);
        }
    }
}
