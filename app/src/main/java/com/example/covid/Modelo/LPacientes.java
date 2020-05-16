package com.example.covid.Modelo;

public class LPacientes {
    private String key;
    private Pacientes pacientes;

    public LPacientes(String key, Pacientes pacientes) {
        this.key = key;
        this.pacientes = pacientes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Pacientes getPacientes() {
        return pacientes;
    }

    public void setPacientes(Pacientes pacientes) {
        this.pacientes = pacientes;
    }
}
