package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PRECATORIO")
public class Precatorio extends Requisitorio {
    
}
