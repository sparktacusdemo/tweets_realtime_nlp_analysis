# Tweets Real-Time NLP Analysis

## Project's Presentation

### Purpose <br>
Real-time Analyze the tweets, by applying NLP algorithms. The application will bring us insights about a specific subject,or theme.
For example, the app can analyze the sentiment (negative, positive, neutral) of a set of tweets that concern a specific topic.
<br>

### Technical Environment <br>
The app is built in a scalable system, using the tools below: <br>
- apache kafka (for the data ETL and streaming data source parts) <br>
- apache Spark (for the data processing (NLP)) <br>
- Spark NLP (John Snow Labs) <br>
- HDFS (hadoop) (to store the App jar file, and others files (third jar files, NLP models, etc) required to deploy the app
- MongoDB (to store the tweets, and the machine Learning computation results) <br>
- zeppelin (data visualization) <br>
- ECLIPSE (as IDE)

in terms of computing resources, we can deploy the app on  <br>
1. local mode (using the spark cluster (standalone mode), app depends of local machine) <br>
2. cluster mode (mesos cluster (using zookeeper quorum) <br>

<br>

**The app is written in Scala language**

### Workflow
![alt text](https://github.com/sparktacusdemo1/tweets_realtime_nlp_analysis/blob/master/img001.png)
<br><br>
[click here to enlarge the schema](https://github.com/sparktacusdemo1/tweets_realtime_nlp_analysis/blob/master/Workflow_presentation.pdf)
<br>

### Points to set
- Kafka Connect: source (Twitter) and sink connectors (MongoDB)
- Mongo DB collections
- Eclipse IDE project (e.g POM.xml file)
- Spark
- HDFS (folders system for the app)
- Zeppelin (MongoDB interpreter to read data stored in collections)
- MESOS resource manager (if cluster mode deployment) (cluster is built on Aws EC2 instances)

### Zeppelin Notebook
[dashboard](http://localhost:8180/)
