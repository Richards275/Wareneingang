package com.richards275.wareneingang.repositories;

import com.richards275.wareneingang.domain.CSVFehler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CSVFehlerRepository extends JpaRepository<CSVFehler, Long> {
  List<CSVFehler> findByLieferung_Id(Long id);
}


