package com.ubiqube.etsi.mano.nfvo.v331.controller.nsd;

public class ApiException extends Exception {
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
}
