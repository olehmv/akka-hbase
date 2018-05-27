package hbase.entity;

import com.flipkart.hbaseobjectmapper.HBColumn;
import com.flipkart.hbaseobjectmapper.HBRecord;
import com.flipkart.hbaseobjectmapper.HBRowKey;
import com.flipkart.hbaseobjectmapper.HBTable;

import java.util.List;
import java.util.Map;

@HBTable("person")
public class Person implements HBRecord{

    public Person(String phoneNumber, String name){
        this.phoneNumber =phoneNumber;
        this.name=name;
    }
    public Person(){}
    @HBRowKey
    private String phoneNumber;
    @HBColumn(family = "info",column = "name")
    private String name;
    @HBColumn(family = "info",column = "zipcode")
    private Integer zipcode;
    @HBColumn(family = "image",column="foto")
    private Map<String,byte[]> fotos;

    public void setFotos(Map<String, byte[]> fotos) {
        this.fotos = fotos;
    }

    public Map<String, byte[]> getFotos() {
        return fotos;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public int getZipcode() {
        return zipcode;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String composeRowKey() {
        return phoneNumber;
    }

    @Override
    public void parseRowKey(String rowKey) {
        this.phoneNumber =rowKey;
    }

    @Override
    public String toString() {
        return phoneNumber +" "+name;
    }
}
