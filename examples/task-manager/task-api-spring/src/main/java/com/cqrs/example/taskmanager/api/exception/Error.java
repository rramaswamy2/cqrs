package com.cqrs.example.taskmanager.api.exception;

public class Error {
    private final String status;
    private final String title;
    private final String detail;

    public Error(String status, String title, String detail) {
        this.status = status;
        this.title = title;
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}
