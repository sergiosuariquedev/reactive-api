package com.dev.nequi.domain.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "franquicias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Franquicia {
    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String nombre;
}
