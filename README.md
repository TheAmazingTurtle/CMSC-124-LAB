# ExQL
*Excel-Inspired Query Language*


**CREATOR**

>Kent Francis E. Genilo (TheAmazingTurtle)

>Jasmine G. Magadan (Jas-1005)

**LANGUAGE OVERVIEW**

>This is a natural-language-inspired programming language designed for processing CSV files. It takes inspiration from Excel and uses a declarative style to simplify accounting tasks.

**KEYWORDS**
1. **Data Import and Manipulation**
   - `load` - imports data from a CSV file into a collection
   - `save` - exports a collection or table to a CSV file
   - `from` - used with `load` to indicate source file of imported data
   - `to` - used with `save` to indicate destination file for exported data
   - `file` - used in `load` or `save` to specify a file name
   - `where` - filters or constrains data based on given condition/s
   - `create` - creates a new table or data structure
   - `table` - used to specify a name for a data table structure
   - `with` - used with `create` to specify number of rows and columns
   - `insert` - adds rows or columns to a table
   - `row/rows` - used with `insert` to specify number of rows in a table
   - `column/columns` -  used with `insert` to specify number of columns in a table
   - `after` - used with `insert` to indicate where to insert a new row or column relative to an existing one
   - `show` - displays an output value or data
   - `concat` - combines multiple strings or values into a si ngle output
<br><br>
2. **Structure and Control Flow**
   - `end` - marks the end of a code block or structure such as `if`, `for`, `while`, or a function
   - `if` - begins a conditional statement that executes only if provided condition is true
   - `then` - marks the start of an `if` block that runs when condition is true
   - `otherwise` - begins an alternate block executed when the `if` condition is false
   - `for` - starts a loop that iterates over each element in a collection
   - `each` - used with `for` to represent the current element in the iteration
   - `while` - starts a loop that continues executing as long as the specified condition is true
   - `do` - used with `while` to define a `do-while` loop that executes the block at least once before checking the condition
   - `based on` - starts a multi-branch decision structure, similar to switch or case statement
   - `when` - used with `based on` to define a specific case or condition
   - `it` - used with `when` inside a `based on` structure to refer to the current value being evaluated
   - `skip` - skips the current iteration of a loop, similar to continue in other languages
   - `escape` - exits the current loop immediately, similar to break in other languages
   - `quit` - terminates the entire program
<br><br>
3. **Assignment and Declaration**
   - `set` - used to assign or declare a variable, array, list, set, or function
   - `to` - indicates the value or expression being assigned
   - `as` - used with `set` to specify a type or a data structure
   - `at` - refers to a specific index or position within an array or a list
   - `index` - a numerical position that starts from 0 and is used to access or reference a specific element within an array or list
   - `array` -  used with `set` and `as` to specify that the variable is an array data structure
   - `list` -  used with `set` and `as` to specify that the variable is a list data structure
   - `set` -  used with `set` and `as` to specify that the variable is a set data structure
   - `containing` - used with `array`, `list`, or `set` to declare or initialize their elements
<br><br>
4. **Arithmetic and Logic**
   - `by` - used to connect `added`, `subtracted`, `multiplied`, and `divided` to the second operand
   - `added` - used with `by` to perform addition between two numbers, or two currencies
   - `subtracted` - used with `by` to perform subtraction between two numbers, or two currencies
   - `multiplied` - used with `by` to perform multiplication between two numbers, or two currencies
   - `divided` - used with `by` to perform division between two numbers, or two currencies with the same type
   - `in` - optional keyword used with arithmetic operators on currencies to indicate the resulting currency of the operation
   - `and` - a logical operator that returns true if both operands are true
   - `or` -  a logical operator that returns true if at least one operand is true
   - `is` - used with `it` or in comparisons to connect a value with a comparison operator
   - `not` - indicates logical negation and used to invert the truth value of a boolean expression
   - `to` - used with `equal` in comparison to indicate a comparison operator (equal to)
   - `than` - used with `less` or `greater` to indicate comparison operator (less than, greater than)
   - `equal` - a comparison operator that returns true if two values are equal
   - `less` - a comparison operator that returns true if the left value is smaller than the right value
   - `greater` - a comparison operator that returns true if the left value is larger than the right value
   - `true` - a boolean literal representing a true value
   - `false` - a boolean literal representing a false value
<br><br>
5. **Functions and Output**
   - `function` - used with `set` and `as` to specify that the variable declared is a function
   - `which` - used with `uses` in function declarations to introduce parameters
   - `uses` - used in function declarations to list the parameters
   - `that` - used with `does` in function declarations to introduce the body of a function
   - `does` - used in function declarations to indicate the body of a function
   - `using` - used when calling a function to provide arguments for its parameters
   - `begin` - marks the start of a function’s code block
   - `end` - marks the end of a function’s code block
   - `spit out` - returns a value from a function, similar to return
   - `sum` - a built-in function that calculates the sum of multiple values or a collection
   - `product` - a built-in function that calculates the product of multiple values or a collection
   - `of` - used with `sum` or `product` to indicate the values or collection of values on which the operation will be applied
