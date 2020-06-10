## Extras
- Jar path: `ItunesApi/api/build/libs/api-1.0-SNAPSHOT.jar`
- Tests report: `ItunesApi/api/build/reports/pitest/index.html`

## Modules
1. `dtos` - separate jar with DTO's for other services to consume
2. `api` - service itself
## Caching
### Search cache
If query exists in cache - it uses it, calls API otherwise, and caches its results in Redis for 24h.

### Top 5 Albums
If album exists in cache - it uses it, calls API otherwise, and caches it in Redis indefinitely. 
All existing albums in the cache are updated every midnight (in bulk, as Single query)

## Scaling
This application efficiently caches iTunes API results which allow our end-users to execute more than 100 queries per hour.
But 100 queries limitation still exists, here are my proposed workarounds:
1. Host multiple reverse proxies on different networks (possibly use something along the lines of AWS lambda for that?)
2. On the internet there are a bunch of free proxies, these proxies can be used to route our requests through since we are not sending any sensitive information.

## Technologies used
- Spring Boot
- WebFlux - Because that's the future of Spring
- Redis - For Itunes related data caching
- r2dbc - For users persistence
- PiTest - Because I am a huge fan of mutational testing
- Lombok - Because I am a special little snowflake
- Swagger - To make this homework reviewers life easier :)

## Commands
- `./gradlew bootRun` - to start the application (Note: external Redis instance is required)
- `./gradlew test` - to execute tests
- `./gradlew pitest` - to execute mutational tests (Takes couple minutes to complete)

## Rest endpoints
#### Users controller
User model example: ` { "id": 1, "name": "User 1", "favoriteAmgArtistsIds": [ 1, 2, 3 ] }`
- `GET /api/users`         - Get all users
- `POST /api/users`        - Create a new user 
- `GET /api/users/{id}`    - Get specific user   
- `PUT /api/users/{id}`    - Edit user 
- `DELETE /api/users/{id}` - Delete user 

#### Artists controller
- `GET /api/artists?term={artist_name}` - Search for artists
- `GET /api/artists/{id}/top`           - Get detailed artist info (Top 5 albums)
