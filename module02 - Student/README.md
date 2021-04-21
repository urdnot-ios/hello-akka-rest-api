Exercise 1: Use the route `hello` as a `get`. Take in 2 parameters: user and message, return a JSON object that is formatted to a string saying "hello" to the user

Exercise 2: Use the route `simpleJson` as a `Post`. Take in a SimpleJsonRequest data structure that matches the inbound JSON format. Decode the message using circe's semi-automatic Derivation into a case class you construct. 

The inbound JSON data will look like this:
```
{"stringItem":"my stuff","intItem": 3, "listItem" : [1,2,3]}
```
Verify two thing about the inbound data:
 - Can it be parsed into valid JSON
 - Can it be parsed into the target datatype

If it passes, then handle the message by summing the values of the list and returning the data as a new case class which you convert to a JSON message. 

However: only return the summed int, not the full message.

If it fails at any point, return the appropriate message. 

Exercise 3: Use the route `jsonExtractor` as a `Post` to parse a JSON message using Circe's simple "parse" function. 

Listen for a valid JSON message with the following structure: 
```
{"id": "c730433b-082c-4984-9d66-855c243266f0","name": "Foo", "counts": [1, 2, 3], "values": {"bar": true, "baz": 100.001, "qux": ["a", "b"]}}
```
    
When your actor receives this generic Json type, use the Hcursor feature to find the double value and return just it to the sender.

Exercise 4: use the `weather` route as a `Post` to parse a JSON string and return it modified to a Unix timestamp.

Listen for a valid JSON message like this:
```
{"time": "2021-04-19 20:09:54", "model": "SwitchDoc Labs FT020T AIO", "avg_windspeed": 0, "wind_gust": 0, "wind_dir": 213, "total_rain": 3960, "temp": 62.3, "humidity": 42, "lux": 334, "uv_index": 0.0}
```
Copy the message into a data structure using the "optics" method from Circe.
Return the message the same way you found it but change the time field to be a Unix epoch timestamp (Long) at the Seconds level.
Send and return the message as a string.

How will you differentiate this string from the other string inputs?