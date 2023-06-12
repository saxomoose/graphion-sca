package io.github.saxomoose.sca.web.utils;

public class Mail {
    private final String subject;
    private final String body;

    public Mail(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String subject() 
    {
        return subject;
    }

    public String body() 
    {
        return body;
    }
}
