NFO] --- spring-boot:3.2.0:run (default-cli) @ car-sales-system ---    
[INFO] Attaching agents: []
16:25:01,364 |-INFO in ch.qos.logback.classic.LoggerContext[default] - This is logback-classic version 1.4.11
16:25:01,373 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Here is a list of configurators discovered as a service, by rank:

16:25:01,374 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a -   org.springframework.boot.logging.logback.RootLogLevelConfigurator
16:25:01,374 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - They will be invoked in order until ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY is returned.
16:25:01,374 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Constructed configurator of type class org.springframework.boot.logging.logback.RootLogLevelConfigurator
16:25:01,406 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - org.springframework.boot.logging.logback.RootLogLevelConfigurator.configure() call lasted 0 milliseconds. ExecutionStatus=INVOKE_NEXT_IF_ANY
16:25:01,406 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Trying to configure with ch.qos.logback.classic.joran.SerializedModelConfigurator
16:25:01,407 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Constructed configurator of type class ch.qos.logback.classic.joran.SerializedModelConfigurator
16:25:01,469 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.scmo]
16:25:01,469 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.scmo]
16:25:01,469 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - ch.qos.logback.classic.joran.SerializedModelConfigurator.configure() call lasted 62 milliseconds. ExecutionStatus=INVOKE_NEXT_IF_ANY      
16:25:01,469 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Trying to configure with ch.qos.logback.classic.util.DefaultJoranConfigurator
16:25:01,480 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Constructed configurator of type class ch.qos.logback.classic.util.DefaultJoranConfigurator
16:25:01,481 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
16:25:01,481 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.xml]
16:25:01,481 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - ch.qos.logback.classic.util.DefaultJoranConfigurator.configure() call lasted 1 milliseconds. ExecutionStatus=INVOKE_NEXT_IF_ANY
16:25:01,481 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Trying to configure with ch.qos.logback.classic.BasicConfigurator
16:25:01,487 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - Constructed configurator of type class ch.qos.logback.classic.BasicConfigurator
16:25:01,487 |-INFO in ch.qos.logback.classic.BasicConfigurator@2dd10034 - Setting up default configuration.
16:25:01,573 |-INFO in ch.qos.logback.classic.util.ContextInitializer@77a9d41a - ch.qos.logback.classic.BasicConfigurator.configure() call lasted 86 milliseconds. ExecutionStatus=NEUTRAL
16:25:03,046 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - Processing appender named [CONSOLE]
16:25:03,047 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - About to instantiate appender of type [ch.qos.logback.core.ConsoleAppender]
16:25:03,056 |-INFO in ch.qos.logback.core.model.processor.ImplicitModelHandler - Assuming default type [ch.qos.logback.classic.encoder.PatternLayoutEncoder] for [encoder] property
16:25:03,081 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - Processing appender named [FILE]
16:25:03,081 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - About to instantiate appender of type [ch.qos.logback.core.rolling.RollingFileAppender]
16:25:03,089 |-INFO in ch.qos.logback.core.model.processor.ImplicitModelHandler - Assuming default type [ch.qos.logback.classic.encoder.PatternLayoutEncoder] for [encoder] property
16:25:03,110 |-INFO in c.q.l.core.rolling.TimeBasedRollingPolicy@621179235 - No compression will be used
16:25:03,111 |-INFO in c.q.l.core.rolling.TimeBasedRollingPolicy@621179235 - Will use the pattern logs/car-sales-system-%d{yyyy-MM-dd}.%i.log for the active file
16:25:03,193 |-INFO in ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP@5a228c54 - The date pattern is 'yyyy-MM-dd' from file name pattern 'logs/car-sales-system-%d{yyyy-MM-dd}.%i.log'.
16:25:03,193 |-INFO in ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP@5a228c54 - Roll-over at midnight.
16:25:03,217 |-INFO in ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP@5a228c54 - Setting initial period to 2025-12-20T08:20:46.383Z
16:25:03,217 |-WARN in ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP@5a228c54 - SizeAndTimeBasedFNATP is deprecated. Use SizeAndTimeBasedRollingPolicy instead
16:25:03,217 |-WARN in ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP@5a228c54 - For more information see http://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedRollingPolicy
16:25:03,223 |-INFO in ch.qos.logback.core.rolling.RollingFileAppender[FILE] - Active log file name: logs/car-sales-system.log
16:25:03,223 |-INFO in ch.qos.logback.core.rolling.RollingFileAppender[FILE] - File property is set to [logs/car-sales-system.log]
16:25:03,224 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - Processing appender named [OPERATION_LOG]
16:25:03,224 |-INFO in ch.qos.logback.core.model.processor.AppenderModelHandler - About to instantiate appender of type [ch.qos.logback.core.rolling.RollingFileAppender]
16:25:03,225 |-INFO in ch.qos.logback.core.model.processor.ImplicitModelHandler - Assuming default type [ch.qos.logback.classic.encoder.PatternLayoutEncoder] for [encoder] property
16:25:03,230 |-INFO in c.q.l.core.rolling.TimeBasedRollingPolicy@1670547982 - No compression will be used
16:25:03,230 |-INFO in c.q.l.core.rolling.TimeBasedRollingPolicy@1670547982 - Will use the pattern logs/operation-%d{yyyy-MM-dd}.log for the active file
16:25:03,231 |-INFO in c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy - The date pattern is 'yyyy-MM-dd' from file name pattern 'logs/operation-%d{yyyy-MM-dd}.log'.
16:25:03,231 |-INFO in c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy - Roll-over at midnight.
16:25:03,232 |-INFO in c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy - Setting initial period to 2025-12-20T08:20:46.383Z      
16:25:03,233 |-INFO in ch.qos.logback.core.rolling.RollingFileAppender[OPERATION_LOG] - Active log file name: logs/operation.log
16:25:03,233 |-INFO in ch.qos.logback.core.rolling.RollingFileAppender[OPERATION_LOG] - File property is set to [logs/operation.log]
16:25:03,234 |-INFO in ch.qos.logback.classic.model.processor.RootLoggerModelHandler - Setting level of ROOT logger to INFO
16:25:03,235 |-INFO in ch.qos.logback.classic.jul.LevelChangePropagator@4667e6e8 - Propagating INFO level on Logger[ROOT] onto the JUL framework  
16:25:03,237 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [CONSOLE] to Logger[ROOT]
16:25:03,237 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [FILE] to Logger[ROOT]
16:25:03,237 |-INFO in ch.qos.logback.core.model.processor.AppenderRefModelHandler - Attaching appender named [OPERATION_LOG] to Logger[ROOT]     
16:25:03,237 |-INFO in ch.qos.logback.classic.model.processor.LoggerModelHandler - Setting level of logger [com.carsales] to DEBUG
16:25:03,237 |-INFO in ch.qos.logback.classic.jul.LevelChangePropagator@4667e6e8 - Propagating DEBUG level on Logger[com.carsales] onto the JUL framework
16:25:03,237 |-INFO in ch.qos.logback.classic.model.processor.LoggerModelHandler - Setting level of logger [com.baomidou.mybatisplus] to DEBUG    
16:25:03,237 |-INFO in ch.qos.logback.classic.jul.LevelChangePropagator@4667e6e8 - Propagating DEBUG level on Logger[com.baomidou.mybatisplus] onto the JUL framework
16:25:03,238 |-INFO in ch.qos.logback.core.model.processor.DefaultProcessor@50fc1a2e - End of configuration.
16:25:03,238 |-INFO in org.springframework.boot.logging.logback.SpringBootJoranConfigurator@1ef26c49 - Registering current configuration as safe fallback point


  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2025-12-20 16:25:03 [background-preinit] INFO  org.hibernate.validator.internal.util.Version - HV000001: Hibernate Validator 8.0.1.Final
