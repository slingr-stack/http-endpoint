# Javascript API

The Javascript API of the http endpoint has three pieces:

- **HTTP requests**: These allow to make regular HTTP requests.
- **Shortcuts**: These are helpers to make HTTP request to the API in a more convenient way.

## HTTP requests
You can make `GET` requests to the [http API](API_URL_HERE) like this:
```javascript
var response = app.endpoints.http.get('/sync')
```

Please take a look at the documentation of the [HTTP endpoint](https://github.com/slingr-stack/http-endpoint#javascript-api)
for more information about generic requests.

## Shortcuts

Instead of having to use the generic HTTP methods, you can (and should) make use of the helpers provided in the endpoint:
<details>
    <summary>Click here to see all the helpers</summary>

<br>

* API URL: '/sync'
* HTTP Method: 'GET'
```javascript
app.endpoints.http.sync.get()
```
---

</details>