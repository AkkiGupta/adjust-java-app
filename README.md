# adjust-java-app
This is a sample demonstrating multiple use cases in java.

This app captures Even from users and post it on cloud.

It track the status of events which get processed i.e. uploaded to cloud and never re-submit a event to cloud.

It also takes care if multiple events not submitted to cloud by making use of local database (Sqlite using content provider)

Also it make use of State machine design pattern to track all the events and their processing.
