package com.univesp.vestIA.repository;

import com.univesp.vestIA.model.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {

    public List<Questao> findAllByAssuntoContainingIgnoreCase(@Param("assunto") String assunto);

}
