package com.example.outbrainApi;

import com.example.outbrainApi.outbrain.api.OutBrainApi;
import com.example.outbrainApi.outbrain.api.OutBrainApiImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		System.getProperties().put( "server.port", 8090 );

//		OutBrainApiImpl outBrainApi = new OutBrainApiImpl();
////		String newName = outBrainApi.createNewName("at-de-ob-a-bathroomrenovation", 1);
//		String newName = outBrainApi.createNewName("at-de-ob-a-bathroomrenovation", 2);
//		System.out.println(newName);


		SpringApplication.run(DemoApplication.class, args);
	}

}