2025-12-20 16:25:03 [restartedMain] INFO  com.carsales.CarSalesApplication - Starting CarSalesApplication using Java 21.0.4 with PID 19484 (D:\github_study_repository\database-class-design\backend\target\classes started by hqhzj in D:\github_study_repository\database-class-design\backend)   
2025-12-20 16:25:03 [restartedMain] DEBUG com.carsales.CarSalesApplication - Running with Spring Boot v3.2.0, Spring v6.1.1
2025-12-20 16:25:03 [restartedMain] INFO  com.carsales.CarSalesApplication - The following 1 profile is active: "dev"
2025-12-20 16:25:03 [restartedMain] INFO  o.s.b.d.env.DevToolsPropertyDefaultsPostProcessor - Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2025-12-20 16:25:03 [restartedMain] INFO  o.s.b.d.env.DevToolsPropertyDefaultsPostProcessor - For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2025-12-20 16:25:05 [restartedMain] WARN  o.s.b.w.s.c.AnnotationConfigServletWebServerApplicationContext - Exception encountered during context initialization - cancelling refresh attempt: java.lang.IllegalArgumentException: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
2025-12-20 16:25:05 [restartedMain] INFO  o.s.b.a.logging.ConditionEvaluationReportLogger -

Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
2025-12-20 16:25:05 [restartedMain] ERROR org.springframework.boot.SpringApplication - Application run failed
java.lang.IllegalArgumentException: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
        at org.springframework.beans.factory.support.FactoryBeanRegistrySupport.getTypeForFactoryBeanFromAttributes(FactoryBeanRegistrySupport.java:86)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.getTypeForFactoryBean(AbstractAutowireCapableBeanFactory.java:838)
        at org.springframework.beans.factory.support.AbstractBeanFactory.isTypeMatch(AbstractBeanFactory.java:620)
        at org.springframework.beans.factory.support.DefaultListableBeanFactory.doGetBeanNamesForType(DefaultListableBeanFactory.java:573)        
        at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanNamesForType(DefaultListableBeanFactory.java:532)
        at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:138)
        at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:775)    
        at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:597)
        at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146)   
        at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:753)
        at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:455)
        at org.springframework.boot.SpringApplication.run(SpringApplication.java:323)
        at org.springframework.boot.SpringApplication.run(SpringApplication.java:1342)
        at org.springframework.boot.SpringApplication.run(SpringApplication.java:1331)
        at com.carsales.CarSalesApplication.main(CarSalesApplication.java:16)
        at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
        at java.base/java.lang.reflect.Method.invoke(Method.java:580)    
        at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:50)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  25.917 s
[INFO] Finished at: 2025-12-20T16:25:05+08:00
[INFO] ------------------------------------------------------------------------

=== Exit Code: 0 ===
请按任意键继续. . .