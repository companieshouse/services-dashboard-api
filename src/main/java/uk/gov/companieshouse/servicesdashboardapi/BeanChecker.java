package uk.gov.companieshouse.servicesdashboardapi;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;


@Component
public class BeanChecker implements CommandLineRunner {

    private final ApplicationContext applicationContext;
    @Value("${loglevel}")
    String logLevel;

    public BeanChecker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String @NonNull ... args) {
        Objects.requireNonNull(logLevel, "logLevel must not be null");
        if ("debug".equalsIgnoreCase(logLevel)) {
            Arrays.stream(applicationContext.getBeanDefinitionNames())
                    .sorted()
                    .forEach(System.out::println);
        }
    }
}
