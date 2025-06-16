package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jasypt.util.password.BasicPasswordEncryptor;
import service.EmailConfirmationService;

import model.Evento;
import model.Usuario;

public class UsuarioService {

	private int proximoId = 1;
	private static UsuarioService instancia;
	private static final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
	private List<Usuario> listaUsuarios = new ArrayList<>();

	private Usuario usuarioTemporario;
	private String otpTemporario;

	private UsuarioService() {
	}

	public static UsuarioService getInstance() {
		if (instancia == null) {
			instancia = new UsuarioService();
		}
		return instancia;
	}

	public Usuario iniciarCadastro(String nome, String sobrenome,String username, String email, String senha) {
		// Validações
		if (!validarEmail(email) || !validarSenha(senha)) {
			return null;
		}

		// Verifica email existente
		if (listaUsuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
			return null;
		}
		
		String senhaHash = passwordEncryptor.encryptPassword(senha);

		// Cria usuário com ID provisional
		Usuario novo = new Usuario(nome,sobrenome,username, email, senhaHash);
		listaUsuarios.add(novo);
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
	
	public void cadastrarUsuario(String nome, String email, String senha) {
	    // ... cadastro no sistema
	    EmailConfirmationService.iniciarConfirmacaoEmail(email, nome);
	}

	/**
	 * Verifica se a data de nascimento corresponde a uma pessoa com 14 anos ou mais.
	 *
	 * @param dataNascimento a data de nascimento a ser validada
	 * @return true se a pessoa tiver 14 anos ou mais, false caso contrário
	 */
	
	public boolean validarDataNascimento(LocalDate dataNascimento) {
	    if (dataNascimento == null) {
	        return false;
	    }

	    LocalDate hoje = LocalDate.now();
	    LocalDate limite = hoje.minusYears(14);

	    return dataNascimento.isBefore(limite) || dataNascimento.isEqual(limite);
	}
	
	public boolean validarEmail(String email) {
		String regexRFC5322 = "^(?:[a-zA-Z0-9_'^&amp;/+-])+(?:\\." + "[a-zA-Z0-9_'^&amp;/+-]+)*@"
				+ "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
		return email != null && email.matches(regexRFC5322);
	}

	public boolean validarTelefone(String telefone) {
		return telefone.matches("\\d{10,11}");
	}

	public boolean validarCPF(String cpf) {
		cpf = cpf.replaceAll("[^\\d]", "");
		if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}"))
			return false;
		try {
			int soma = 0, peso = 10;
			for (int i = 0; i < 9; i++)
				soma += (cpf.charAt(i) - '0') * peso--;
			int dig1 = 11 - (soma % 11);
			dig1 = (dig1 >= 10) ? 0 : dig1;

			soma = 0;
			peso = 11;
			for (int i = 0; i < 10; i++)
				soma += (cpf.charAt(i) - '0') * peso--;
			int dig2 = 11 - (soma % 11);
			dig2 = (dig2 >= 10) ? 0 : dig2;

			return dig1 == (cpf.charAt(9) - '0') && dig2 == (cpf.charAt(10) - '0');
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validarSenha(String senha) {
		if (senha == null || senha.length() < 8)
			return false;

		boolean temMaiuscula = senha.matches(".*[A-Z].*");
		boolean temMinuscula = senha.matches(".*[a-z].*");
		boolean temNumero = senha.matches(".*[0-9].*");
		boolean temEspecial = senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

		return temMaiuscula && temMinuscula && temNumero && temEspecial;
	}

	public boolean fazerLogin(String email, String senhaDigitada) {
		for (Usuario u : listaUsuarios) {
			if (u.getEmail().equalsIgnoreCase(email) && passwordEncryptor.checkPassword(senhaDigitada, u.getSenha())) {
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
			System.out.printf("│ %-20s: %-15s │\n", "CPF", u.getClass());
			System.out.printf("│ %-20s: %-15s │\n", "Nascimento", u.getDataNascimento());
			System.out.printf("│ %-20s: %-15s │\n", "Gênero", u.getGenero());
			System.out.println("└──────────────────────────────────────┘");
			System.out.println();
		}

		System.out.println("============ FIM DA LISTA ============\n");
	}

	public void carregarUsuariosDeTeste() {
		Usuario Tester = new Usuario();
		Tester.setId(1);
		Tester.setNome("Usuario Teste");
		Tester.setUsername("Tester");
		Tester.setSenha("Teste@123");
		Tester.setEmail("fmouraschool@gmail.com");
		this.cadastrar(Tester);
		
		Usuario Tester2 = new Usuario();
		Tester2.setId(2);
		Tester2.setNome("Usuario Teste2");
		Tester2.setUsername("Brazino");
		Tester2.setSenha("Teste@123");
		Tester2.setEmail("brazino@gmail.com");
		this.cadastrar(Tester2);
		
		Usuario Tester3 = new Usuario();
		Tester3.setId(3);
		Tester3.setNome("Usuario Teste3");
		Tester3.setUsername("Caetano");
		Tester3.setSenha("Teste@123");
		Tester3.setEmail("joazin1012123987@gmail.com");
		this.cadastrar(Tester3);
	}

	
	public boolean dadosCompletosCadastrados(Usuario usuario) {
		
		if (usuario.getCpf() == null || usuario.getGenero() == null || 
				usuario.getTelefone() == null || usuario.getDataNascimento() == null) {
		    // Algum dos campos está nulo
			System.out.println("[DEBUG] Os Dados não estão completamente cadastrados");
			return false;
		}
		
		return true;
	}
	
	
	// MÉTODOS AUXILIARES ADICIONADOS
	
	/**
	 * Busca e retorna um usuário com base no seu ID.
	 *
	 * @param id o ID do usuário a ser buscado
	 * @return o usuário correspondente, ou null se não for encontrado
	 */
    public Usuario buscarPorId(int id) {
        return listaUsuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }
	
    
    // MÉTODOS DE INTEGRAÇÃO COM EVENTOS
    
    
    /**
     * Permite que um usuário participe de um evento.
     * Adiciona o evento à lista de eventos do usuário e o usuário à lista de participantes do evento.
     *
     * @param usuarioId ID do usuário que deseja participar
     * @param evento evento ao qual o usuário deseja se juntar
     * @return true se a participação foi bem-sucedida, false caso contrário
     */
    public boolean registrarParticipacaoUsuario(int usuarioId, Evento evento) {
    	Usuario usuario = buscarPorId(usuarioId);
    	if(usuario != null && evento != null) {
    		boolean participou = usuario.participarEvento(evento);
    		if (participou) {
    			EventoService.getInstance().adicionarParticipante(evento.getId(), usuarioId);
    		}
    		return participou;
    	}
    	return false;
    }
    
	
    /**
     * Cancela a participação de um usuário em um evento.
     *
     * @param usuarioId ID do usuário que deseja cancelar a participação
     * @param evento Evento do qual o usuário deseja se remover
     * @return true se a operação foi realizada com sucesso, false caso contrário
     */
    public boolean removerParticipacaoUsuario(int usuarioId, Evento evento) {
        Usuario usuario = buscarPorId(usuarioId);
        if (usuario != null && evento != null) {
            usuario.cancelarParticipacao(evento);
            return true;
        }
        return false;
    }
    
    
    /**
     * Retorna uma lista dos eventos em que o usuário está participando.
     *
     * @param usuarioId ID do usuário
     * @return Lista de eventos que o usuário está participando ou uma lista vazia se o usuário não for encontrado
     */

    public List<Evento> getEventosParticipandoUsuario(int usuarioId){
    	System.out.println("[DEBUG] Buscando eventos para o usuário ID: "+ usuarioId);
    	
    	Usuario usuario = buscarPorId(usuarioId);
    	if(usuario==null) {
    		System.out.println("[DEBUG] Usuário não encontrado");
    		return Collections.emptyList();
    	}
    	
    	List<Evento> eventos = usuario.getEventosParticipando();
    	System.out.println("[DEBUG] Eventos encontrados: "+ eventos.size());
    	eventos.forEach(e -> System.out.println(" - " + e.getTitulo()));
    	
    	return new ArrayList<>(eventos);
    }
      
    
    /**
     * Retorna uma lista de eventos organizados pelo usuário.
     *
     * @param usuarioId ID do usuário
     * @return Lista de eventos organizados pelo usuário ou uma lista vazia se o usuário não for encontrado
     */

    public List<Evento> getEventosOrganizandoUsuario(int usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        return usuario != null ? usuario.getEventosOrganizados() : new ArrayList<>();
        // operador ternário >> condição ? valor_se_verdadeiro : valor_se_falso;
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
		if (usuario == null)
			return false;

		String hash = passwordEncryptor.encryptPassword(novaSenha);
		usuario.setSenha(hash);
		return true;
	}
}
