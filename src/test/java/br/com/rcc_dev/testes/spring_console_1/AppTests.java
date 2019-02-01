package br.com.rcc_dev.testes.spring_console_1;

import java.io.IOException;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AppTests {

	private FakeFtpServer fakeFtpServer;
	private String user = "user1";
	private String pass = "pass1";

	@Before
	public void setup() {
		log.info("Iniciando FakeFtpServer");
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.addUserAccount(new UserAccount(user, pass, "/data"));

		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry("/data"));
		fileSystem.add(new FileEntry("/data/foobar.txt", "abcdef 1234567890"));
		fakeFtpServer.setFileSystem(fileSystem);
		fakeFtpServer.setServerControlPort(0);

		fakeFtpServer.start();
	}

	@Test
	public void ftpTest() throws SocketException, IOException {
		log.info("Fazendo teste de leitura");
		App app = new App();
		app.listFtp("localhost", fakeFtpServer.getServerControlPort(), user, pass, "/data");
	}


	@After
	public void teardown() {
		fakeFtpServer.stop();
	}

}

