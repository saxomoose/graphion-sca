package com.saxomoose.eid.web.dtos;

import com.saxomoose.eid.web.enums.ToastType;

public class Toast {
    private final ToastType toastType;
    private final String header;
    private final String body;

    public Toast(ToastType toastType, String header, String body) {
        this.toastType = toastType;
        this.header = header;
        this.body = body;
    }

    public ToastType getToastType() { return toastType; }

    public String getHeader() { return header; }

    public String getBody() { return body; }
}
