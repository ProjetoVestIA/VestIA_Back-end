package com.univesp.vestIA.controller;

import com.univesp.vestIA.model.Questao;
import com.univesp.vestIA.repository.QuestaoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questao")
@CrossOrigin(origins = "*", allowedHeaders = "*") // TODO: Colocar a origem definitiva

public class QuestaoController {

    @Autowired
    private QuestaoRepository questaoRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Questao>> getAll(){
        return ResponseEntity.ok(questaoRepository.findAll()); // Pega todas as questões, usado apenas para fins de teste
    }

    @GetMapping("/{id}")
    public ResponseEntity<Questao> getById(@PathVariable Long id){
        return questaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/assunto/{assunto}")
    public ResponseEntity<List<Questao>> getByAssunto(@PathVariable String assunto){
        return ResponseEntity.ok(questaoRepository.findAllByAssuntoContainingIgnoreCase(assunto));
    }

    @PostMapping ("/post")
    public ResponseEntity<Questao> post(@Valid @RequestBody Questao questao) {

        questao.setId(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questaoRepository.save(questao)); // Cria uma questão, usado apenas por administradores

        // TODO: Proteger o endpoint
    }

    @PutMapping("/put")
    public ResponseEntity<Questao> put(@Valid @RequestBody Questao questao) {
        return questaoRepository.findById(questao.getId())
                .map(resposta -> ResponseEntity.status(HttpStatus.OK)
                        .body(questaoRepository.save(questao)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Questao> questao = questaoRepository.findById(id);

        if(questao.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        questaoRepository.deleteById(id);
    }

}
