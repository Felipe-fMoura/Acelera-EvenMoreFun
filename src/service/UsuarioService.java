package service;

import java.util.ArrayList;
import java.util.List;

import org.jasypt.util.password.BasicPasswordEncryptor;

import model.Usuario;

public class UsuarioService {

    private static UsuarioService instancia;
    private static final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
    private List<Usuario> listaUsuarios = new ArrayList<>();

    private UsuarioService() { }

    public static UsuarioService getInstance() {
        if (instancia == null) {
            instancia = new UsuarioService();
        }
        return instancia;
    }

    public boolean cadastrar(Usuario usuario) {
        // Verifica duplicidade de email
        for (Usuario u : listaUsuarios) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                return false;
            }
        }
        // Criptografa a senha antes de armazenar
        String hash = passwordEncryptor.encryptPassword(usuario.getSenha());
        usuario.setSenha(hash);
        listaUsuarios.add(usuario);
        return true;
    }

    public boolean validarEmail(String email) {
        return email.contains("@") && email.endsWith(".com");
    }

    public boolean fazerLogin(String email, String senhaDigitada) {
    for (Usuario u : listaUsuarios) {
        if (u.getEmail().equalsIgnoreCase(email)
            && passwordEncryptor.checkPassword(senhaDigitada, u.getSenha())) {
            return true;
        }
    }
    return false;
}


    public List<Usuario> getUsuarios() {
        return listaUsuarios;
    }

    public void listarUsuarios() {
        if (listaUsuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        System.out.println("Lista de usuários cadastrados:");
        for (Usuario u : listaUsuarios) {
            System.out.println("Nome: " + u.getNome() +
                               " | Email: " + u.getEmail() +
                               " | Hash: " + u.getSenha());
        }
    }
    
    
    
    
}
