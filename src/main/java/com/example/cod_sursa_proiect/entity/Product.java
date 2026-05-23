package com.example.cod_sursa_proiect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "produse")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pozaUrl;

    @Column(unique = true, nullable = false)
    private String nume;

    private Double pretEur;

    @Column(name = "pret_ron")
    private Double pretRon;

    @Column(name = "curs_valutar_eur_ron")
    private Double cursValutar;

    @Column(length = 1500)
    private String descriere;

    public Product() {}

    public Product(String pozaUrl, String nume, Double pretEur, Double pretRon, Double cursValutar, String descriere) {
        this.pozaUrl = pozaUrl;
        this.nume = nume;
        this.pretEur = pretEur;
        this.pretRon = pretRon;
        this.cursValutar = cursValutar;
        this.descriere = descriere;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPozaUrl() { return pozaUrl; }
    public void setPozaUrl(String pozaUrl) { this.pozaUrl = pozaUrl; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public Double getPretEur() { return pretEur; }
    public void setPretEur(Double pretEur) { this.pretEur = pretEur; }

    public Double getPretRon() { return pretRon; }
    public void setPretRon(Double pretRon) { this.pretRon = pretRon; }

    public Double getCursValutar() { return cursValutar; }
    public void setCursValutar(Double cursValutar) { this.cursValutar = cursValutar; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
}