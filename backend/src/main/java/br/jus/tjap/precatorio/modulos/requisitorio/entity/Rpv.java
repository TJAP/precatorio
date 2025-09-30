package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("RPV")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rpv extends Requisitorio {
    
}
