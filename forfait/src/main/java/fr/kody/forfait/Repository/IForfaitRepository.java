package fr.kody.forfait.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.kody.forfait.Entity.Forfait;

@Repository
public interface IForfaitRepository extends JpaRepository <Forfait, Long>{


    @Transactional
	@Query(value = "select * from Forfait where intitule like 'Mango%'" ,nativeQuery = true)
    List <Forfait> listerForfaitMango();
    
    @Transactional
	@Query(value = "select * from Forfait where intitule like 'Hemle%'",nativeQuery = true)
	List <Forfait> listerForfaitHemle();
	
	@Modifying
	@Transactional
	@Query(value = "delete from Forfait forfait where forfait.intitule=:intitule")
	void supprimerForfait(@Param("intitule")String intitule);
}