package com.dl.dw.event;

public class ScanResultEvent {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ScanResultEvent(String content) {
        this.content = content;
    }
}
