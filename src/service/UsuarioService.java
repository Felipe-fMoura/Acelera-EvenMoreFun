package service;

import java.util.ArrayList;
import java.util.List;
import org.jasypt.util.password.BasicPasswordEncryptor;
import model.Usuario;

public class UsuarioService {
	

    private int proximoId = 1;
    private static UsuarioService instancia;
    private static final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
    private List<Usuario> listaUsuarios = new ArrayList<>();

    private Usuario usuarioTemporario;
    private String otpTemporario;

    private UsuarioService() { }

    public static UsuarioService getInstance() {
        if (instancia == null) {
            instancia = new UsuarioService();
        }
        return instancia;
    }

    public Usuario iniciarCadastro(String username, String email, String senha) {
        // Validações
        if (!validarEmail(email) || !validarSenha(senha)) {
            return null;
        }
        
        // Verifica email existente
        if (listaUsuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            return null;
        }
        
        // Cria usuário com ID provisional
        Usuario novo = new Usuario(username, email, senha);
        novo.setId(proximoId++); // Reserva o ID
        return novo;
    }
    
    public boolean completarCadastro(Usuario usuario) {
        // Criptografa senha
        String hash = passwordEncryptor.encryptPassword(usuario.getSenha());
        usuario.setSenha(hash);
        
        // Adiciona à lista
        listaUsuarios.add(usuario);
        return true;
    }
    
    public boolean cadastrar(Usuario usuario) {

    	
        for (Usuario u : listaUsuarios) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                return false;
            }
        }
        
        String hash = passwordEncryptor.encryptPassword(usuario.getSenha());
        usuario.setSenha(hash);
        listaUsuarios.add(usuario);
   	 usuario.setId(proximoId++);
        return true;
    }

    public boolean validarEmail(String email) {
        String regexRFC5322 = "^(?:[a-zA-Z0-9_'^&amp;/+-])+(?:\\." +
                              "[a-zA-Z0-9_'^&amp;/+-]+)*@" +
                              "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        return email != null && email.matches(regexRFC5322);
    }
    
    public boolean validarTelefone(String telefone) {
        return telefone.matches("\\d{10,11}");
    }

    public boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
        try {
            int soma = 0, peso = 10;
            for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * peso--;
            int dig1 = 11 - (soma % 11);
            dig1 = (dig1 >= 10) ? 0 : dig1;

            soma = 0; peso = 11;
            for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * peso--;
            int dig2 = 11 - (soma % 11);
            dig2 = (dig2 >= 10) ? 0 : dig2;

            return dig1 == (cpf.charAt(9) - '0') && dig2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validarSenha(String senha) {
        if (senha == null || senha.length() < 8) return false;
        
        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temNumero = senha.matches(".*[0-9].*");
        boolean temEspecial = senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return temMaiuscula && temMinuscula && temNumero && temEspecial;
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
        System.out.println("\n============ USUÁRIOS CADASTRADOS ============");
        System.out.println("Total de usuários: " + listaUsuarios.size());
        System.out.println("--------------------------------------------");

        for (Usuario u : listaUsuarios) {
            System.out.println("┌──────────────────────────────────────┐");
            System.out.printf("│ ID: %-34d │\n", u.getId());
            System.out.println("├──────────────────────────────────────┤");
            System.out.printf("│ %-20s: %-15s │\n", "Username", u.getUsername());
            System.out.printf("│ %-20s: %-15s │\n", "Email", u.getEmail());
            System.out.printf("│ %-20s: %-15s │\n", "Nome", u.getNome());
            System.out.printf("│ %-20s: %-15s │\n", "Sobrenome", u.getSobrenome());
            System.out.printf("│ %-20s: %-15s │\n", "Telefone", u.getTelefone());
            System.out.printf("│ %-20s: %-15s │\n", "CPF", u.getCPF());
            System.out.printf("│ %-20s: %-15s │\n", "Nascimento", u.getDataNascimento());
            System.out.printf("│ %-20s: %-15s │\n", "Gênero", u.getGenero());
            System.out.println("└──────────────────────────────────────┘");
            System.out.println();
        }

        System.out.println("============ FIM DA LISTA ============\n");
    }

    public void carregarUsuariosDeTeste() {
        Usuario joazin = new Usuario();
        joazin.setNome("Joazin Teste");
        joazin.setEmail("joazin1012123987@gmail.com");
        joazin.setSenha("Broxa@123");
        this.cadastrar(joazin);
    }

    // OTP - fluxo de redefinição de senha

    public Usuario getUsuarioPorEmail(String email) {
        for (Usuario u : listaUsuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public void setUsuarioTemporario(Usuario usuario) {
        this.usuarioTemporario = usuario;
    }

    public Usuario getUsuarioTemporario() {
        return this.usuarioTemporario;
    }

    public void setOtpTemporario(String otp) {
        this.otpTemporario = otp;
    }

    public String getOtpTemporario() {
        return this.otpTemporario;
    }

    public boolean atualizarSenha(String email, String novaSenha) {
        Usuario usuario = getUsuarioPorEmail(email);
        if (usuario == null) return false;

        String hash = passwordEncryptor.encryptPassword(novaSenha);
        usuario.setSenha(hash);
        return true;
    }
}
