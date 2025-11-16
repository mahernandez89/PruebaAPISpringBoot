package com.ejemplo.PruebaAPISpringBoot.dto;

public class PaymentResponseDto {
    private boolean success;
    private String message;
    private Long orderId;

    public PaymentResponseDto() {}
    public PaymentResponseDto(boolean success, String message, Long orderId) {
        this.success = success;
        this.message = message;
        this.orderId = orderId;
    }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
