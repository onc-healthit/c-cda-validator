package gov.nist.healthcare.ttt.webapp;

import gov.nist.healthcare.ttt.webapp.common.config.ComponentConfig;
import gov.nist.healthcare.ttt.webapp.common.config.ToolkitClientConfig;
import gov.nist.healthcare.ttt.webapp.common.security.PortFilter;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.DispatcherServlet;

@EnableAutoConfiguration
@Import({ComponentConfig.class,
	ToolkitClientConfig.class
})
@ImportResource("classpath:/spring/resources.xml")
public class Application {
	
	@Value("${server.port}")
	static int serverPort;


	@Bean
	public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
		ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
		registration.addUrlMappings("/");
		registration.setLoadOnStartup(1);
		return registration;
	}

	@Bean
	@Autowired
	public EmbeddedServletContainerFactory servletContainer(@Value("${toolkit.endpoint.port}") int toolkitEndpoint) {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		tomcat.addAdditionalTomcatConnectors(createSslConnector(toolkitEndpoint));
		return tomcat;
	}

	private Connector createSslConnector(int toolkitEndpoint) {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//		try {
//			File keystore = new ClassPathResource("keystore").getFile();
//			File truststore = new ClassPathResource("keystore").getFile();
//			connector.setScheme("https");
//			connector.setSecure(true);
			connector.setPort(toolkitEndpoint);
//			protocol.setSSLEnabled(true);
//			protocol.setKeystoreFile(keystore.getAbsolutePath());
//			protocol.setKeystorePass("changeit");
//			protocol.setTruststoreFile(truststore.getAbsolutePath());
//			protocol.setTruststorePass("changeit");
//			protocol.setKeyAlias("tomcat");
			return connector;
//		}
//		catch (IOException ex) {
//			throw new IllegalStateException("can't access keystore: [" + "keystore"
//					+ "] or truststore: [" + "keystore" + "]", ex);
//		}
	}
    
    @Bean
    @Autowired
    public FilterRegistrationBean portFilter(@Value("${server.port}") int serverPort) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        PortFilter filter = new PortFilter(serverPort);
        filterRegistrationBean.setFilter(filter);
        return filterRegistrationBean;
    }

        
	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		SpringApplication.run(Application.class, args);
	}


}