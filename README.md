# Tweets Real-Time NLP Analysis

## Project's Presentation

### Purpose <br>
Real-time Analyze the tweets, by applying NLP algorithms. The application will bring us insights about a specific subject,or theme.
<br>

### Technical Environment <br>
The app is built in a scalable system, using the frameworks below: <br>
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
![alt text](https://github.com/sparktacusdemo1/tweets_realtime_nlp_analysis/blob/master/workflow.png)
<br><br>
[click here to enlarge the schema](https://github.com/sparktacusdemo1/tweets_realtime_nlp_analysis/blob/master/Workflow_tweets_realtime_nlp_analysis.pdf)
<br>

### Points to set
- Kafka Connect: source (Twitter) and sink connectors (MongoDB)
- Mongo DB collections
- Eclipse IDE project (e.g POM.xml file)
- Spark
- HDFS (folder system for the app)
- Zeppelin (e.g MongoDB interpreter)

### Zeppelin Notebook
<br>
[put the notebook link here]
