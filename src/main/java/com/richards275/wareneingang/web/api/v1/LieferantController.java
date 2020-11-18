package com.richards275.wareneingang.web.api.v1;

import com.richards275.wareneingang.domain.Lieferant;
import com.richards275.wareneingang.security.payload.request.LieferantRequest;
import com.richards275.wareneingang.service.LieferantService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class LieferantController {

  private final LieferantService lieferantService;

  public LieferantController(LieferantService lieferantService) {
    this.lieferantService = lieferantService;
  }

  @GetMapping("/lieferant")
  @ResponseStatus(HttpStatus.OK)
  public List<Lieferant> findAll() {
    return lieferantService.findAll();
  }

  @PostMapping("/lieferant/register")
  @ResponseStatus(HttpStatus.OK)
  public Lieferant registerLieferant(@Valid @RequestBody LieferantRequest lieferantRequest) {
    return lieferantService.registerLieferant(lieferantRequest);
  }

  @GetMapping("/lieferant/wechsle/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Lieferant wechsleAktivInaktiv(@PathVariable Long id) {

    return lieferantService.wechsleAktivInaktiv(id);
  }

}