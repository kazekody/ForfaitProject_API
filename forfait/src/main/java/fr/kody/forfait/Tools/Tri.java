package fr.kody.forfait.Tools;

import java.util.ArrayList;
import java.util.List;

import fr.kody.forfait.Entity.Forfait;

public class Tri {

    public static List<Forfait> triSms(List<Forfait> list){

        List<Double> listH = new ArrayList<Double>();
                for(Forfait forfait: list ){
                   listH.add(forfait.getSms()/forfait.getPrix());
                }
                for(int j = 0; j <= listH.size() - 1; j++){
                    double courant = listH.get(j);
                    Forfait forfait = list.get(j);
                    int i = j;
                        while(i> 0 && listH.get(i-1) < courant){
                            listH.set(i, listH.get(i-1));
                            list.set(i, list.get(i-1));
                            i--;
                        }
                        listH.set(i, courant);
                        list.set(i,forfait);
                }

        return list;
    } 

    public static List<Forfait> triData(List<Forfait> list){

        List<Double> listH = new ArrayList<Double>();
                for(Forfait forfait: list ){
                   listH.add(forfait.getData()/forfait.getPrix());
                }
                for(int j = 0; j <= listH.size() - 1; j++){
                    double courant = listH.get(j);
                    Forfait forfait = list.get(j);
                    int i = j;
                        while(i> 0 && listH.get(i-1) < courant){
                            listH.set(i, listH.get(i-1));
                            list.set(i, list.get(i-1));
                            i--;
                        }
                        listH.set(i, courant);
                        list.set(i,forfait);
                }

        return list;
    } 


    public static List<Forfait> triAppel(List<Forfait> list){

        List<Double> listH = new ArrayList<Double>();
                for(Forfait forfait: list ){
                   listH.add(forfait.getAppel()/forfait.getPrix());
                }
                for(int j = 0; j <= listH.size() - 1; j++){
                    double courant = listH.get(j);
                    Forfait forfait = list.get(j);
                    int i = j;
                        while(i> 0 && listH.get(i-1) < courant){
                            listH.set(i, listH.get(i-1));
                            list.set(i, list.get(i-1));
                            i--;
                        }
                        listH.set(i, courant);
                        list.set(i,forfait);
                }

        return list;
    } 
    
}