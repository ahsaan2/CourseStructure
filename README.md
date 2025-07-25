# CourseStructure

## Elasticsearch Configuration

This application is configured to connect to a local Elasticsearch instance running at `localhost:9200`.

**Default configuration:**

```
src/main/resources/application.properties

spring.elasticsearch.uris=http://localhost:9200
```

If your Elasticsearch instance is running on a different host or port, update the `spring.elasticsearch.uris` property in `application.properties` accordingly.

No further modification is needed for a default local setup.

---

## Bulk-Indexing Sample Data

On application startup, the app will automatically read `sample-courses.json` from `src/main/resources` and bulk-index all course objects into the Elasticsearch `courses` index **if the index is empty**.

### How to Trigger Data Ingestion
- Place your `sample-courses.json` file in `src/main/resources`.
- Start the application (`mvn spring-boot:run` or run from your IDE).
- On first run (when the index is empty), the data will be loaded automatically.

### How to Verify
- Check your application logs for the message:
  - `Sample courses indexed into Elasticsearch.`
- You can also verify by querying Elasticsearch directly:
  - Visit: `http://localhost:9200/courses/_search` in your browser or use a tool like Postman/curl.

If you add or change data in `sample-courses.json`, delete the `courses` index in Elasticsearch to trigger re-ingestion on the next startup.
