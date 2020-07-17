package fr.kody.forfait.Service;

import java.util.List;

import fr.kody.forfait.Entity.Forfait;


public interface IForfaitService {

    public void ajouterForfait (Forfait forfait);
    
    public List<Forfait> listerForfait();

    public List<Forfait> listerForfaitMango();

    public List<Forfait> listerForfaitHemle();

    public void modifierForfait();

    public void supprimerForfait(String intitule);
}