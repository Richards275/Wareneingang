package com.richards275.wareneingang.service;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.repositories.LieferantRepository;
import com.richards275.wareneingang.security.payload.request.LieferantRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LieferantService {

  private final LieferantRepository lieferantRepository;

  public LieferantService(LieferantRepository lieferantRepository) {
    this.lieferantRepository = lieferantRepository;
  }

  public List<Lieferant> findAll() {
    return lieferantRepository.findAll();
  }

  public Lieferant registerLieferant(LieferantRequest lieferantRequest) {
    return lieferantRepository.save(
        new Lieferant(lieferantRequest.getName(), true)
    );
  }

  public Lieferant wechsleAktivInaktiv(Long id) {
    return lieferantRepository
        .findById(id)
        .map(lieferant -> {
          lieferant.setIstAktiv(!lieferant.getIstAktiv());
          return lieferantRepository.save(lieferant);
        })
        .orElse(null);
  }

}
