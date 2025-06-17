/*
 * Métodos relevantes criados:
 * - getInputStream(): retorna os dados como InputStream (para anexos em e-mails).
 * - getContentType(): retorna o tipo MIME dos dados.
 */

package otp;

import jakarta.activation.DataSource;
import java.io.*;

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
        if (data == null) throw new IOException("No data");
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
