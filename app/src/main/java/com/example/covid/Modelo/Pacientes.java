package com.example.covid.Modelo;

public class Pacientes {
    private String direccion;
    private String nombreCompleto;
    private String fechaDeIngreso;
    private String fotoCedula;
    private String cedula;

    public Pacientes() {
    }

    public Pacientes(String direccion, String nombreCompleto, String fechaDeIngreso, String fotoCedula, String cedula) {
        this.direccion = direccion;
        this.nombreCompleto = nombreCompleto;
        this.fechaDeIngreso = fechaDeIngreso;
        this.fotoCedula = fotoCedula;
        this.cedula = cedula;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFechaDeIngreso() {
        return fechaDeIngreso;
    }

    public void setFechaDeIngreso(String fechaDeIngreso) {
        this.fechaDeIngreso = fechaDeIngreso;
    }

    public String getFotoCedula() {
        return fotoCedula;
    }

    public void setFotoCedula(String fotoCedula) {
        this.fotoCedula = fotoCedula;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
}
