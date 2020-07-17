package fr.kody.forfait.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import fr.kody.forfait.Entity.Forfait;
import fr.kody.forfait.Service.IForfaitService;
import fr.kody.forfait.Tools.Response;
import fr.kody.forfait.Tools.Tri;

@CrossOrigin
@RestController
@RequestMapping("/GestionForfait")
public class ControllerForfait {

    @Autowired
    IForfaitService forfaitService;

    @Autowired
	   RestTemplate restTemplate;

    String url1 = "http://localhost:8080/GestionForfait/ListerForfaitMango";
    String url2 = "http://localhost:8080/GestionForfait/ListerForfaitHemle";

    @CrossOrigin
    @PostMapping("/AjouterForfait/{intitule}/{appel}/{sms}/{data}/{prix}/{validite}")
    public Response ajouterForfait(@PathVariable("intitule") String intitule, @PathVariable("appel") Double appel,
            @PathVariable("sms") Double sms, @PathVariable("data") Double data, @PathVariable("prix") Double prix,
            @PathVariable("validite") Double validite) {
        Response reponse = new Response();
        Forfait forfait = new Forfait();
        forfait.setIntitule(intitule);
        forfait.setAppel(appel);
        forfait.setSms(sms);
        forfait.setData(data);
        forfait.setPrix(prix);
        forfait.setValidite(validite);

        forfaitService.ajouterForfait(forfait);

        reponse.setReturnValue(forfait);
        return reponse;
    }

    @CrossOrigin
    @GetMapping("/ListerForfait")
    public Response listerForfait() {
        Response reponse = new Response();
        reponse.setReturnValue(forfaitService.listerForfait());
        return reponse;

    }

    @CrossOrigin
    @GetMapping("/ListerForfaitMango")
    public Response listerForfaitMango() {
        Response listMango = new Response();
        listMango.setReturnValue(forfaitService.listerForfaitMango());
     return listMango;
        
    }

    @CrossOrigin
    @GetMapping("/ListerForfaitHemle")
    public Response listerForfaitHemle() {
        Response listHemle = new Response();
        listHemle.setReturnValue(forfaitService.listerForfaitHemle());
        return listHemle;
    }

