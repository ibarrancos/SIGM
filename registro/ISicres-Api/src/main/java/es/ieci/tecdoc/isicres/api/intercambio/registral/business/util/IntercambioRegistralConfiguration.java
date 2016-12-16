package es.ieci.tecdoc.isicres.api.intercambio.registral.business.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ieci.tecdoc.common.isicres.ConfigFilePathResolverIsicres;

/**
 * Clase para consultar la configuraci�n referente a Intercambio Registral
 *
 */
public class IntercambioRegistralConfiguration {


	private static final Logger log = Logger.getLogger(IntercambioRegistralConfiguration.class);

	private static IntercambioRegistralConfiguration _instance = null;

	private Properties configurationProperties = new Properties();

	private String isicresIntercambioRegistralConfigurationPath;


	/**
	 * Obtiene la instancia �nica de configuraci�n
	 */
	public synchronized static IntercambioRegistralConfiguration getInstance() {
		if (_instance == null) {
			_instance = new IntercambioRegistralConfiguration();
		}

		return _instance;
	}



	private IntercambioRegistralConfiguration() {
		initPath();
		initConfigurator();
	}

	/**
	 * Inicializa la configuraci�n buscando el fichero seg�n la pol�tica de carga del framework
	 */
	private void initConfigurator() {
		try {
			InputStream stream = getClass().getResourceAsStream(
					isicresIntercambioRegistralConfigurationPath);
			if (stream == null) {
				stream = getClass().getClassLoader().getResourceAsStream(
						isicresIntercambioRegistralConfigurationPath);
			}
			if (stream == null) {
				stream = ClassLoader
						.getSystemResourceAsStream(isicresIntercambioRegistralConfigurationPath);
			}
			if(stream!=null)
			{
				configurationProperties.load(stream);
			}
			else{
				configurationProperties.load(new FileInputStream(isicresIntercambioRegistralConfigurationPath));
			}

		} catch (IOException t) {
			log.fatal("Imposible cargar el fichero de configuracion ["
					+ isicresIntercambioRegistralConfigurationPath + "]", t);
		}
	}

	private void initPath(){
		ConfigFilePathResolverIsicres filePathResolver = ConfigFilePathResolverIsicres.getInstance();
		isicresIntercambioRegistralConfigurationPath = filePathResolver.getIntercambioRegistralConfigPath();
	}

	public String getProperty(String key)
	{
		return configurationProperties.getProperty(key);
	}

	public String getCountryCode(){
		return configurationProperties.getProperty(IntercambioRegistralConfigurationKeys.COUNTRY_CODE_KEY);
	}

	public Long getFileMaxSize(){
		return Long.valueOf(configurationProperties.getProperty(IntercambioRegistralConfigurationKeys.FILE_MAX_SIZE_KEY));
	}

	public Integer getFileMaxNum(){
		return Integer.valueOf(configurationProperties.getProperty(IntercambioRegistralConfigurationKeys.FILE_MAX_NUM_KEY));
	}
	public List<String> getExtensiones(){
		String extensions = configurationProperties.getProperty(IntercambioRegistralConfigurationKeys.FILE_EXTENSIONS_KEY);
		List<String> extensionsList = Arrays.asList(extensions.split(","));
		return extensionsList;
	}
	public Long getFilesSetMaxSize() {
		return Long.valueOf(this.configurationProperties.getProperty("ficheros.tamano.maximo.total"));
	}

	public boolean getActiveValidationRelationEntidadUnidad() {
		return Boolean.parseBoolean(this.configurationProperties.getProperty("activa.validacion.relacion.entidad.unidad", "false"));
	}

}
