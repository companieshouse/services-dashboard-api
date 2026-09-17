package uk.gov.companieshouse.servicesdashboardapi;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BeanCheckerTest {

    @Test
    void shouldPrintSortedBeanNamesWhenLogLevelIsDebug() throws Exception {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeanDefinitionNames()).thenReturn(new String[]{"zetaBean", "alphaBean", "middleBean"});

        BeanChecker beanChecker = new BeanChecker(applicationContext);
        ReflectionTestUtils.setField(beanChecker, "logLevel", "debug");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            beanChecker.run();
        } finally {
            System.setOut(originalOut);
        }

        String expected = String.join(System.lineSeparator(), "alphaBean", "middleBean", "zetaBean") + System.lineSeparator();
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
        verify(applicationContext).getBeanDefinitionNames();
    }

    @Test
    void shouldNotPrintAnythingWhenLogLevelIsNotDebug() throws Exception {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        BeanChecker beanChecker = new BeanChecker(applicationContext);
        ReflectionTestUtils.setField(beanChecker, "logLevel", "info");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            beanChecker.run();
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("", output.toString(StandardCharsets.UTF_8));
        verify(applicationContext, never()).getBeanDefinitionNames();
    }

    @Test
    void shouldThrowExceptionWhenLogLevelIsNull() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        BeanChecker beanChecker = new BeanChecker(applicationContext);
        ReflectionTestUtils.setField(beanChecker, "logLevel", null);

        assertThrows(NullPointerException.class, beanChecker::run);
        verify(applicationContext, never()).getBeanDefinitionNames();
    }

}
