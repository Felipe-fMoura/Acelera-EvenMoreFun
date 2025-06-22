/*
 * - Implementa a interface `DataSource` para fornecer dados baseados em um array de bytes.
 * - Utilizada para enviar anexos por e-mail, especialmente em contextos que requerem DataSource para conteúdo binário.
 *
 * Principais métodos:
 * - getInputStream()
 *   - Retorna um `InputStream` a partir do array de bytes.
 *   - Lança IOException caso os dados sejam nulos.
 *
 * - getOutputStream()
 *   - Não suportado; lança IOException ao ser chamado.
 *
 * - getContentType()
 *   - Retorna o tipo MIME da carga útil (ex: "image/png", "application/pdf").
 *
 * - getName()
 *   - Retorna o nome da fonte de dados, fixo como "ByteArrayDataSourceCustom".
 *
 */

package otp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.DataSource;

public class ByteArrayDataSourceCustom implements DataSource {

	private byte[] data;
	private String type;
	private String name = "ByteArrayDataSourceCustom";

	public ByteArrayDataSourceCustom(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}

	// Retorna os dados como InputStream (usado para envio de anexos em e-mails)
	@Override
	public InputStream getInputStream() throws IOException {
		if (data == null) {
			throw new IOException("No data");
		}
		return new ByteArrayInputStream(data);
	}

	// Retorna o tipo MIME do conteúdo
	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Not Supported");
	}

	@Override
	public String getContentType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}
}