    @CrossOrigin
    @PostMapping("/MeilleurForfait/{montant}/{dure}/{prioriteData}/{prioriteSms}/{prioriteAppel}")
    public Response meilleurForfait(@PathVariable("montant") double montant, @PathVariable("dure")double dure,
    @PathVariable("prioriteData")double prioriteData, @PathVariable("prioriteSms")double prioriteSms,
    @PathVariable("prioriteAppel")double prioriteAppel) {
        double recharge=0;
        Response Mango, Hemle, reponse = new Response();
        List<Forfait> listMango = new ArrayList<Forfait>();
        List<Forfait>  listHemle = new ArrayList<Forfait>();
        Mango = restTemplate.getForObject(this.url1, Response.class);
        Hemle = restTemplate.getForObject(this.url2, Response.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            listMango = Arrays.asList(mapper.readValue(mapper.writeValueAsString(Mango.getReturnValue()), Forfait[].class));
            listHemle = Arrays.asList(mapper.readValue(mapper.writeValueAsString(Hemle.getReturnValue()), Forfait[].class));
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
        }
        List<Forfait> minListHemle = new ArrayList<Forfait>();
        List<Forfait> minListMango = new ArrayList<Forfait>();
        
        for(Forfait forfait: listMango){
            if (forfait.getValidite() >= dure && forfait.getPrix() <= montant){
                minListMango.add(forfait);
             }
        }

        for(Forfait forfait: listHemle){
            if (forfait.getValidite() >= dure && forfait.getPrix() <= montant){
                minListHemle.add(forfait);
             }
        }
        System.out.println(minListMango.size());
        System.out.println(minListHemle.size());
        double[] tabPriorite = new double[3];
        tabPriorite[0] = prioriteAppel;
        tabPriorite[1] = prioriteSms;
        tabPriorite[2] = prioriteData;
        /*System.out.println(Tri.triSms(minListHemle));
        System.out.println(Tri.triSms(minListMango));*/


         if (prioriteAppel != prioriteData && prioriteAppel != prioriteSms && prioriteData != prioriteSms){
            if(tabPriorite[0] > tabPriorite[1] && tabPriorite[1] > tabPriorite[2]){

            }
            if(tabPriorite[1] > tabPriorite[2] && tabPriorite[2] > tabPriorite[0]){
                
            }
            if(tabPriorite[2] > tabPriorite[1] && tabPriorite[1] > tabPriorite[0]){
                
            }
            if(tabPriorite[0] > tabPriorite[2] && tabPriorite[2] > tabPriorite[1]){
                
            }
            if(tabPriorite[1] > tabPriorite[0] && tabPriorite[0] > tabPriorite[2]){
               List <Forfait> listH1 = new ArrayList<Forfait>();
               List <Forfait> listM1 = new ArrayList<Forfait>();
               List <Forfait> listH2 = new ArrayList<Forfait>();
               List <Forfait> listM2 = new ArrayList<Forfait>();
               List <Forfait> listH3 = new ArrayList<Forfait>();
               List <Forfait> listM3 = new ArrayList<Forfait>();
               listH1 = Tri.triSms(minListHemle);
               listM1 = Tri.triSms(minListMango);
               listH2 = Tri.triAppel(minListHemle);
               listM2 = Tri.triAppel(minListMango);
               listH3 = Tri.triData(minListHemle);
               listM3 = Tri.triData(minListMango);
                int[][] tabOccurH = new int[minListHemle.size()][minListHemle.size()];
                int[][] tabOccurM = new int[minListHemle.size()][minListHemle.size()];
                int k = 0;
                int c;
                do {
                     c = 0;
                    for(int i = 0; i <= listH1.size() -1 ; i++){
                        if (listH1.get(i).getPrix() < recharge){
                            tabOccurH[0][k] = (int) listH1.get(i).getIdForfait();
                            tabOccurH[1][k] = tabOccurH[1][k] +1 ;
                            recharge = recharge - listH1.get(i).getPrix();
                            c++;
                            k++;
                            i = listH1.size();
                        }
                    }

                    for(int i = 0; i <= listH2.size() -1 ; i++){
                        if (listH1.get(i).getPrix() < recharge){
                            tabOccurH[0][k] = (int) listH2.get(i).getIdForfait();
                            tabOccurH[1][k] = tabOccurH[1][k] +1 ;
                            recharge = recharge - listH2.get(i).getPrix();
                            c++;
                            k++;
                            i = listH2.size();
                        }
                    }

                    for(int i = 0; i <= listH3.size() -1 ; i++){
                        if (listH1.get(i).getPrix() < recharge){
                            tabOccurH[0][k] = (int) listH3.get(i).getIdForfait();
                            tabOccurH[1][k] = tabOccurH[1][k] +1 ;
                            recharge = recharge - listH3.get(i).getPrix();
                            c++;
                            k++;
                            i = listH3.size();
                        }
                    }
                    
                }while(recharge!=0 && c!=0);
                
                do {
                    c = 0;
                   for(int i = 0; i <= listM1.size() -1 ; i++){
                       if (listM1.get(i).getPrix() < recharge){
                           tabOccurM[0][k] = (int) listM1.get(i).getIdForfait();
                           tabOccurM[1][k] = tabOccurH[1][k] +1 ;
                           recharge = recharge - listM1.get(i).getPrix();
                           c++;
                           k++;
                           i = listM1.size();
                       }
                   }

                   for(int i = 0; i <= listM2.size() -1 ; i++){
                       if (listM2.get(i).getPrix() < recharge){
                           tabOccurM[0][k] = (int) listM2.get(i).getIdForfait();
                           tabOccurM[1][k] = tabOccurH[1][k] +1 ;
                           recharge = recharge - listM2.get(i).getPrix();
                           c++;
                           k++;
                           i = listM2.size();
                       }
                   }

                   for(int i = 0; i <= listM3.size() -1 ; i++){
                       if (listM3.get(i).getPrix() < recharge){
                           tabOccurM[0][k] = (int) listM3.get(i).getIdForfait();
                           tabOccurM[1][k] = tabOccurH[1][k] +1 ;
                           recharge = recharge - listM3.get(i).getPrix();
                           c++;
                           k++;
                           i = listM3.size();
                       }
                   }
                   
               }while(recharge!=0 && c!=0);
                 
            }
            if(tabPriorite[2] > tabPriorite[0] && tabPriorite[0] > tabPriorite[1]){
                
            }
         }

        

        return reponse;

        }
}