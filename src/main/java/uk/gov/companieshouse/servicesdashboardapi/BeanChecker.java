package uk.gov.companieshouse.servicesdashboardapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public class BeanChecker implements CommandLineRunner {

   @Value("${loglevel}")
   String logLevel;

    private final ApplicationContext applicationContext;

    public BeanChecker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
      if (logLevel.equals("debug")) {
         String[] beanNames = applicationContext.getBeanDefinitionNames();
         Arrays.sort(beanNames);
         for (String beanName : beanNames) {
               System.out.println(beanName);
         }
      }
   }
}
