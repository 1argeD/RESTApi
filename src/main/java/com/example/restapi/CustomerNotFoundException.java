package com.example.restapi;


public class CustomerNotFoundException extends Exception {

    String code;
    String message;
    String error;

    CustomerNotFoundException() {}
    CustomerNotFoundException(String code, String message, String error){
        this.code = code;
        this.message = message;
        this.error = error;
    }

      CustomerNotFoundException(Long id) {
        this.code = "404";
        this.error = "Not Found Exception";
        this.message = "can't find this : "+id.toString() + "  Customer";
    }

}
