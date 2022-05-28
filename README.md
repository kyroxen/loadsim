# The loadsim
A simple dumb load simulator.

## Sample curl for testing a GET endpoint:
```
curl --location --request POST 'http://localhost:8085/load-sim' \
--header 'Content-Type: application/json' \
--data-raw '{
    "threadCount": 1000,
    "taskCount": 1000,
    "inputDto": {
        "type": "GET",
        "timeout": 10000,
        "url": "http://localhost:8080/api/analysis?param1=888888888&from=2020-01-02&to=2020-03-03",
        "payload": null
    }
}'

```

## Sample curl for testing a POST endpoint:
```
curl --location --request POST 'http://localhost:8085/load-sim' \
--header 'Content-Type: application/json' \
--data-raw '{
    "threadCount": 1000,
    "taskCount": 1000,
    "inputDto": {
        "type": "POST",
        "timeout": 10000,
        "url": "http://localhost:8080/api/resource",
        "payload": "{\r\n \"userId\": \"abc123\",\r\n  \"key\": \"value\",\r\n }"
    }
}'

```
