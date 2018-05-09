package org.kududb.spark.demo.gamer.aggregates

import java.util
import java.util.ArrayList

import org.kududb.ColumnSchema.ColumnSchemaBuilder
import org.kududb.client.{CreateTableOptions, KuduClient}
import org.kududb.{ColumnSchema, Schema, Type}

object CreateGamerAggregatesKuduTable {
  def main(args:Array[String]): Unit = {
    if (args.length == 0) {
      println("{kuduMaster} {tableName}")
      return
    }

    val kuduMaster = args(0)
    val tableName = args(1)
    val numberOfBuckets = args(2).toInt

    val kuduClient = new KuduClient.KuduClientBuilder(kuduMaster).build()
    val columnList = new util.ArrayList[ColumnSchema]()

    columnList.add(new ColumnSchemaBuilder("gamer_id", Type.STRING).key(true).build())
    columnList.add(new ColumnSchemaBuilder("last_time_played", Type.INT64).key(false).build())
    columnList.add(new ColumnSchemaBuilder("games_played", Type.INT32).key(false).build())
    columnList.add(new ColumnSchemaBuilder("games_won", Type.INT32).key(false).build())
    columnList.add(new ColumnSchemaBuilder("oks", Type.INT32).key(false).build())
    columnList.add(new ColumnSchemaBuilder("deaths", Type.INT32).key(false).build())
    columnList.add(new ColumnSchemaBuilder("damage_given", Type.INT32).key(false).build())
    columnList.add(new ColumnSchemaBuilder("damage_taken", Type.INT32).key(false).build())
    columnList.add(new ColumnSchemaBuilder("max_oks_in_one_game", Type.INT32).key(false).build())
    columnList.add(new ColumnSchemaBuilder("max_deaths_in_one_game", Type.INT32).key(false).build())
    val schema = new Schema(columnList)

    if (kuduClient.tableExists(tableName)) {
      println("Deleting Table")
      kuduClient.deleteTable(tableName)
    }
    val builder = new CreateTableOptions()

    val hashColumnList = new ArrayList[String]
    hashColumnList.add("gamer_id")

    builder.addHashPartitions(hashColumnList, numberOfBuckets)

    println("Creating Table")
    kuduClient.createTable(tableName, schema, builder)
    println("Created Table")
    kuduClient.shutdown()
  }
}
