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
        /*System.out.println(minListMango.size());
        System.out.println(minListHemle.size());*/
        double[] tabPriorite = new double[3];
        tabPriorite[0] = prioriteAppel;
        tabPriorite[1] = prioriteSms;
        tabPriorite[2] = prioriteData;
        /*System.out.println(Tri.triSms(minListHemle));
        System.out.println(Tri.triSms(minListMango));*/


         if (prioriteAppel != prioriteData && prioriteAppel != prioriteSms && prioriteData != prioriteSms){
            if(tabPriorite[0] > tabPriorite[1] && tabPriorite[1] > tabPriorite[2]){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triAppel(minListHemle));
                listM1.addAll(Tri.triAppel(minListMango));
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                         }
                     }
 
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             
                             i = listH2.size();
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             
                             i = listH3.size();
                         }
                     }
                 }while(rechargeH!=0 && c!=0);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);

                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
            if(tabPriorite[1] > tabPriorite[2] && tabPriorite[2] > tabPriorite[0]){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triData(minListHemle));
                listM2.addAll(Tri.triData(minListMango));
                listH3.addAll(Tri.triAppel(minListHemle));
                listM3.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                         }
                     }
 
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             
                             i = listH2.size();
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             
                             i = listH3.size();
                         }
                     }
                 }while(rechargeH!=0 && c!=0);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);

                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
            if(tabPriorite[2] > tabPriorite[1] && tabPriorite[1] > tabPriorite[0]){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triData(minListHemle));
                listM1.addAll(Tri.triData(minListMango));
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triAppel(minListHemle));
                listM3.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                         }
                     }
 
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             
                             i = listH2.size();
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             
                             i = listH3.size();
                         }
                     }
                 }while(rechargeH!=0 && c!=0);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);

                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
            if(tabPriorite[0] > tabPriorite[2] && tabPriorite[2] > tabPriorite[1]){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triAppel(minListHemle));
                listM1.addAll(Tri.triAppel(minListMango));
                listH2.addAll(Tri.triData(minListHemle));
                listM2.addAll(Tri.triData(minListMango));
                listH3.addAll(Tri.triSms(minListHemle));
                listM3.addAll(Tri.triSms(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                         }
                     }
 
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             
                             i = listH2.size();
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             
                             i = listH3.size();
                         }
                     }
                 }while(rechargeH!=0 && c!=0);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);

                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
            if(tabPriorite[1] > tabPriorite[0] && tabPriorite[0] > tabPriorite[2]){
               List <Forfait> listH1 = new ArrayList<Forfait>();
               List <Forfait> listM1 = new ArrayList<Forfait>();
               List <Forfait> listH2 = new ArrayList<Forfait>();
               List <Forfait> listM2 = new ArrayList<Forfait>();
               List <Forfait> listH3 = new ArrayList<Forfait>();
               List <Forfait> listM3 = new ArrayList<Forfait>();
               listH1.addAll(Tri.triSms(minListHemle));
               listM1.addAll(Tri.triSms(minListMango));
               listH2.addAll(Tri.triAppel(minListHemle));
               listM2.addAll(Tri.triAppel(minListMango));
               listH3.addAll(Tri.triData(minListHemle));
               listM3.addAll(Tri.triData(minListMango));
               List <Forfait> FlistH = new ArrayList<Forfait>();
               List <Forfait> FlistM = new ArrayList<Forfait>();
               double rechargeH = montant;
               double rechargeM = montant;
                int c;
                do {
                     c = 0;
                    for(int i = 0; i <= listH1.size() -1 ; i++){
                        if (listH1.get(i).getPrix() <= rechargeH){
                           FlistH.add(listH1.get(i));
                           rechargeH = rechargeH - listH1.get(i).getPrix();
                            c++;
                            i = listH1.size();
                        }
                    }

                    for(int i = 2; i <= listH2.size() -1 ; i++){
                        if (listH2.get(i).getPrix() <=rechargeH){
                            FlistH.add(listH2.get(i));
                            rechargeH = rechargeH - listH2.get(i).getPrix();
                            c++;
                            
                            i = listH2.size();
                        }
                    }

                    for(int i = 3; i <= listH3.size() -1 ; i++){
                        if (listH3.get(i).getPrix() <=rechargeH){
                            FlistH.add(listH3.get(i));
                            rechargeH = rechargeH - listH3.get(i).getPrix();
                            c++;
                            
                            i = listH3.size();
                        }
                    }
                }while(rechargeH!=0 && c!=0);
                
                do {
                    c = 0;
                   for(int i = 0; i <= listM1.size() -1 ; i++){
                       if (listM1.get(i).getPrix() <=rechargeM){
                        FlistM.add(listM1.get(i));
                        rechargeM = rechargeM - listM1.get(i).getPrix();
                           c++;
                           i = listM1.size();
                       }
                   }

                   for(int i = 2; i <= listM2.size() -1 ; i++){
                       if (listM2.get(i).getPrix() <=rechargeM){
                        FlistM.add(listM2.get(i));
                        rechargeM = rechargeM - listM2.get(i).getPrix();
                           c++;
                           i = listM2.size();
                       }
                   }

                   for(int i = 3; i <= listM3.size() -1 ; i++){
                       if (listM3.get(i).getPrix() <=rechargeM){
                        FlistM.add(listM3.get(i));
                        rechargeM = rechargeM - listM3.get(i).getPrix();
                           c++;
                           i = listM3.size();
                       }
                   }
                   
               }while(rechargeM!=0 && c!=0);
               FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
               
            }
            if(tabPriorite[2] > tabPriorite[0] && tabPriorite[0] > tabPriorite[1]){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triData(minListHemle));
                listM1.addAll(Tri.triData(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triSms(minListHemle));
                listM3.addAll(Tri.triSms(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                         }
                     }
 
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             
                             i = listH2.size();
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             
                             i = listH3.size();
                         }
                     }
                 }while(rechargeH!=0 && c!=0);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        }
        

         if (prioriteAppel == prioriteData && prioriteAppel == prioriteSms && prioriteData == prioriteSms){
            if(prioriteAppel == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 2){
            List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triData(minListHemle));
                listM1.addAll(Tri.triData(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triSms(minListHemle));
                listM3.addAll(Tri.triSms(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                         }
                     }
 
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             
                             i = listH2.size();
                         }
                     }
 
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             
                             i = listH3.size();
                         }
                     }
                 }while(rechargeH!=0 && c!=0);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
         }

         if(prioriteAppel == 1){
            List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triData(minListHemle));
                listM1.addAll(Tri.triData(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triSms(minListHemle));
                listM3.addAll(Tri.triSms(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                         }
                     }
 
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             
                             i = listH2.size();
                         }
                     }
 
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             
                             i = listH3.size();
                         }
                     }
                 }while(rechargeH!=0 && c!=0);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
         }

         if(prioriteAppel == 0){
            List <Forfait> FlistH = new ArrayList<Forfait>();
            reponse.setReturnValue(FlistH);
               return reponse;
         }

        }

        if (prioriteAppel == prioriteData && prioriteSms != prioriteAppel){
            if(prioriteAppel == 3 && prioriteSms == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 3 && prioriteSms == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 3 && prioriteSms == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 2 && prioriteSms == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 2 && prioriteSms == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 2 && prioriteSms == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 1 && prioriteSms == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 1 && prioriteSms == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 1 && prioriteSms == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
 
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
 
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
 
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 0 && prioriteSms == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 0 && prioriteSms == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

            if(prioriteAppel == 0 && prioriteSms == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }            
            
        }

        if (prioriteSms == prioriteData && prioriteAppel != prioriteSms){
            if(prioriteSms == 3 && prioriteAppel == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 3 && prioriteAppel == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 3 && prioriteAppel == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 2 && prioriteAppel == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 2 && prioriteAppel == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 2 && prioriteAppel == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 1 && prioriteAppel == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 1 && prioriteAppel == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 1 && prioriteAppel == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 0 && prioriteAppel == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triAppel(minListHemle));
                listM1.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 0 && prioriteAppel == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triAppel(minListHemle));
                listM1.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 0 && prioriteAppel == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triAppel(minListHemle));
                listM1.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }

        }


        if (prioriteSms == prioriteAppel && prioriteData != prioriteSms){
            if(prioriteSms == 3 && prioriteData == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triAppel(minListHemle));
                listM3.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 3 && prioriteData == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 3 && prioriteData == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 0; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 2 && prioriteData == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triAppel(minListHemle));
                listM3.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 2 && prioriteData == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 2 && prioriteData == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 2; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 1 && prioriteData == 0){
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH2.addAll(Tri.triSms(minListHemle));
                listM2.addAll(Tri.triSms(minListMango));
                listH3.addAll(Tri.triAppel(minListHemle));
                listM3.addAll(Tri.triAppel(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 3; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 1 && prioriteData == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 2; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 2; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 1 && prioriteData == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                List <Forfait> listH2 = new ArrayList<Forfait>();
                List <Forfait> listM2 = new ArrayList<Forfait>();
                List <Forfait> listH3 = new ArrayList<Forfait>();
                List <Forfait> listM3 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triSms(minListHemle));
                listM1.addAll(Tri.triSms(minListMango));
                listH2.addAll(Tri.triAppel(minListHemle));
                listM2.addAll(Tri.triAppel(minListMango));
                listH3.addAll(Tri.triData(minListHemle));
                listM3.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 3; i <= listH2.size() -1 ; i++){
                         if (listH2.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH2.get(i));
                             rechargeH = rechargeH - listH2.get(i).getPrix();
                             c++;
                             i = listH2.size();
                             System.out.println(rechargeH);
                         }
                     }
        
                     for(int i = 0; i <= listH3.size() -1 ; i++){
                         if (listH3.get(i).getPrix() <=rechargeH){
                             FlistH.add(listH3.get(i));
                             rechargeH = rechargeH - listH3.get(i).getPrix();
                             c++;
                             i = listH3.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
        
                    for(int i = 3; i <= listM2.size() -1 ; i++){
                        if (listM2.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM2.get(i));
                         rechargeM = rechargeM - listM2.get(i).getPrix();
                            c++;
                            i = listM2.size();
                        }
                    }
        
                    for(int i = 0; i <= listM3.size() -1 ; i++){
                        if (listM3.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM3.get(i));
                         rechargeM = rechargeM - listM3.get(i).getPrix();
                            c++;
                            i = listM3.size();
                        }
                    }
                    
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 0 && prioriteData == 1){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triData(minListHemle));
                listM1.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 3; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 3; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 0 && prioriteData == 2){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triData(minListHemle));
                listM1.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 2; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 2; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
            if(prioriteSms == 0 && prioriteData == 3){
                List <Forfait> listH1 = new ArrayList<Forfait>();
                List <Forfait> listM1 = new ArrayList<Forfait>();
                listH1.addAll(Tri.triData(minListHemle));
                listM1.addAll(Tri.triData(minListMango));
                List <Forfait> FlistH = new ArrayList<Forfait>();
                List <Forfait> FlistM = new ArrayList<Forfait>();
                double rechargeH = montant;
                double rechargeM = montant;
                 int c;
                 do {
                      c = 0;
                     for(int i = 0; i <= listH1.size() -1 ; i++){
                         if (listH1.get(i).getPrix() <= rechargeH){
                            FlistH.add(listH1.get(i));
                            rechargeH = rechargeH - listH1.get(i).getPrix();
                             c++;
                             i = listH1.size();
                             System.out.println(rechargeH);
                         }
                     }
                 }while(rechargeH > 0 && c!=0);
                 System.out.println(FlistH);
                 
                 do {
                     c = 0;
                    for(int i = 0; i <= listM1.size() -1 ; i++){
                        if (listM1.get(i).getPrix() <=rechargeM){
                         FlistM.add(listM1.get(i));
                         rechargeM = rechargeM - listM1.get(i).getPrix();
                            c++;
                            i = listM1.size();
                        }
                    }
                }while(rechargeM!=0 && c!=0);
                FlistH.addAll(FlistM);
               reponse.setReturnValue(FlistH);
               return reponse;
            }
        
        }

        

        return reponse;

        }


    @CrossOrigin
    @PostMapping("/test/{montant}/{dure}/{prioriteData}/{prioriteSms}/{prioriteAppel}")
    public Response test(@PathVariable("montant") double montant, @PathVariable("dure")double dure,
    @PathVariable("prioriteData")double prioriteData, @PathVariable("prioriteSms")double prioriteSms,
    @PathVariable("prioriteAppel")double prioriteAppel){
        Response reponse = new Response();
        double nbreRepJuste;
        double nbreRepAtt;
        double nbreRepObtenu;



        return reponse;

    }



    }

