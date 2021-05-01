package cn.edu.sustech.cs307.factory;

import java.util.HashMap;
import java.util.Map;

public abstract class ServiceFactory {
    private final Map<Class<?>, Object> services = new HashMap<>();

    /**
     * Create a service instance of the given service class.
     *
     * @param serviceClass the requested service class.
     * @return an instance of the service.
     */
    public <T> T createService(Class<T> serviceClass) {
        try {
            return (T) services.get(serviceClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Register a service implementation class.
     *
     * @param serviceClass the service interface.
     * @param implementationInstance the service implementation instance.
     */
    protected <T> void registerService(Class<T> serviceClass, T implementationInstance) {
        services.put(serviceClass, implementationInstance);
    }
}
