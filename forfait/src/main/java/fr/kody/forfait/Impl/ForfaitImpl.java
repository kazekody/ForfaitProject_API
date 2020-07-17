package fr.kody.forfait.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.kody.forfait.Entity.Forfait;
import fr.kody.forfait.Repository.IForfaitRepository;
import fr.kody.forfait.Service.IForfaitService;

@Service
public class ForfaitImpl implements IForfaitService {

    @Autowired
	public IForfaitRepository forfaitRepository;

    @Override
    public void ajouterForfait(Forfait forfait) {
      forfaitRepository.save(forfait);

    }

    @Override
    public List<Forfait> listerForfait() {
        List<Forfait> list = forfaitRepository.findAll();
        return list;
    }

    @Override
    public void modifierForfait() {

    }

    @Override
    public void supprimerForfait(String intitule) {
        forfaitRepository.supprimerForfait(intitule);
    }

    @Override
    public List<Forfait> listerForfaitMango() {
        List <Forfait> listMango = forfaitRepository.listerForfaitMango();
        return listMango;
    }

    @Override
    public List<Forfait> listerForfaitHemle() {
        List <Forfait> listHemle = forfaitRepository.listerForfaitHemle();
        return listHemle;
    }


    
}