import Dominio.Producer;
import jdbc.conn.ConnectionFactory;
import lombok.extern.log4j.Log4j2;
import repository.ProducerRepository;
import service.ProducerService;

import java.sql.Connection;
import java.util.List;

@Log4j2
public class Main {
    public static void main(String[] args) {
        Producer producer = Producer.builder().name("Studio Deen").build();
        Producer producerToUpdate = Producer.builder().id(1).name("MADHOUSE").build();
        //ProducerService.save(producer);
        //ProducerService.delete(5);
        //ProducerService.update(producerToUpdate);
        //List<Producer> producers = ProducerService.findAll();
        //List<Producer> producers = ProducerService.findByName("Mad");
        /* for(Producer x : producers) {
            System.out.println(x);
        } */
        //ProducerService.showProducerMetaData();
        //ProducerService.showDriverMetaData();
        //ProducerService.showTypeScrollWorking();
        //List<Producer> producers = ProducerService.findByNameAndUpdateToUpperCase("Deen");
        /*List<Producer> producers = ProducerService.findByNameAndInsertWhenNotFount("A-1 pictures");
        for(Producer x : producers) {
            System.out.println(x);
        } */
        //ProducerService.findByNameAndDelete("A-1");
        //List<Producer> producers = ProducerService.findByNamePrepareStatement("Bones");
        List<Producer> producers = ProducerService.findByNameCallableStatement("nh");
        for (Producer x : producers) {
            System.out.println(x);
        }
    }
}