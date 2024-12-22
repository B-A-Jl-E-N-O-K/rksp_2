package ru.zverev.rksp4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.zverev.rksp4.model.Salad;
@Repository
@Component
public interface SaladRepository extends JpaRepository<Salad, Integer>{
    //Zverev
    Salad findSaladById(Long id);
}
