/*
 * Modelo que representa um Certificado de participação.
 * 
 * Responsável por armazenar as informações necessárias para gerar o certificado,
 * como o usuário beneficiado, o evento relacionado e a data de emissão do certificado.
 * 
 * Atributos principais:
 * - Usuario usuario: Referência ao usuário que recebeu o certificado.
 * - Evento evento: Referência ao evento ao qual o certificado se refere.
 * - LocalDate dataEmissao: Data em que o certificado foi emitido.
 * 
 * Métodos principais:
 * - Getters e setters para todos os atributos, permitindo encapsulamento e manipulação dos dados.
 * 
 * Uso típico:
 * - Instanciado e preenchido durante o processo de geração e envio de certificados.
 * - Utilizado pelo serviço CertificadoService para criar documentos PDF e enviar e-mails.
 */

package model;

import java.time.LocalDate;

public class Certificado {

    private Usuario usuario;
    private Evento evento;
    private LocalDate dataEmissao;
    
    public Certificado() {
    }

    public Certificado(Usuario usuario, Evento evento, LocalDate dataEmissao) {
        this.usuario = usuario;
        this.evento = evento;
        this.dataEmissao = dataEmissao;
    }

    // Getter e Setter para usuario
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getter e Setter para evento
    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    // Getter e Setter para dataEmissao
    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
}
