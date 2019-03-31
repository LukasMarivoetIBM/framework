package io.ejat.framework.spi;

import javax.validation.constraints.NotNull;

public interface IDynamicStatusStoreRegistration {
    
    void initialise(@NotNull IFrameworkInitialisation frameworkInitialisation) throws DynamicStatusStoreException;
}