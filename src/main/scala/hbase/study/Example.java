package hbase.study;

import hbase.connection.HBaseConnection;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;


/**
 * Examples:<p>
 * Scan By timestamp<p>
 * Scan with multiple filters<p>
 * Scan  with Caching rows and batching columns</p>
 * With Counters Increment
 */
public class Example {

    public static void main(String[] args) throws IOException {
        Date date = new Date(1416083300000l);
        System.out.println(date);

        HTable table = new HTable(HBaseConfiguration.create(), "t1");
        Scan s = new Scan();
        s.setMaxVersions(1);
        s.setTimeRange(0L, 1416083300000L);
        ResultScanner scanner = table.getScanner(s);
        for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
            System.out.println(Bytes.toString(rr.getRow()) + " => " +
                    Bytes.toString(rr.getValue(Bytes.toBytes("f1"), Bytes.toBytes("a"))));
        }

        // by default filterlist uses which means all filter must be passed (AND condition)
        FilterList.Operator mustPassAll = FilterList.Operator.MUST_PASS_ALL;
        // If you want to apply OR condition for filters use
        FilterList.Operator mustPassOne = FilterList.Operator.MUST_PASS_ONE;
        FilterList filterList = new FilterList(mustPassOne);
        filterList.addFilter(new RowFilter(null, null));
        s.setFilter(filterList);

        Put put = new Put(Bytes.toBytes("key"));
        List<Cell> cells = put.get(Bytes.toBytes(""), Bytes.toBytes(""));
        Cell cell = cells.get(0);

        //caching number of rows
        s.setCaching(100);
        //caching number of columns
        s.setBatch(1);
        PageFilter pageFilter = new PageFilter(10);
        s.setFilter(pageFilter);
        Result[] results = scanner.next(10);
        scanner.close();

        Increment increment = new Increment(Bytes.toBytes("20180101"));
        increment.addColumn(Bytes.toBytes("daily"), Bytes.toBytes("clicks"), 1);
        increment.addColumn(Bytes.toBytes("daily"), Bytes.toBytes("hits"), 1);
        increment.addColumn(Bytes.toBytes("weekly"), Bytes.toBytes("clicks"), 10);
        increment.addColumn(Bytes.toBytes("weekly"), Bytes.toBytes("hits"), 10);
        Result result1 = table.increment(increment);

        final HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(""));
        final boolean readOnly = hTableDescriptor.isReadOnly();
        hTableDescriptor.setMemStoreFlushSize(64);

        final HColumnDescriptor hColumnDescriptor = new HColumnDescriptor("");


    }

    public String[] getColumnsInColumnFamily(Result r, String ColumnFamily) {

        NavigableMap<byte[], byte[]> familyMap = r.getFamilyMap(Bytes.toBytes(ColumnFamily));
        String[] Quantifers = new String[familyMap.size()];

        int counter = 0;
        for (byte[] bQunitifer : familyMap.keySet()) {
            Quantifers[counter++] = Bytes.toString(bQunitifer);

        }

        return Quantifers;
    }
}
