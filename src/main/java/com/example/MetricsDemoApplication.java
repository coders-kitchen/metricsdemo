package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MetricsDemoApplication {

  private static PrometheusAccurateMeter accurateMeter = PrometheusAccurateMeter.build()
                                                                                .ratingInterval(10)
                                                                                .name("jo_meter")
                                                                                .help("Yo Meter")
                                                                                .register();


  public static void main(String[] args) {
    SpringApplication.run(MetricsDemoApplication.class, args);
  }


  @ResponseBody
  @RequestMapping("/hello")
  public String sayHello() throws InterruptedException {
    accurateMeter.mark();
    return "hello";
  }
}
