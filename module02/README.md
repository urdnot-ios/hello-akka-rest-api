Exercise 1:  Take in a simple get with parameters: user and message, return a JSON object that is formatted to a string saying "hello" to the user

Exercise 2: expect a SimpleJsonRequest, decode the message using circe's semi-automatic Derivation into a case class you construct. 

The inbound JSON data must be valid JSON and contain three elements:
1. stringItem with a string value
2. intItem with an integer value
3. list item with a list of integers

Verify two thing about the inbound data:
 - Can it be parsed into valid JSON
 - Can it be parsed into the target datatype

If it passes, then handle the message by summing the values of the list and returning the data as a new case class which you convert to a JSON message. 

If it fails at any point, return the appropriate message. 

Exercise 3: Parse a JSON message using Circe's simple "parse" function. 

Listen for a valid JSON message with the following structure: 
   1. id with a string value
   2. name with a string value 
   3. counts with a list of integers value 
   4. values with an array value that contains:
      - bar with boolean value 
      - baz with a double value
      - qux with a list of strings value
    
When your actor receives this generic Json type, use the Hcursor feature to find the double value and return just it to the sender.