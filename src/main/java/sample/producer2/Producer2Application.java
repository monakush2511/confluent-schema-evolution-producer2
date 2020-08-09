package sample.producer2;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import com.example.Sensor;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.EnableSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
@EnableSchemaRegistryClient
@SpringBootApplication
@RestController
public class Producer2Application {

	@Autowired
	private KafkaTemplate<String,Object> template;
	private Random random = new Random();

	BlockingQueue<Sensor> unbounded = new LinkedBlockingQueue<>();
		
	// injected from application.properties
    @Value("${topic.name}")  
	  private String topicName;

    @Value("${topic.partitions-num}")
	  private int numPartitions;

    @Value("${topic.replication-factor}")
	  private int replicas;

	  @Bean
	  NewTopic moviesTopic() {
	    return new NewTopic(topicName, numPartitions, (short) replicas);
	  }

	public static void main(String[] args) {
		SpringApplication.run(Producer2Application.class, args);
	}

/*	private Sensor randomSensor() {
		Sensor sensor = new Sensor();
		sensor.setId(UUID.randomUUID().toString() + "-v2");
		sensor.setAcceleration(random.nextFloat() * 10);
		sensor.setVelocity(random.nextFloat() * 100);
		sensor.setInternalTemperature(random.nextFloat() * 50);
		sensor.setAccelerometer(null);
		sensor.setMagneticField(null);
		return sensor;
	}*/

	@PostMapping(value = "/messages")
	public String sendMessage() {
		Sensor sensor = new Sensor();
		sensor.setId(UUID.randomUUID().toString() + "-v2");
		//sensor.setRegion("region");
		//sensor.setAcceleration(random.nextFloat() * 10);
		sensor.setVelocity(random.nextFloat() * 100);
		sensor.setExternalTemperature(random.nextFloat() * 50);
		//sensor.setInternalTemperature(random.nextFloat() * 50);
		sensor.setAccelerometer(null);
		sensor.setMagneticField(null);
		//unbounded.offer(randomSensor());
		template.send(topicName,sensor);
		return "ok, have fun with v2 payload!";
	}

	/*@Bean
	public Supplier<Sensor> supplier() {
		return () -> unbounded.poll();
	}*/

@Configuration
	static class ConfluentSchemaRegistryConfiguration {
		@Bean
		public SchemaRegistryClient schemaRegistryClient(@Value("${spring.kafka.properties.schema.registry.url}") String endpoint){
			ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
			client.setEndpoint(endpoint);
			return client;
		}
	}
}

