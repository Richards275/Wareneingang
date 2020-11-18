package com.richards275.wareneingang.repositories;

import com.richards275.wareneingang.domain.Ware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WareRepository extends JpaRepository<Ware, Long> {

  Optional<Ware> findByNameAndNummerAndLieferung_Id(String name, String nummer, long lieferungId);

  List<Ware> findByLieferung_Id(Long id);

  Page<Ware> findByNameContaining(String title, Pageable pageable);

}


