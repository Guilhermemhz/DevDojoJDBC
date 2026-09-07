import Dominio.Producer;
import service.ProducerService;
import service.ProducerServiceRowSet;

import java.util.List;

public class Main2 {
    public static void main(String[] args) {
        Producer producerToUpdate = Producer.builder().id(1).name("MADHOUSE").build();
        ProducerServiceRowSet.updateCachedRowSet(producerToUpdate);
        /*List<Producer> producers = ProducerServiceRowSet.findByNameJdbcRowSet("");
        for (Producer x : producers) {
            System.out.println(x);
        } */

    }
}
