package com.mx.logic.controllers.interfaces;

public interface Observable {
    void registerObserver(Observer observer);
    void removeObserver();
    void removeAllObserver();
    void notifyObservers(String message);
}
