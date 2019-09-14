package ru.ifmo.rain.dolgikh.walk;

class WalkException extends Exception {

    private String message;

    WalkException(String message) {
        super(message);
        this.message = message;
    }

    void printMessage() {
        System.out.println("WalkException: " + message);
    }
}
