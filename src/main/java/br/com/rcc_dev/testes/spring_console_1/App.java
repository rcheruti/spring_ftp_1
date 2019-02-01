package br.com.rcc_dev.testes.spring_console_1;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import br.com.rcc_dev.testes.spring_console_1.entities.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Referência de estudo: https://www.baeldung.com/java-ftp-client
 * 
 */
@SpringBootApplication
@EnableConfigurationProperties
@Slf4j
public class App implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	// ----------------------------------------

	@Autowired
	private Config config;

	@Override
	public void run(String... args) throws Exception {
		log.info("Iniciando App. Config: {}", config);

		// ----
		Options options = new Options();
		options.addOption("h", "help", false, "Apresenta esta mensagem de ajuda.");
		options.addOption("u", "user", true, "Usuário para conectar no servidor FTP.");
		options.addOption("p", "pass", true, "Senha para conectar no servidor FTP.");
		options.addOption("h", "host", true, "Ip/Endereço do servidor FTP.");
		options.addOption("i", "port", true, "Porta do servidor FTP.");
		CommandLine commands = new DefaultParser().parse(options, args);
		// ----

		if (commands.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			final String lf = formatter.getNewLine();
			log.info("HELP:" + lf);
			formatter.printHelp(120, "this.jar", lf + "Podem ser usados os seguintes comandos:" + lf, options,
					lf + "Para mais informações consulte a documentação." + lf, true);
			return;
		}

		if( !commands.hasOption('u') ){
			log.error("Informe o usuário através do parâmetro '-u'!");
			return;
		}
		if( !commands.hasOption('p') ){
			log.error("Informe a senha através do parâmetro '-p'!");
			return;
		}

		String user = commands.getOptionValue('u');
		String pass = commands.getOptionValue('p');
		String host = commands.getOptionValue('h');
		String portStr = commands.getOptionValue('i');
		int port = config.getFtpPort();

		if( host == null || host.isEmpty() ) host = config.getFtpServer();
		if( portStr != null || !portStr.isEmpty() ) port = Integer.parseInt(portStr);

		// ----

		listFtp(host, port, user, pass, commands.getArgs()); 

		log.info("Terminando App");
	}

	public void listFtp(
			String host, int port, 
			String user, String pass, 
			String... arquivos) throws SocketException, IOException {

		FTPClient ftp = new FTPClient(); // client FTP
		ftp.addProtocolCommandListener(new PrintCommandListener(System.out)); // logs no console

		ftp.connect(host, port); //iniciar conexão
		
		// verificar se conseguimos uma conexão com sucesso
		int replyCode = ftp.getReplyCode();
		if( !FTPReply.isPositiveCompletion( replyCode ) ){
			ftp.disconnect();
			throw new IOException("Erro ao conectar no servidor FTP! Reply Code: " + replyCode);
		}

		ftp.login(user, pass);

		FTPFile[] files = ftp.listFiles(arquivos[0]);
		log.info("Arquivos encontrados: {}", files.length);
		for( FTPFile f : files ){
			log.info("- {}", f.getName());
		}

		ftp.disconnect(); // fechar
	}

}

