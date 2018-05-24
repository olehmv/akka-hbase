package hbase;

import com.flipkart.hbaseobjectmapper.HBColumn;
import com.flipkart.hbaseobjectmapper.HBRecord;
import com.flipkart.hbaseobjectmapper.HBRowKey;
import com.flipkart.hbaseobjectmapper.HBTable;
import lombok.*;

@Data
@HBTable("person")
public class Person implements HBRecord {

    public Person(String id, String name){
        this.id=id;
        this.name=name;
    }
    public Person(){}

    @HBRowKey
    private String id;
    @HBColumn(family= "info",column = "name")
    private String name;

    @Override
    public String composeRowKey() {
        return id;
    }

    @Override
    public void parseRowKey(String rowKey) {
        this.id=rowKey;
    }

    @Override
    public String toString() {
        return id+" "+name;
    }
}
