package com.richards275.wareneingang.repositories;

import com.richards275.wareneingang.domain.Lieferant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LieferantRepository extends JpaRepository<Lieferant, Long> {

}

