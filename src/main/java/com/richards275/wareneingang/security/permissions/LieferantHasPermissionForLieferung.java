package com.richards275.wareneingang.security.permissions;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@authoriseService.userHasAccessToLieferung(principal,#lieferungDto)")
public @interface LieferantHasPermissionForLieferung {
}