package com.prod.tap.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class TapBeansLoad {
    private static final Logger LOGGER = LoggerFactory.getLogger(TapBeansLoad.class);

    private static ConfigurableApplicationContext context;
    private static boolean initialized = false;
    private static Class configClass;

    private TapBeansLoad() {
    }

    /**
     * <p>This method is required to allow overriding the config class, not using the default <b>cart-core</b>
     * bootstrap. This is for example used by the downstream project <b>cart-bdd</b> in the <b>Main</b> class&aphos;
     * <b>main</b> method to bootstrap using its configuration.</p>
     *
     * @param configClass the config class
     */
    public static void setConfigClass(Class configClass) {
        TapBeansLoad.configClass = configClass;
    }

    /**
     * Initialize the lifecycle.
     */
    public static synchronized void init() {
        if (!initialized) {
            configureContainer();
            initialized = true;
            LOGGER.debug("bootstrap: initialized.");
        }
    }

    /**
     * Configure the Spring application context.
     */
    private static synchronized void configureContainer() {
        if (context == null) {
            context = new AnnotationConfigApplicationContext(configClass);
        }
    }

    public static ApplicationContext getContext() {
        init();
        return context;
    }

    public static Object getBean(String name) {
        init();
        return context.getBean(name);
    }

    public static Object getBean(Class class1) {
        init();
        return context.getBean(class1);
    }

}
