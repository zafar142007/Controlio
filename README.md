Controlio
=========

This is an Android app which communicates with a server on a desktop to issue commands to it by speech or touch input. These commands can be configured in a properties file on the server. The device which hosts this app needs to be on a network to talk to this server. Once started, the app needs the IP address and the port (the port can be configured according to you on the server by a GUI) on which the server is running. 
The server files are in the folder ControlioServer. 
The properties file has commands which are mapped to keyboard shortcuts of different OSs. Basically, there is a Java Robot class on the server which executes commands issued from the app based on the mappings it finds in the properties file. For example, on Windows OS, the command "close window" is mapped to Alt+F4. The properties file has a syntax to add commands, which is as follows:
1. Each line constitutes a command.
2. Structure of a command is as follows: <command_1> = <Key_code1>%<Key_code2>%...@<pattern>@<OS_name1>, <command_2> = <Key_code1>%<Key_code2>%...@<pattern>@<OS_name2>, ...
3. Key_code refers to a java.awt.event.KeyEvent constant for a keyboard key. For example, VK_A refers to the 'A' key, VK_ALT refers to the 'ALT' key. More could be found on http://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html/#VK_ENTER
4. A key combination is specified by a pattern in which the keys of the given combination are to be pressed. Each key can be pressed or released. A pattern is a string of english language alphabets a-z. In the order of the appearance of keys in the command alphabets a,b,c etc are assigned to presses and releases of the respective keys. For example, for the command Alt+Tab, 'a' refers to pressing of the first key in the combination (Alt), 'b' refers to release of the first key in the combination (Alt), 'c' refers to pressing ofthe second key in the combinaton (Tab), and 'd' refers to the release of the second key in the combination (Tab). Here, if the pattern for the command Alt+Tab is 'acdb' then it means that the Alt key is first pressed (and held), then Tab is pressed (and held), then Tab is released, and finally Alt is released.
5. A command string <command_x> needs to have underscores in places of spaces, if any.
6. <OS_namex> can be "nix" (*Nix like OSs) or "win" (Windows).
7. Two different commands can have the same key combinations.
