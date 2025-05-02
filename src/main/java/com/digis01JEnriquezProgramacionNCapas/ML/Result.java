package com.digis01JEnriquezProgramacionNCapas.ML;

import java.util.List;


public class Result<T> {
    public boolean correct;
    public String errorMessage; //Mensaje descriptivo
    public Exception ex;//Maneja toda la exception
    public T object;
    public List<T> objects;
}
