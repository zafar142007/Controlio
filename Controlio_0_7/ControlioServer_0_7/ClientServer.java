package maaartin.so;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class RebindDemo {
	static class Server implements Runnable {
		@Override public void run() {
			while (true) {
				try {
					step();
				} catch (final Exception e) {
					print(e);
				}
			}
		}

		private void step() throws Exception {
			Thread.sleep(90);
			print();
			final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress("localhost", 4567));
			final SocketChannel socketChannel = serverSocketChannel.accept();
			final byte[] bytes = new byte[1024];
			final ByteBuffer bb = ByteBuffer.wrap(bytes);
			socketChannel.read(bb);
			print(new String(bb.array(), "utf-8"));
			socketChannel.close();

			serverSocketChannel.close();
		}
	}

	static class Client implements Runnable {
		@Override public void run() {
			while (true) {
				try {
					step();
				} catch (final Exception e) {
					print(e);
				}
			}
		}

		private void step() throws UnknownHostException, Exception {
			Thread.sleep(100);
			print();
			final Socket socket = new Socket((String) null, 4567);
			final OutputStream out = socket.getOutputStream();
			out.write("bye\n".getBytes("utf-8"));
			out.close();
		}
	}

	public static void main(String[] args) throws Exception {
		new Thread(new Server(), "Server").start();
		Thread.sleep(50);
		new Thread(new Client(), "Client").start();
		Thread.sleep(2000);
		print("EXITING");
		System.exit(0);
	}

	private static void print() {
		System.out.println(Thread.currentThread().getName());
	}

	private static void print(String s) {
		System.out.println(Thread.currentThread().getName() + ": " + s);
	}

	private static void print(Exception e) {
		System.out.println(Thread.currentThread().getName() + ": " + e);
	}
}