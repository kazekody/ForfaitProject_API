package fr.kody.forfait.Entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Forfait")
public class Forfait implements Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="IdForfait", unique = true, updatable = false, nullable = false)
    private long IdForfait;

    @Column(name = "Intitule")
    private String intitule;

    @Column(name = "Sms")
    private double sms;

    @Column(name = "Appel")
    private double appel;

    @Column(name = "Data")
    private double data;

    @Column(name = "Prix")
    private double prix;

    @Column(name = "Validite")
    private double validite;

    public double getValidite() {
        return this.validite;
    }

    public void setValidite(double validite) {
        this.validite = validite;
    }

    public double getPrix() {
        return this.prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public double getData() {
        return this.data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public double getAppel() {
        return this.appel;
    }

    public void setAppel(double appel) {
        this.appel = appel;
    }

    public double getSms() {
        return this.sms;
    }

    public void setSms(double sms) {
        this.sms = sms;
    }

    public String getIntitule() {
        return this.intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public long getIdForfait() {
        return this.IdForfait;
    }

    public void setIdForfait(long IdForfait) {
        this.IdForfait = IdForfait;
    }

    @Override
    public String toString() {
        return "{" +
            " IdForfait='" + getIdForfait() + "'" +
            ", intitule='" + getIntitule() + "'" +
            ", sms='" + getSms() + "'" +
            ", appel='" + getAppel() + "'" +
            ", data='" + getData() + "'" +
            ", prix='" + getPrix() + "'" +
            ", validite='" + getValidite() + "'" +
            "}";
    }

    public Forfait() {
 
    }

    
}