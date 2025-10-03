package com.univesp.vestIA.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Questao {

    @NotBlank(message = "O enunciado não pode estar vazio")
    @Size(min = 5, message = "O enunciado deve ter pelo menos 5 caracteres")
    private String enunciado;

    @NotBlank(message = "Alternativa A não pode estar vazia")
    @Size(min = 5, message = "Alternativa A deve ter pelo menos 5 caracteres")
    private String alternativaA;

    @NotBlank(message = "Alternativa B não pode estar vazia")
    @Size(min = 5, message = "Alternativa B deve ter pelo menos 5 caracteres")
    private String alternativaB;

    @NotBlank(message = "Alternativa C não pode estar vazia")
    @Size(min = 5, message = "Alternativa C deve ter pelo menos 5 caracteres")
    private String alternativaC;

    @NotBlank(message = "Alternativa D não pode estar vazia")
    @Size(min = 5, message = "Alternativa D deve ter pelo menos 5 caracteres")
    private String alternativaD;

    @NotBlank(message = "Alternativa E não pode estar vazia")
    @Size(min = 5, message = "Alternativa E deve ter pelo menos 5 caracteres")
    private String alternativaE;

    @NotNull(message = "A resposta correta deve ser informada")
    private Resposta resposta;

    public enum Resposta {
        A, B, C, D, E
    }

    private String assunto;

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getAlternativaA() {
        return alternativaA;
    }

    public void setAlternativaA(String alternativaA) {
        this.alternativaA = alternativaA;
    }

    public String getAlternativaB() {
        return alternativaB;
    }

    public void setAlternativaB(String alternativaB) {
        this.alternativaB = alternativaB;
    }

    public String getAlternativaC() {
        return alternativaC;
    }

    public void setAlternativaC(String alternativaC) {
        this.alternativaC = alternativaC;
    }

    public String getAlternativaD() {
        return alternativaD;
    }

    public void setAlternativaD(String alternativaD) {
        this.alternativaD = alternativaD;
    }

    public String getAlternativaE() {
        return alternativaE;
    }

    public void setAlternativaE(String alternativaE) {
        this.alternativaE = alternativaE;
    }

    public Resposta getResposta() {
        return resposta;
    }

    public void setResposta(Resposta resposta) {
        this.resposta = resposta;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }
}
