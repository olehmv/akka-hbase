package hbase;

import com.flipkart.hbaseobjectmapper.HBColumn;
import com.flipkart.hbaseobjectmapper.HBTable;
import com.flipkart.hbaseobjectmapper.*;
import com.flipkart.hbaseobjectmapper.codec.*;

@HBTable("car")
public class Car implements HBRecord{
    @HBRowKey
    private String aid;
    @HBColumn(family = "info",column = "name")
    private String name;

    public Car(){

    }

    public Car(String aid, String name){
        this.aid=aid;
        this.name=name;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String composeRowKey() {
        return aid;
    }

    @Override
    public void parseRowKey(String rowKey) {
            this.aid=rowKey;
    }
}
