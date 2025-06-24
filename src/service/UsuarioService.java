/*
 * Serviço responsável pelo gerenciamento completo de usuários.
 * 
 * Implementa o padrão Singleton para garantir uma única instância.
 * 
 * Principais responsabilidades:
 * - Cadastro e autenticação de usuários
 * - Validação de dados sensíveis (CPF, email, senha)
 * - Gerenciamento de participação em eventos
 * - Integração com serviços de email e eventos
 * - Controle de fluxos temporários (OTP, recuperação de senha)
 * 
 * Estruturas de dados principais:
 * - ArrayList<Usuario>: Armazenamento dos usuários
 * - BasicPasswordEncryptor: Criptografia de senhas
 * 
 * Métodos principais:
 * - iniciarCadastro(), completarCadastro(): Fluxo de cadastro em etapas
 * - fazerLogin(): Autenticação com email e senha
 * - validar*(): Métodos de validação de CPF, email, senha, etc.
 * - registrarParticipacaoUsuario(): Vinculação usuário-eventos
 * - getUsuarioPorEmail(): Busca por email
 * - atualizarSenha(): Redefinição segura de senha
 * 
 * Validações implementadas:
 * - Força da senha (complexidade)
 * - Formato de email válido
 * - CPF válido (incluindo dígitos verificadores)
 * - Idade mínima (14 anos)
 * - Telefone válido
 * 
 * Padrões utilizados:
 * - Singleton: Controle de instância única
 * - Factory: Para criação de usuários em etapas
 * - Strategy: Diferentes algoritmos de validação
 */

package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jasypt.util.password.BasicPasswordEncryptor;

