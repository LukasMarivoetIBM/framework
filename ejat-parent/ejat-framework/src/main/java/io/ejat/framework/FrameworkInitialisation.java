package io.ejat.framework;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import io.ejat.framework.spi.ConfigurationPropertyStoreException;
import io.ejat.framework.spi.DynamicStatusStoreException;
import io.ejat.framework.spi.FrameworkException;
import io.ejat.framework.spi.IConfigurationPropertyStoreService;
import io.ejat.framework.spi.IDynamicStatusStoreService;
import io.ejat.framework.spi.IFramework;
import io.ejat.framework.spi.IFrameworkInitialisation;

public class FrameworkInitialisation implements IFrameworkInitialisation {

	private final Framework framework;
	private final Properties bootstrapProperties;
	private final Properties overrideProperties;
	private final Properties recordProperties = new Properties();

	private final URI        uriConfigurationPropertyStore;

	private Log              logger = LogFactory.getLog(this.getClass());

	public FrameworkInitialisation(Properties bootstrapProperties,
			Properties overrideProperties) throws URISyntaxException, InvalidSyntaxException, FrameworkException {
		this.bootstrapProperties = bootstrapProperties;
		this.overrideProperties  = overrideProperties;

		logger.info("Initialising the eJAT Framework");

		this.framework = new Framework(this.overrideProperties, this.recordProperties);

		String propUri = this.bootstrapProperties.getProperty("framework.config.store");
		if (propUri == null || propUri.isEmpty()) {
			this.uriConfigurationPropertyStore = new URI("file://" + System.getProperty("user.home") + "/.ejat/cps.properties");
		} else {
			this.uriConfigurationPropertyStore = new URI(propUri);
		}
		logger.debug("Configuration Property Store is " + propUri);


		//*** Initialise the Configuration Property Store
		logger.trace("Searching for CPS providers");
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<?>[] cpsServiceReference = bundleContext.getAllServiceReferences(IConfigurationPropertyStoreService.class.getName(), null);
		if (cpsServiceReference == null || cpsServiceReference.length == 0) {
			throw new FrameworkException("No Configuration Property Store Services have been found");
		}
		for(ServiceReference<?> cpsReference : cpsServiceReference) {
			IConfigurationPropertyStoreService cpsService = (IConfigurationPropertyStoreService) bundleContext.getService(cpsReference);
			logger.trace("Found CPS Provider " + cpsService.getClass().getName());
			cpsService.initialise(this);
		}
		if (this.framework.getConfigurationPropertyStoreService() == null) {
			throw new FrameworkException("Failed to initialise a Configuration Property Store, unable to continue");
		}
		logger.debug("Selected CPS Service is " + this.framework.getConfigurationPropertyStoreService().getClass().getName());


		//*** Initialise the Dynamic Status Store
		logger.trace("Searching for DSS providers");
		ServiceReference<?>[] dssServiceReference = bundleContext.getAllServiceReferences(IDynamicStatusStoreService.class.getName(), null);
		if (dssServiceReference == null || dssServiceReference.length == 0) {
			throw new FrameworkException("No Dynamic Status Store Services have been found");
		}
		for(ServiceReference<?> dssReference : dssServiceReference) {
			IDynamicStatusStoreService dssService = (IDynamicStatusStoreService) bundleContext.getService(dssReference);
			logger.trace("Found DSS Provider " + dssService.getClass().getName());
			dssService.initialise(this);
		}
//		if (this.framework.getDynamicStatusStoreService() == null) {
//			throw new FrameworkException("Failed to initialise a Dynamic Status Store, unable to continue");
//		}
//		logger.debug("Selected DSS Service is " + this.framework.getDynamicStatusStoreService().getClass().getName());


		logger.error("Framework implementation is incomplete");
	}

	@Override
	public @NotNull URI getBootstrapConfigurationPropertyStore() {
		return this.uriConfigurationPropertyStore;
	}

	@Override
	public URI getDynamicStatusStoreUri() {
		throw new UnsupportedOperationException("No implemented yet");
	}

	@Override
	public List<URI> getResultArchiveStoreUris() {
		throw new UnsupportedOperationException("No implemented yet");
	}

	@Override
	public void registerConfigurationPropertyStoreService(
			@NotNull IConfigurationPropertyStoreService configurationPropertyStoreService)
					throws ConfigurationPropertyStoreException {
		this.framework.setConfigurationPropertyStoreService(configurationPropertyStoreService);
	}

	@Override
	public void registerDynamicStatusStoreService(@NotNull IDynamicStatusStoreService dynamicStatusStoreService)
			throws DynamicStatusStoreException {
		this.framework.setDynamicStatusStoreService(dynamicStatusStoreService);
	}

	/* (non-Javadoc)
	 * @see io.ejat.framework.spi.IFrameworkInitialisation#getFramework()
	 */
	@Override
	public @NotNull IFramework getFramework() {
		return this.framework;
	}

}