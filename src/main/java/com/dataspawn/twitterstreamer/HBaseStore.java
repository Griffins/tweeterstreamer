package com.dataspawn.twitterstreamer;

import com.mongodb.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Created by griffins on 7/14/15.
 */
public class HBaseStore implements Store {


    HTable hTable;

    public HBaseStore() throws Exception {
        Configuration hConf = HBaseConfiguration.create();
        hConf.set("hbase.zookeeper.quorum", Settings.getHbaseZookeeperQuorum());
        hConf.set("base.zookeeper.property.clientPort", String.valueOf(Settings.getHbaseZookeeperClientPort()));
        hTable = new HTable(hConf, Settings.getHBaseTableName());
    }

    public void add(String tweet) throws IOException {
        Put put = new Put(Bytes.toBytes("tweet"));
        put.addColumn(Bytes.toBytes("tweet"), Bytes.toBytes("tweet"), Bytes.toBytes(tweet));

        hTable.put(put);
    }
}