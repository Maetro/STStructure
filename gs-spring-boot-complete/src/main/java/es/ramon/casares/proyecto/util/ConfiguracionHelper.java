package es.ramon.casares.proyecto.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ConfiguracionHelper {
	private String name;
    private String env;
    private List<String> servers = new ArrayList<String>();
    private List<Integer> numbers = new ArrayList<Integer>();

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public List<String> getServers() {
	return servers;
    }

    public void setServers(List<String> servers) {
	this.servers = servers;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getEnv() {
	return env;
    }

    public void setEnv(String env) {
	this.env = env;
    }

}
