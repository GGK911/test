// package springTest.task;
//
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.scheduling.annotation.SchedulingConfigurer;
// import org.springframework.scheduling.config.ScheduledTaskRegistrar;
// import org.springframework.scheduling.support.CronTrigger;
// import org.springframework.stereotype.Component;
//
// /**
//  * 定时任务测试
//  *
//  * @author TangHaoKai
//  * @version V1.0 2024/9/4 9:19
//  */
// @Slf4j
// @Component
// public class TaskTest implements SchedulingConfigurer {
//
//     @Value("${task.time1}")
//     private String taskTime1;
//
//     /**
//      * 普通的定时任务，写在任意一个component中即可执行
//      */
//     @Scheduled(cron = "0/5 * * * * ?")
//     public void task() {
//         log.info("**********定时任务:{}**********", System.currentTimeMillis());
//     }
//
//     /**
//      * 可配置的定时任务，需实现SchedulingConfigurer，重写configureTasks方法
//      *
//      * @param taskRegistrar 注册器
//      */
//     @Override
//     public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//         taskRegistrar.addTriggerTask(new Runnable() {
//             @Override
//             public void run() {
//                 log.info("**********定时任务2:{}**********", System.currentTimeMillis());
//             }
//         }, triggerContext -> {
//             // 任务触发，可修改任务的执行周期
//             CronTrigger trigger = new CronTrigger(taskTime1);
//             return trigger.nextExecutionTime(triggerContext);
//         });
//     }
// }