import model.Evento;
import model.Usuario;
import otp.EmailConfirmationService;

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

	public Usuario iniciarCadastro(String nome, String sobrenome, String username, String email, String senha) {
		// Validações
		// Verifica email existente
		if (!validarEmail(email) || !validarSenha(senha)
				|| listaUsuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
			return null;
		}

		// Criptografa a senha
		String senhaHash = passwordEncryptor.encryptPassword(senha);

		// Cria usuário, mas NÃO adiciona à lista ainda
		Usuario novo = new Usuario(nome, sobrenome, username, email, senhaHash);

		// Garante caminho da foto padrão caso não tenha sido setado
		if (novo.getCaminhoFotoPerfil() == null || novo.getCaminhoFotoPerfil().isEmpty()) {
			novo.setCaminhoFotoPerfil("/resources/profile/iconFotoPerfilDefault.png");
		}

		return novo;
	}

	public boolean completarCadastro(Usuario usuario) {
		if (usuario.getId() == 0) {
			usuario.setId(proximoId++);
			listaUsuarios.add(usuario);
		} else {
			// Atualiza usuário existente na lista
			int idx = listaUsuarios.indexOf(usuario);
			if (idx >= 0) {
				listaUsuarios.set(idx, usuario);
			} else {
				listaUsuarios.add(usuario);
			}
		}
		// outras lógicas de validação, criptografia, etc, se necessário

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
		if (usuario.getId() == 0) {
			usuario.setId(proximoId++);
		}
		return true;
	}

	public void cadastrarUsuario(String nome, String email, String senha) {
		// ... cadastro no sistema
		EmailConfirmationService.iniciarConfirmacaoEmail(email, nome);
	}

	/**
	 * Verifica se a data de nascimento corresponde a uma pessoa com 14 anos ou
	 * mais.
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
		if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
			return false;
		}
		try {
			int soma = 0, peso = 10;
			for (int i = 0; i < 9; i++) {
				soma += (cpf.charAt(i) - '0') * peso--;
			}
			int dig1 = 11 - (soma % 11);
			dig1 = (dig1 >= 10) ? 0 : dig1;

			soma = 0;
			peso = 11;
			for (int i = 0; i < 10; i++) {
				soma += (cpf.charAt(i) - '0') * peso--;
			}
			int dig2 = 11 - (soma % 11);
			dig2 = (dig2 >= 10) ? 0 : dig2;

			return dig1 == (cpf.charAt(9) - '0') && dig2 == (cpf.charAt(10) - '0');
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validarSenha(String senha) {
		if (senha == null || senha.length() < 8) {
			return false;
		}

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

		Usuario Tester1 = new Usuario();
		Tester1.setId(1);
		Tester1.setNome("Eduardo Enari");
		Tester1.setUsername("ProfEduEnari");
		Tester1.setSenha("Teste@123");
		Tester1.setEmail("eduenari@gmail.com");
		Tester1.setTelefone("11982566881");
		Tester1.setCpf("311.456.777-00");
		Tester1.setDataNascimento(LocalDate.of(2002, 5, 15));
		Tester1.setGenero("Masculino");
		this.cadastrar(Tester1);

		Usuario Tester2 = new Usuario();
		Tester2.setId(2);
		Tester2.setNome("Carlos Feichas");
		Tester2.setUsername("ProfFeichas");
		Tester2.setSenha("Teste@123");
		Tester2.setEmail("feichas@gmail.com");
		Tester2.setTelefone("11922933818");
		Tester2.setCpf("121.436.777-00");
		Tester2.setDataNascimento(LocalDate.of(1999, 2, 15));
		Tester2.setGenero("Masculino");
		this.cadastrar(Tester2);

		Usuario Tester3 = new Usuario();
		Tester3.setId(3);
		Tester3.setNome("Alefe");
		Tester3.setUsername("Tester");
		Tester3.setSenha("Teste@123");
		Tester3.setEmail("alefe@gmail.com");
		Tester3.setTelefone("11982938888");
		Tester3.setCpf("321.456.777-00");
		Tester3.setDataNascimento(LocalDate.of(2002, 1, 15));
		Tester3.setGenero("Masculino");
		this.cadastrar(Tester3);

		Usuario Tester4 = new Usuario();
		Tester4.setId(4);
		Tester4.setNome("Thiago Motta");
		Tester4.setUsername("Thiago_Motta");
		Tester4.setSenha("Teste@123");
		Tester4.setEmail("thiago.oliveiramotta@gmail.com");
		Tester4.setTelefone("11999998888");
		Tester4.setCpf("123.456.777-00");
		Tester4.setDataNascimento(LocalDate.of(1965, 1, 15));
		Tester4.setGenero("Feminino");
		this.cadastrar(Tester4);

		Usuario Tester5 = new Usuario();
		Tester5.setId(5);
		Tester5.setNome("Caetano");
		Tester5.setUsername("Caetano");
		Tester5.setSenha("Teste@123");
		Tester5.setEmail("joazin1012123987@gmail.com");
		Tester5.setTelefone("12996311271");
		Tester5.setCpf("123.456.789-00");
		Tester5.setDataNascimento(LocalDate.of(2002, 12, 17));
		Tester5.setGenero("Masculino");
		this.cadastrar(Tester5);

		Usuario Tester6 = new Usuario();
		Tester6.setId(6);
		Tester6.setNome("Felipe");
		Tester6.setUsername("FeMoura");
		Tester6.setSenha("Teste@123");
		Tester6.setEmail("fmoura.dev@gmail.com");
		this.cadastrar(Tester6);

		int maiorId = listaUsuarios.stream().mapToInt(Usuario::getId).max().orElse(0);
		proximoId = maiorId + 1;

	}

	public boolean dadosCompletosCadastrados(Usuario usuario) {

		if (usuario.getCpf() == null || usuario.getGenero() == null || usuario.getTelefone() == null
				|| usuario.getDataNascimento() == null) {
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
		return listaUsuarios.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
	}

	// MÉTODOS DE INTEGRAÇÃO COM EVENTOS

	/**
	 * Permite que um usuário participe de um evento. Adiciona o evento à lista de
	 * eventos do usuário e o usuário à lista de participantes do evento.
	 *
	 * @param usuarioId ID do usuário que deseja participar
	 * @param evento    evento ao qual o usuário deseja se juntar
	 * @return true se a participação foi bem-sucedida, false caso contrário
	 */
	public boolean registrarParticipacaoUsuario(int usuarioId, Evento evento) {
		Usuario usuario = buscarPorId(usuarioId);
		if (usuario != null && evento != null) {
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
	 * @param evento    Evento do qual o usuário deseja se remover
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
	 * @return Lista de eventos que o usuário está participando ou uma lista vazia
	 *         se o usuário não for encontrado
	 */

	public List<Evento> getEventosParticipandoUsuario(int usuarioId) {
		System.out.println("[DEBUG] Buscando eventos para o usuário ID: " + usuarioId);

		Usuario usuario = buscarPorId(usuarioId);
		if (usuario == null) {
			System.out.println("[DEBUG] Usuário não encontrado");
			return Collections.emptyList();
		}

		List<Evento> eventos = usuario.getEventosParticipando();
		System.out.println("[DEBUG] Eventos encontrados: " + eventos.size());
		eventos.forEach(e -> System.out.println(" - " + e.getTitulo()));

		return new ArrayList<>(eventos);
	}

	/**
	 * Retorna uma lista de eventos organizados pelo usuário.
	 *
	 * @param usuarioId ID do usuário
	 * @return Lista de eventos organizados pelo usuário ou uma lista vazia se o
	 *         usuário não for encontrado
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
		if (usuario == null) {
			return false;
		}

		String hash = passwordEncryptor.encryptPassword(novaSenha);
		usuario.setSenha(hash);
		return true;
	}

	public boolean isCadastroCompleto(Usuario usuario) {
		return usuario.getNome() != null && !usuario.getNome().isEmpty() && usuario.getEmail() != null
				&& !usuario.getEmail().isEmpty() && usuario.getSenha() != null && !usuario.getSenha().isEmpty()
				&& usuario.getCpf() != null && !usuario.getCpf().isEmpty() && usuario.getTelefone() != null
				&& !usuario.getTelefone().isEmpty();
	}
}
