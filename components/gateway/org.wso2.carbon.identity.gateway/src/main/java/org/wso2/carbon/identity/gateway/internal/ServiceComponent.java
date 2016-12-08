package org.wso2.carbon.identity.gateway.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.framework.IdentityProcessor;
import org.wso2.carbon.identity.gateway.handler.callback.GatewayCallbackHandler;
import org.wso2.carbon.identity.gateway.processor.InitRequestProcessor;
import org.wso2.carbon.kernel.CarbonRuntime;

import java.util.logging.Logger;

/**
 * Service component to consume CarbonRuntime instance which has been registered as an OSGi service
 * by Carbon Kernel.
 *
 * @since 1.0.0-SNAPSHOT
 */
@Component(
        name = "org.wso2.carbon.identity.gateway.internal.ServiceComponent",
        immediate = true
)
public class ServiceComponent {

    private Logger logger = Logger.getLogger(ServiceComponent.class.getName());

    /**
     * This is the activation method of ServiceComponent. This will be called when its references are
     * satisfied.
     *
     * @param bundleContext the bundle context instance of this bundle.
     * @throws Exception this will be thrown if an issue occurs while executing the activate method
     */
    @Activate
    protected void start(BundleContext bundleContext) throws Exception {

        bundleContext.registerService(IdentityProcessor.class, new InitRequestProcessor(), null);
        logger.info("Service Component is activated");


    }

    /**
     * This is the deactivation method of ServiceComponent. This will be called when this component
     * is being stopped or references are satisfied during runtime.
     *
     * @throws Exception this will be thrown if an issue occurs while executing the de-activate method
     */
    @Deactivate
    protected void stop() throws Exception {
        logger.info("Service Component is deactivated");


    }

    /**
     * This bind method will be called when CarbonRuntime OSGi service is registered.
     *
     * @param carbonRuntime The CarbonRuntime instance registered by Carbon Kernel as an OSGi service
     */
    @Reference(
            name = "carbon.runtime.service",
            service = CarbonRuntime.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetCarbonRuntime"
    )
    protected void setCarbonRuntime(CarbonRuntime carbonRuntime) {
        DataHolder.getInstance().setCarbonRuntime(carbonRuntime);
    }

    /**
     * This is the unbind method which gets called at the un-registration of CarbonRuntime OSGi service.
     *
     * @param carbonRuntime The CarbonRuntime instance registered by Carbon Kernel as an OSGi service
     */
    protected void unsetCarbonRuntime(CarbonRuntime carbonRuntime) {
        DataHolder.getInstance().setCarbonRuntime(null);
    }


    /**
     * This bind method will be called when {@link GatewayCallbackHandler} OSGi services are registered
     *
     * @param callbackHandler {@link GatewayCallbackHandler} OSGi service instance.
     */
    @Reference(
            name = "gateway.callback.handler",
            service = GatewayCallbackHandler.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetCallbackHandler"
    )
    protected void setCallbackHandler(GatewayCallbackHandler callbackHandler) {
        DataHolder.getInstance().addGatewayCallbackHandler(callbackHandler);
    }


    protected void unsetCallbackHandler(GatewayCallbackHandler callbackHandler) {
        DataHolder.getInstance().addGatewayCallbackHandler(null);
    }
}