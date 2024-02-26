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

## Installation

Clone the repository:

```bash
    git clone https://github.com/1kotik/tvshow-api
```

## Usage
### Type Request
In command line:
```
curl -X GET "http://localhost:8080/tvshows?title={your-title}"
```

In browser (or platforms for using APIs (Postman)):
```
http://localhost:8080/tvshows?title={your-title}
```

## JSON Example

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



