package com.junha.securemarshmallow;
import com.junha.securemarshmallow.domain.UserAccount;
import com.junha.securemarshmallow.repository.UserAccountRepository;
import com.junha.securemarshmallow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class SecureMarshmallowApplication {
    private UserAccountRepository userAccountRepository;
    @Autowired
    public SecureMarshmallowApplication(UserAccountRepository userAccountRepository)
    {
        this.userAccountRepository = userAccountRepository;
    }
    @Transactional
    public void deleteAllRefreshTokens() {
        userAccountRepository.deleteAllRefreshTokens();
    }
    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(SecureMarshmallowApplication.class, args);
        SecureMarshmallowApplication application = context.getBean(SecureMarshmallowApplication.class);
        application.deleteAllRefreshTokens();
    }
}
