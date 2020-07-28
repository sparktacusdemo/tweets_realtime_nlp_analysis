//-- data streaming NLP Analysis 2020
/*
the code below, read the data stored in a Kafka topic, and process the data stream
the data processing is a NLP algorithm (spark pipeline)
then the computation results are saved to mongodb collection
a connector between Zeppelin/mongodb is done further in the workflow, to visualize the results of Tweets NLP analysis
*/

//import libraries and packages
//--------------------------------
import org.apache.spark.sql.SparkSession
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.sql.streaming.StreamingQueryListener._
import org.apache.spark.sql.streaming.StreamingQueryException
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.ml.feature.{VectorAssembler,VectorSizeHint}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.PipelineStage
import org.apache.spark.sql.Row
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.linalg._
import org.apache.spark.ml.stat._
import org.apache.spark.sql.types._
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.feature.SQLTransformer
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{ArrayType, IntegerType, MapType, StringType, StructType}
import org.apache.spark.sql.SaveMode

//Json4s
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization.{read => JsonRead}
import org.json4s.jackson.Serialization.{read, write}
import org.json4s.JsonDSL._

//Hadoop 
import org.apache.hadoop.fs._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.FileSystem

//scala, java packages
import scala.collection.immutable.List 
import scala.collection.immutable.Map
import scala.util.Random 
import scala.collection.mutable.HashMap 

//SPARK NLP
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import com.johnsnowlabs.nlp.pretrained.ResourceDownloader
import com.johnsnowlabs.nlp.SparkNLP
import com.johnsnowlabs.nlp.annotators.Tokenizer
import com.johnsnowlabs.nlp.annotators.TokenizerModel
import com.johnsnowlabs.nlp.annotators.pos.perceptron.PerceptronModel
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector
import com.johnsnowlabs.nlp.annotators.sda.pragmatic.SentimentDetector
import com.johnsnowlabs.nlp.annotators.sda.vivekn.ViveknSentimentApproach
import com.johnsnowlabs.nlp.annotators.sda.vivekn.ViveknSentimentModel
import com.johnsnowlabs.nlp.AnnotatorApproach
import com.johnsnowlabs.nlp.DocumentAssembler
import com.johnsnowlabs.nlp.Finisher
import com.johnsnowlabs.nlp.HasInputAnnotationCols
import com.johnsnowlabs.nlp.HasOutputAnnotationCol
import com.johnsnowlabs.nlp.HasOutputAnnotatorType
import com.johnsnowlabs.nlp.LightPipeline
import  com.johnsnowlabs.nlp.util.regex.RuleFactory
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.annotator._

//mongo db
import org.bson.Document
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.rdd.api.java.JavaMongoRDD
import com.mongodb.spark.config.ReadConfig
import com.mongodb.spark.sql._

//----------------------------------------------------------------------------------------------


//-------
//  CODE
//--------------------------------------------------------------------------------------


object TweetsNLP {
  
  def main(args: Array[String]) {
   
    println("scala KAFKA SPARK MONGO - NLP TWeets - 2020")
    
    //set up Spark environment
    val jars = Seq(
        //here are listed all required .jar files  (scala app, kafka, spark kafka, hdfs, mongo db, etc)
    )
    val conf = new SparkConf()
    conf.setAppName("App-TweetsNLP2020")
    conf.setMaster("spark://pclocalhost:7077")   //standalone master
    conf.setJars(jars)
    conf.set("spark.driver.memory", "1g")
    conf.set("spark.executor.memory", "2g")
    //conf set up for spark/mongo db system
    conf.set(//..spark mongodb...//)
		//spark conf set up for hadoop hdfs
    conf.set(//..hdfs...//);
    
		//create SparkSession
    val spark = SparkSession.builder
    .config(conf)
    .config("spark.sql.streaming.checkpointLocation","hdfs://localhost:9000/sparkStreamingStructKafka/checkpoints_Mongodbkafkatwitter_scala001")
	  .config("spark.streaming.stopGracefullyOnShutdown","true")
	  .config("spark.debug.maxToStringFields", 2000)
    .getOrCreate()
    
    //set up for Kafka
		// Subscribe to 1 topic (twitter_topic_status_nbalakers)
		val dfKafka = spark
	    .readStream
		  .format("kafka")
		  .option("kafka.bootstrap.servers", "localhost:9092")
		  .option("subscribe", "kafka_topic_name")
		  .load();
    
    //read data from kafka and build a spark dataset for data processing
    dfKafka.createOrReplaceTempView("dftemp983");
		val sqlDF = spark.sql("SELECT FLOOR(RAND() * 9999999) as id, CAST(value AS STRING) AS text FROM dftemp983");
   
		System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("+        Data Processing - Spark NLP      +");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++\n");
		
		//SET up NLP pipeline 
		//---------------------
		//load the NLP Models (2 models)
		val analyze_sentiment_en00 = PipelineModel.load("/hdfs_folder/analyze_sentiment_en_2.4.0_2.4_1580483464667")
		val recognize_entities_dl_en01 = PipelineModel.load("/hdfs_folder/recognize_entities_dl_en_2.4.3_2.4_1584626752821")
		
    //definition of the pipeline stages
		val array3 = Array("document", "sentence", "token", "checked", "sentiment", "document", "sentence", "token", "embeddings", "ner", "entities")
		val finisher02 = new Finisher()
				.setInputCols(array3)
				.setIncludeMetadata(true);
		val arr_stages = Array(analyze_sentiment_en00,recognize_entities_dl_en01,finisher02)
		
		val pipelineNLP = new Pipeline()
			    .setStages(arr_stages)
		
    //fit the pipeline on data stream
		val modelNLP00 = pipelineNLP.fit(sqlDF);
		val pipmodtransfo_output = modelNLP00.transform(sqlDF);	    
		
		
		//---------------------------
		//set query stream and start
		//---------------------------
		
		 import spark.implicits._  //required to use explode
		 
		    //----------------------------------------------------
				//- save NLP results to MONGO DB Collection
				//-----------------------------------------------------
		    
      		val query00 = pipmodtransfo_output.select(
      				//here, list the fields to select
      				)
      				  .writeStream
      				  .Batch { () =>
                  batchDF.persist()
                  MongoSpark.write(batchDF.select(
      				       //here, list the fields to export
      						    		)).option("collection", "mongodb_collection_name").mode("append").save();
                  batchDF.unpersist()
                  }.start()
		  
                  
          		try {
              			query00.awaitTermination()
              			
              		} 
                 catch {
                     //here , code to handle error e
                 }
				  

  }
  
}
