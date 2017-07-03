package com.jobservice.tasktracker;

import com.github.ltsopensource.spring.boot.annotation.EnableTaskTracker;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by ningyang on 2017/4/11.
 * 这是一个任务处理节点
 * 1 这里专门处理在job-node上面提交的各种任务以及cron表达式的任务
 * 2 这里包含任务处理逻辑, 可以通过spring的service注解来实现你的服务
 * 3 你的服务可以使用mysql来做数据库的存储, 也可以调用dubbo来处理你的逻辑
 * 4 使用mysql的话需要在pom文件中加入mysql的依赖, 实现一个jpaConfig来配置你的jpa和datasource, 跟其他项目一样, 你可以copy过来
 *  <dependency>
 *      <groupId>mysql</groupId>
 *      <artifactId>mysql-connector-java</artifactId>
 *      <scope>runtime</scope>
 *      </dependency>
 *  <dependency>
 *      <groupId>com.alibaba</groupId>
 *      <artifactId>druid</artifactId>
 *      <version>1.0.17</version>
 *  </dependency>
 * 5 调用分布式dubbo服务, 你需要加入dubbo的依赖, 使用方式可以参照其他项目的dubbo方式,在resource目录下面放入dubbo
 *   provider或者consumer配置文件(xml), 然后在@Link{TaskTrackerStarter}上面加入@ImportResource注解引入dubbo文件
 *  <dependency>
 *      <groupId>com.alibaba</groupId>
 *      <artifactId>dubbo</artifactId>
 *      <version>${dubbo.version}</version>
 *      <exclusions>
 *          <exclusion>
 *              <groupId>org.springframework</groupId>
 *              <artifactId>spring</artifactId>
 *          </exclusion>
 *      </exclusions>
 *  </dependency>
 *
 *  <dependency>
 *      <groupId>org.apache.zookeeper</groupId>
 *      <artifactId>zookeeper</artifactId>
 *      <version>${zookeeper.version}</version>
 *      <exclusions>
 *          <exclusion>
 *              <groupId>javax.jms</groupId>
 *              <artifactId>jms</artifactId>
 *          </exclusion>
 *          <exclusion>
 *              <groupId>com.sun.jdmk</groupId>
 *              <artifactId>jmxtools</artifactId>
 *          </exclusion>
 *          <exclusion>
 *              <groupId>com.sun.jmx</groupId>
 *              <artifactId>jmxri</artifactId>
 *          </exclusion>
 *          <exclusion>
 *              <groupId>junit</groupId>
 *              <artifactId>junit</artifactId>
 *          </exclusion>
 *          <exclusion>
 *              <artifactId>slf4j-log4j12</artifactId>
 *              <groupId>org.slf4j</groupId>
 *          </exclusion>
 *      </exclusions>
 *  </dependency>
 */
@SpringBootApplication(scanBasePackages = "com.jobservice.tasktracker")
@EnableTaskTracker
@ImportResource("classpath:dubbo/dubbo-consumer.xml")
public class TaskTrackerStarter extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TaskTrackerStarter.class);
    }

    public static void main(String[] args){
        SpringApplication app = new SpringApplication(TaskTrackerStarter.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
