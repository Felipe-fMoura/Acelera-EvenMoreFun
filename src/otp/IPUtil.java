/*
getLocalIPv4()
Obtém o endereço IPv4 local ativo da máquina:
Itera sobre todas as interfaces de rede disponíveis.
Ignora interfaces que são loopback ou inativas.
Busca o primeiro endereço IPv4 que não seja localhost (127.x.x.x) nem APIPA (169.x.x.x).
Retorna o IP encontrado como String.
Em caso de erro ou falha, retorna "localhost" como fallback.
Estruturas e técnicas utilizadas:
Uso de NetworkInterface para listar interfaces de rede.
Uso de InetAddress para obter endereços IP.
Filtragem de endereços IPv4 válidos excluindo endereços especiais.
Tratamento de exceções para garantir robustez.
*/

package otp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPUtil {

	public static String getLocalIPv4() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();

				// Ignora interfaces que não estão ativas ou são loopback
				if (iface.isLoopback() || !iface.isUp()) {
					continue;
				}

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					if (addr instanceof java.net.Inet4Address) {
						String ip = addr.getHostAddress();
						// Evita IPs internos como 127.0.0.1 ou 169.x.x.x
						if (!ip.startsWith("127.") && !ip.startsWith("169.")) {
							return ip;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "localhost"; // fallback
	}
}
