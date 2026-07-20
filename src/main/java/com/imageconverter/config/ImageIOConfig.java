package com.imageconverter.config;

import jakarta.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.springframework.context.annotation.Configuration;

/**
 * Ensures all ImageIO SPI plugins (including TwelveMonkeys extensions
 * loaded from nested JARs) are registered with the IIORegistry.
 */
@Configuration
public class ImageIOConfig {

    @PostConstruct
    public void init() {
        // Force scan for any plugins on the classpath. Spring Boot's nested-jar classloader
        // normally handles ServiceLoader, but explicitly scanning is a safe belt-and-braces
        // approach for image plugins.
        ImageIO.scanForPlugins();
        System.out.println("[ImageConverter] Registered ImageIO readers: "
                + java.util.Arrays.toString(ImageIO.getReaderFormatNames()));
        System.out.println("[ImageConverter] Registered ImageIO writers: "
                + java.util.Arrays.toString(ImageIO.getWriterFormatNames()));
    }
}
