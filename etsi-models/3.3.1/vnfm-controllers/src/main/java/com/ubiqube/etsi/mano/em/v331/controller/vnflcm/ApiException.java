package com.ubiqube.etsi.mano.em.v331.controller.vnflcm;

public class ApiException extends Exception {
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
}
