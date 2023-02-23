package com.saxomoose.eid.web.enums;

import com.google.common.net.MediaType;

public enum SupportedMediaType {
    PDF(0);

    private MediaType mediaType;

    SupportedMediaType(int index) {
        if (index == 0) {
            this.mediaType = MediaType.PDF;
        }
    }

    public MediaType getMediaType() {
        return this.mediaType;
    }
}
