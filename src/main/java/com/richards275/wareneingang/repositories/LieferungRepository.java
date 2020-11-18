package com.richards275.wareneingang.repositories;

import com.richards275.wareneingang.domain.Lieferung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LieferungRepository extends JpaRepository<Lieferung, Long> {
  List<Lieferung> findByLieferantId(Long id);
}

