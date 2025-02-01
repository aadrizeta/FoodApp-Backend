package com.app.foodapp.dto;

public class ApiDelivery<T> {

    private String message;
    private Boolean success;
    private int status;
    private T data;

    private Object error;

    public ApiDelivery(String message, Boolean success, int status, T data, Object error) {
        this.message = message;
        this.error = error;
        this.data = data;
        this.status = status;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
