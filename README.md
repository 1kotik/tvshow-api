# TV Show API Application

REST API for searching information about TV Shows.

## Content
- [Functional](#functional)
- [Technologies](#technologies)
- [Installation](#installation)
- [Usage](#usage)
- [JSON Example](#json-example)
## Functional
- Searching TV Shows by title
- Returning results in JSON format

Note: Application can return list of TV Shows with entered title. Also you can provide incomplete title.

## Technologies:
- Java
- Spring Boot
- Lombok
- Maven
- JPA Hibernate
- PostgreSQL

## Installation

Clone the repository:

```bash
    git clone https://github.com/1kotik/tvshow-api
```

## Usage
### Type Request
#### In command line:
- GET from external API
```
curl -X GET "http://localhost:8080/tvshows/get-from-api?title={your-title}"
```
- GET from database
```
curl -X GET "http://localhost:8080/tvshows/get?title={your-title}"
```
- POST
```
curl -X POST "http://localhost:8080/tvshows/post {body}"
```
- DELETE
```
curl -X DELETE "http://localhost:8080/tvshows/delete?id={id}"
```
- PUT 
```
curl -X PUT "http://localhost:8080/tvshows/update {body}"
```

You can get do such requests with viewers using 
```
http://localhost:8080/viewers"
```


## JSON Example
### TV Show:
```json
[
    {
        "id": 20177,
        "title": "Severance",
        "permalink": "perseverance",
        "startDate": "2022-02-18",
        "endDate": null,
        "country": "UK",
        "network": "Apple TV+",
        "status": "Running",
        "imageThumbnailPath": "https://static.episodate.com/images/tv-show/thumbnail/20177.jpg"
    }
]
```

### Viewer:
```json
[
  {
    "id": 22,
    "age": 22,
    "name": "Amy",
    "country": "US"
  }
]
```

### Character:
```json
[
  {
    "id": 19,
    "name": "Sam",
    "feature": "Winchester"
  }
]
```



