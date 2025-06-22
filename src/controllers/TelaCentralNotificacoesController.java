/*
 * Controller responsável pela tela de gerenciamento de notificações do usuário.
 * 
 * Principais funcionalidades:
 * - Exibição de notificações organizadas por tipo (histórico e alertas)
 * - Integração com o serviço de notificações
 * - Navegação básica de retorno
 * 
 * Componentes principais:
 * - TabPane: Organização por abas (histórico/alertas)
 * - ListViews: Exibição das notificações
 * 
 * Serviços utilizados:
 * - NotificacaoService: Obtenção das notificações do usuário
 * - SessaoUsuario: Acesso ao usuário logado
 * 
 * Métodos principais:
 * - initialize(): Carrega as notificações ao iniciar
 * - handleVoltar(): Fecha a janela atual
 * 
 * Estruturas de dados:
 * - ListView<String>: Para exibição das notificações
 * - List<Notificacao>: Armazenamento temporário dos dados
 */

package controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import model.Notificacao;
import model.Usuario;
import service.NotificacaoService;
import session.SessaoUsuario;

public class TelaCentralNotificacoesController {

	@FXML private TabPane tabPane;
	@FXML private ListView<String> listHistorico;
	@FXML private ListView<String> listAlertas;
	@FXML private Button btnVoltar;

	private Usuario usuarioLogado;

	@FXML
	public void initialize() {
		usuarioLogado = SessaoUsuario.getUsuarioLogado();

		List<Notificacao> historicos = NotificacaoService.getInstance().getNotificacoes(usuarioLogado.getId(),
				Notificacao.Tipo.HISTORICO);
		listHistorico.getItems().setAll(historicos.stream().map(Notificacao::toString).toList());

		List<Notificacao> alertas = NotificacaoService.getInstance().getNotificacoes(usuarioLogado.getId(),
				Notificacao.Tipo.ALERTA);
		listAlertas.getItems().setAll(alertas.stream().map(Notificacao::toString).toList());
	}

	@FXML
	private void handleVoltar() {
		Stage stage = (Stage) btnVoltar.getScene().getWindow();
		// Fecha a janela
		stage.close();
	}
}
