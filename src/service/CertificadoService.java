/*
 * Serviço responsável pela geração e envio de certificados em formato PDF.
 * 
 * Implementa o padrão Singleton para garantir uma única instância no sistema.
 * 
 * Principais responsabilidades:
 * - Gerar certificados PDF personalizados com dados do usuário e evento
 * - Criar certificados visualmente formatados com título, texto, rodapé e borda decorativa
 * - Adicionar número de série único para garantir autenticidade e controle
 * - Enviar certificados por e-mail com anexo PDF utilizando serviço de envio SMTP (EmailSender)
 * 
 * Tecnologias utilizadas:
 * - iText (com.itextpdf.text) para criação dinâmica de PDFs
 * - Jakarta Mail (via EmailSender) para envio de e-mails
 * 
 * Fluxo principal:
 * - gerarCertificadoPDF(Certificado cert): Gera o PDF do certificado em memória e retorna como array de bytes
 * - enviarCertificadoPorEmail(Certificado cert): Gera o PDF e envia por email para o usuário associado
 * 
 * Customizações visuais:
 * - Fonte Times New Roman para título e texto do certificado
 * - Fonte Helvetica para rodapé
 * - Layout A4 com margens ajustadas
 * - Texto justificado e centralizado para melhor leitura
 * - Borda cinza decorativa ao redor da página
 * - Número de série gerado aleatoriamente para identificação única do certificado
 */

package service;

import model.Certificado;
import model.Evento;
import model.Usuario;
import model.Badge;
import otp.EmailSender;
import java.io.ByteArrayOutputStream;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class CertificadoService {

    private static CertificadoService instancia;

    public static CertificadoService getInstance() {
        if (instancia == null) {
            instancia = new CertificadoService();
        }
        return instancia;
    }

    public byte[] gerarCertificadoPDF(Certificado cert) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 50, 50, 70, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, out);
    doc.open();

    Font fonteTitulo = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD);
    Font fonteTexto = new Font(Font.FontFamily.TIMES_ROMAN, 14);
    Font fonteRodape = new Font(Font.FontFamily.HELVETICA, 10);

    Usuario usuario = cert.getUsuario();
    Evento evento = cert.getEvento();

    String dataEvento = evento.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String dataHoje = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String numeroSerie = java.util.UUID.randomUUID().toString().substring(0, 8);

    // Título centralizado
    Paragraph titulo = new Paragraph("CERTIFICADO DE PARTICIPAÇÃO", fonteTitulo);
    titulo.setAlignment(Element.ALIGN_CENTER);
    doc.add(titulo);

    doc.add(new Paragraph(" ")); // Espaço

    // Texto principal
    String texto = String.format(
        "Certificamos que %s, CPF: %s, participou do evento \"%s\", realizado em %s. " +
        "Este certificado é emitido em %s para fins de comprovação.",
        usuario.getNome(),
        usuario.getCpf() != null ? usuario.getCpf() : "não informado",
        evento.getTitulo(),
        dataEvento,
        dataHoje
    );

    Paragraph corpo = new Paragraph(texto, fonteTexto);
    corpo.setAlignment(Element.ALIGN_JUSTIFIED);
    corpo.setSpacingBefore(20);
    corpo.setSpacingAfter(30);
    doc.add(corpo);

    // Rodapé com identificador único
    Paragraph rodape = new Paragraph("Número de série: " + numeroSerie, fonteRodape);
    rodape.setAlignment(Element.ALIGN_RIGHT);
    doc.add(rodape);

    // Moldura opcional (decorativa)
    PdfContentByte canvas = writer.getDirectContent();
    Rectangle border = new Rectangle(50, 50, 545, 792); // margem interna
    border.setBorder(Rectangle.BOX);
    border.setBorderWidth(2);
    border.setBorderColor(BaseColor.GRAY);
    canvas.rectangle(border);

    doc.close();
    return out.toByteArray();
}


    public void enviarCertificadoPorEmail(Certificado cert) {
        try {
            byte[] pdfBytes = gerarCertificadoPDF(cert);
            String assunto = "Certificado de Participação - " + cert.getEvento().getTitulo();
            String corpo = "Olá " + cert.getUsuario().getNome() + ",\n\nSegue em anexo seu certificado de participação no evento.";
            String nomeArquivo = "certificado_" + cert.getUsuario().getNome().replaceAll(" ", "_") + ".pdf";

            EmailSender.sendEmailWithAttachment(cert.getUsuario().getEmail(), assunto, corpo, pdfBytes, nomeArquivo);
            
            /* Após envio, adiciona badge ao usuário
            cert.getUsuario().adicionarBadge(
                new Badge(
                    "Participou de: " + cert.getEvento().getTitulo(),
                    "/resources/icons/badge_evento.png", // Atualize com o caminho real do ícone
                    "Certificado recebido por participação no evento \"" + cert.getEvento().getTitulo() + "\" em " + 
                        cert.getEvento().getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
            );
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
