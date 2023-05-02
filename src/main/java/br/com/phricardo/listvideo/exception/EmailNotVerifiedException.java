package br.com.phricardo.listvideo.exception;

public class EmailNotVerifiedException extends RuntimeException {

    public EmailNotVerifiedException(String message) {
        super(message);
    }

    public EmailNotVerifiedException() {
        super("User email not verified");
    }
}

