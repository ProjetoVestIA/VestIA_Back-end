package com.univesp.vestIA.controller;

import com.univesp.vestIA.model.Questao;
import com.univesp.vestIA.repository.QuestaoRepository;
import com.univesp.vestIA.repository.UsuarioRepository;
import com.univesp.vestIA.service.UsuarioService;
import com.univesp.vestIA.util.JwtHelper;
import com.univesp.vestIA.util.TestBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Optional;

import static com.univesp.vestIA.model.Questao.Resposta.A;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class QuestaoControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String BASE_URL = "/questao";
    private static final String ADMIN = "root@root.com";
    private static final String SENHA = "rootroot";
    private String token;

    private static final String ALT_A = "Alternativa A correta";
    private static final String ALT_B = "Alternativa B incorreta";
    private static final String ALT_C = "Alternativa C incorreta";
    private static final String ALT_D = "Alternativa D incorreta";
    private static final String ALT_E = "Alternativa E incorreta";


    @BeforeAll
    void start() {

        questaoRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuarioService.cadastrarUsuario(
                TestBuilder.criarUsuario(null, "Root", ADMIN, SENHA, 0)
        );

        token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
    }


    @Test
    @DisplayName("✔ 01 - Deve cadastrar uma questão com sucesso")
    void deveCadastrarQuestao() {

        // Given
        Questao questao = TestBuilder.criarQuestao(null,
                "Qual a capital da França, segundo o vestibular?",
                ALT_A, ALT_B, ALT_C, ALT_D, ALT_E,
                A, "Geografia"
        );

        // When
        HttpEntity<Questao> requisicao = JwtHelper.criarRequisicaoComToken(questao, token);
        ResponseEntity<Questao> resposta = testRestTemplate.exchange(
                BASE_URL + "/post", HttpMethod.POST, requisicao, Questao.class);

        // Then
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
    }


    @Test
    @DisplayName("✔ 02 - Deve listar todas as questões com sucesso")
    void deveListarTodasQuestoes() {

        // Given
        questaoRepository.save(
                TestBuilder.criarQuestao(null, "Questão 1 de História",
                        ALT_A, ALT_B, ALT_C, ALT_D, ALT_E, A,"Historia")
        );

        questaoRepository.save(
                TestBuilder.criarQuestao(null, "Questão 2 de Matemática",
                        ALT_A, ALT_B, ALT_C, ALT_D, ALT_E, A,"Matemática")
        );

        // When
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
        ResponseEntity<Questao[]> resposta = testRestTemplate.exchange(
                BASE_URL + "/all", HttpMethod.GET, requisicao, Questao[].class);

        // Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        // Ajustado para garantir que há pelo menos 2 questões após os saves
        // (considerando que outros testes também podem ter salvo)
        assertTrue(resposta.getBody().length >= 2);
    }


    @Test
    @DisplayName("✔ 03 - Deve buscar uma questão pelo ID com sucesso")
    void deveBuscarPorId() {

        // Given
        Questao salva = questaoRepository.save(
                TestBuilder.criarQuestao(null, "Questão teste ID para Biologia",
                        ALT_A, ALT_B, ALT_C, ALT_D, ALT_E, A,"Biologia")
        );

        // When
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
        ResponseEntity<Questao> resposta = testRestTemplate.exchange(
                BASE_URL + "/" + salva.getId(), HttpMethod.GET, requisicao, Questao.class);

        // Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(salva.getId(), resposta.getBody().getId());
    }


    @Test
    @DisplayName("✔ 04 - Deve atualizar uma questão com sucesso")
    void deveAtualizarQuestao() {

        // Given
        Questao salva = questaoRepository.save(
                TestBuilder.criarQuestao(null, "Questão antiga de Física",
                        ALT_A, ALT_B, ALT_C, ALT_D, ALT_E, A,"Física")
        );

        salva.setEnunciado("Questão de Física atualizada com novos detalhes");

        // When
        HttpEntity<Questao> requisicao = JwtHelper.criarRequisicaoComToken(salva, token);
        ResponseEntity<Questao> resposta = testRestTemplate.exchange(
                BASE_URL + "/put", HttpMethod.PUT, requisicao, Questao.class);

        // Then
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Questão de Física atualizada com novos detalhes", resposta.getBody().getEnunciado());
    }


    @Test
    @DisplayName("✔ 05 - Deve deletar uma questão com sucesso")
    void deveDeletarQuestao() {

        // Given
        Questao salva = questaoRepository.save(
                TestBuilder.criarQuestao(null, "Questão a deletar de Química",
                        ALT_A, ALT_B, ALT_C, ALT_D, ALT_E, A,"Química")
        );

        // When
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
        ResponseEntity<Void> resposta = testRestTemplate.exchange(
                BASE_URL + "/" + salva.getId(), HttpMethod.DELETE, requisicao, Void.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertEquals(Optional.empty(), questaoRepository.findById(salva.getId()));
    }

}