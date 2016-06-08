package es.ramon.casares.proyecto.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfiguracionHelper.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ConfiguracionHelper {
	
	/** The name. */
	private String name;
    
    /** The env. */
    private String env;
    
    /** The servers. */
    private List<String> servers = new ArrayList<String>();
    
    /** The numbers. */
    private List<Integer> numbers = new ArrayList<Integer>();
    
    /** The minimum square. */
    private Integer minimumSquare;

    /**
     * Gets the numbers.
     *
     * @return the numbers
     */
    public List<Integer> getNumbers() {
        return numbers;
    }

    /**
     * Sets the numbers.
     *
     * @param numbers the new numbers
     */
    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    /**
     * Gets the servers.
     *
     * @return the servers
     */
    public List<String> getServers() {
	return servers;
    }

    /**
     * Sets the servers.
     *
     * @param servers the new servers
     */
    public void setServers(List<String> servers) {
	this.servers = servers;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Gets the env.
     *
     * @return the env
     */
    public String getEnv() {
	return env;
    }

    /**
     * Sets the env.
     *
     * @param env the new env
     */
    public void setEnv(String env) {
	this.env = env;
    }

	/**
	 * Gets the minimum square.
	 *
	 * @return the minimum square
	 */
	public Integer getMinimumSquare() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Sets the minimum square.
	 *
	 * @param minimumSquare the new minimum square
	 */
	public void setMinimumSquare(Integer minimumSquare) {
		this.minimumSquare = minimumSquare;
	}

}
