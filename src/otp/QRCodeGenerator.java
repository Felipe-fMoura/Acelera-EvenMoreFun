/*
 * Método relevante criado:
 * - generateQRCode(String text, int size): gera um QR Code em formato PNG retornando o array de bytes.
 */

package otp;

import java.io.ByteArrayOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QRCodeGenerator {

	public static byte[] generateQRCode(String text, int size) throws Exception {
		BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
		return baos.toByteArray();
	}
}