<br><br>
6. **Currencies**
   - `php` - attached to a value to specify that it is in Philippine Peso
   - `usd` - attached to a value to specify that it is in US Dollars
   - `eur` - attached to a value to specify that it is in Euros
   - `gbp` - attached to a value to specify that it is in British Pounds
   - `jpy` - attached to a value to specify that it is in Japanese Yen
   - `cny` - attached to a value to specify that it is in Chinese Yuan
   - `krw` - attached to a value to specify that it is in South Korean Won
   - `inr` - attached to a value to specify that it is in Indian Rupees
   - `chf` - attached to a value to specify that it is in Swiss Francs
   - `brl` - attached to a value to specify that it is in Brazilian Reals
   - `sek` - attached to a value to specify that it is in Swedish Krona
   - `rub` - attached to a value to specify that it is in Russian Rubles
   - `try` - attached to a value to specify that it is in Turkish Lira
   - `mxn` - attached to a value to specify that it is in Mexican Pesos
   - `myr` - attached to a value to specify that it is in Malaysian Ringgit

**OPERATORS**
1. **Assignment**
   - `set` … `as` - declares a variable, array, list, set, or function and optionally assigns it a value
<br><br>
2. **Arithmetic **
   - `added by` - performs addition between two numbers or currencies
   - `subtracted by` - performs subtraction between two numbers or currencies
   - `multiplied by` - performs multiplication between two numbers or currencies
   - `divided by` - performs division between two numbers or currencies
<br><br>
3. **Logical**
   - `and` - returns true if both operands are true
   - `or` - returns true if at least one operand is true
   - `not` - returns the logical negation of an operand; inverts the truth value of a boolean expression
<br><br>
4. **Comparison**
   - `is equal to` - returns true if the left value is equal to the right value
   - `is greater than` - returns true if the left value is larger than the right value
   - `is greater than or equal to` - returns true if the left value is larger than the right value or equal to the right value
   - `is less than` - returns true if the left value is smaller than the right value
   - `is less than or equal to` - returns true if the left value is smaller than the right value or equal to the right value

**LITERALS**
1. **String**- enclosed in double “ ”
      - example :
        
            “This is a string” 

3. **Number**-  integer or decimal values
      - examples:

            1, 2, 3, 2004, 5.04, 10.05, 0.25** **

4. **Boolean**- logical values such as true or false
      - examples:

            true
            false

5. **Currency**- numeric values attached to a currency literal
      - examples

            10php, 5usd, 15eur

**IDENTIFIERS**
1. **General identifiers**
   - Both general variables and column headers start with $. The type is determined by context:
      - General variable names and column header names for table or CSV header must start with $. Example:
        
            $variable
            $column_header
2. **Naming convention**
    - snake case is recommended for readability. Example:

          $customer_name
    - language is case-sensitive. The two sample identifiers below are distinct. 

          $customer_name
          $Customer_Name
3. **Grid/Positional identifier**
    - refer to a specific position in a table, list, or array
    - The sample identifier below refers to cell at column A, row 1

          @A1
4. **Function identifiers**
    - functions are declared using *set … as function* and distinguished from variables by the* using* keyword.

**COMMENTS**
   - Nested comments are not allowed. Since the opening symbols (`***`) for block comments are the same as the closing symbols (`***`), any (`***`) inside a block comment will be treated as a block common terminator.
   - For line comment : Start with `#` and continue to the end of the line. 

    	 # This is a line comment
   - For block comment : Enclosed between `***` at the start and end, and can span multiple lines.


         *** This is 
         a block
         comment
    	 ***

**SYNTAX STYLE**
   - Whitespace (tabs, spaces, newlines) is not generally significant, except for separating keywords, identifiers, and values.
   - Statements are terminated by newline and are indented. No need for semicolors and other terminators.
   - Code blocks are delimited by `begin` and `end`
   - Commas (`,`) are used to separate multiple arguments or items in a list and array and the `and` keyword to signify the last item. 

**SAMPLE CODES**
1. **LOADING A FILE**

       load $transactions from file “sales.csv”

2. **SAVING A FILE**

       save $transactions from file “updated-sales.csv”

3. **FUNCTION**

       set  calculate_total as function which uses $price and $quantity that does
            begin
                 spit out $price multiplied by $quantity
            end

4. **CALLING A FUNCTION**

       calculate_total using 10 and 5

5. **ARRAY**

       set $daily_sales as array containing 500, 750, and 1000

6. **LIST**

       set $inventory_items as list containing “Laptop”, “Monitor”, and “Keyboard”

7. **SET**

       set $payment_methods as set containing “Credit Card”, “Gcash”, and “Cash” 

8. **ASSIGNING A VALUE TO A VARIABLE**

       set $total_sales to 10000

9. **FOR LOOP**

       for each $item in $numbers do
           show $item
       end_for

11. **WHILE LOOP**

        set $index to 0
        while $index is less than 10 do
              show $index
              set $index to $index added by 1
        end_while

13. **DO WHILE LOOP**

        set $index to 0
        do
            show $index
            set $index to $index added by 1
        while $index is less than 10

14. **IF ELSE**

        if $total is greater than 1000 then
             show "High"
        otherwise if $total is greater than 300 and $total is less than 700 then
             show "Mid”
        otherwise
             show "Low"**
        end_if

13. **BASED ON**

        based on $customer_type
              when it is "VIP" then
                   set $discount to 20
                   escape
              when it is "Regular" then
                   set $discount to 10
                   escape
              otherwise 
                   set $discount to 0
                   escape
        end_based

15. **CREATING A TABLE**
   - Either row/rows and column/columns can be used.
     
         create table $customer_names with 5 rows, 3 columns
         create table $customer_names with 5 row, 3 column

17. **INSERTING A COLUMN, A ROW**

        insert 3 columns after @Profit


**Design Rationale**
