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

    @Override
    public InputStream getInputStream() throws IOException {
        if (data == null) throw new IOException("No data");
        return new ByteArrayInputStream(data);
    }

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
