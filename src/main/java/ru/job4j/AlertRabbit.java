package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {


    static Properties getProperties() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream("./data/rabbit.properties")) {
            properties.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public Connection init() throws ClassNotFoundException, SQLException {
        Connection cn;
        Class.forName(getProperties().getProperty("driver-class-name"));
        cn = DriverManager.getConnection(
                getProperties().getProperty("url"),
                getProperties().getProperty("username"),
                getProperties().getProperty("password")
        );
        return cn;
    }

    public static void main(String[] args) {
        AlertRabbit rabbit = new AlertRabbit();

        int interval = Integer.parseInt(getProperties()
                .getProperty("rabbit.interval"));
        int sleep = Integer.parseInt(getProperties()
                .getProperty("rabbit.sleep"));

        try {
            Connection cn = rabbit.init();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("cn", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(sleep);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("cn");
            try (PreparedStatement statement =
                         cn.prepareStatement("insert into rabbit(created_date) values (?)",
                                 Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, System.currentTimeMillis());
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}