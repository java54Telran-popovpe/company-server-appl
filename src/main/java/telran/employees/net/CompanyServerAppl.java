package telran.employees.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import telran.employees.*;
import telran.io.Persistable;
import telran.net.Protocol;
import telran.net.TcpServer;

public class CompanyServerAppl {

	private static final String FILE_NAME = "employeesTest.data";
	private static final int PORT = 5000;

	public static void main(String[] args) {

		Company company = new CompanyMapsImpl();
		try {
			((Persistable) company).restore(FILE_NAME);
		} catch (Exception e) {

		}
		Protocol protocol = new CompanyProtocol(company);
		TcpServer tcpServer = new TcpServer(protocol, PORT);
		Thread tcpServerThread = new Thread(tcpServer);
		tcpServerThread.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String userInput = null;
		while (true) {
			System.out.println("Enter \"shutdown\" for graceful server shutdown");
			try {
				userInput = reader.readLine();
				if (userInput.equals("shutdown")) {
					tcpServer.shutdown();
					waitForServerToTerminate(tcpServerThread);
					saveCompanyData(company, FILE_NAME);
					return;

				}
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

	}

	private static void waitForServerToTerminate(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException e) {
			
		}
	}

	private static void saveCompanyData(Company company, String fileName) {
		try {
			((Persistable) company).save(FILE_NAME);
		} catch (Exception e) {

		}

	}

}