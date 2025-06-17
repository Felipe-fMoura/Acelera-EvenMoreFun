/*
 * Métodos relevantes criados:
 * - getInstance() : SessaoUsuario (Singleton para obter instância)
 * - setUsuario(Usuario usuario) : void (define usuário logado)
 * - getUsuario() : Usuario (retorna usuário logado)
 * - limparSessao() : void (limpa sessão do usuário)
 * - getUsuarioLogado() : static Usuario (facilita acesso ao usuário logado)
 */

package session;

import model.Usuario;

public class SessaoUsuario {
    private static SessaoUsuario instancia;
    private Usuario usuarioLogado;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstance() {
        if (instancia == null) {
            instancia = new SessaoUsuario();
        }
        return instancia;
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuario() {
        return usuarioLogado;
    }

    public void limparSessao() {
        usuarioLogado = null;
    }
 // facilitar chamadas diretas
    public static Usuario getUsuarioLogado() {
        return getInstance().getUsuario();
    }
}
