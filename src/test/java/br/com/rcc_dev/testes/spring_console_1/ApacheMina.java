package br.com.rcc_dev.testes.spring_console_1;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.UserAuthNoneFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.rcc_dev.testes.spring_console_1.entities.Config;
import lombok.extern.slf4j.Slf4j;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApacheMina {

  SshServer sshd;

  @Autowired Config config;

  @Before
  public void setup() throws IOException {
    log.info("Iniciando Apache MINA SFTP");
    sshd = SshServer.setUpDefaultServer();
    sshd.setPort( config.getFtpPort() );
    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));

    List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
    userAuthFactories.add(new UserAuthNoneFactory());
    sshd.setUserAuthFactories(userAuthFactories);

    sshd.setCommandFactory( command -> null );

    List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
    namedFactoryList.add(new SftpSubsystemFactory());
    sshd.setSubsystemFactories(namedFactoryList);

    sshd.start();
  }

  @After
  public void teardown() throws IOException {
    log.info("Fechando Apache MINA SFTP");
		sshd.close();
  }
  
}
