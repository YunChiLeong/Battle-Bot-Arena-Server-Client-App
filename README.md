# Battle-Bot-Arena-Server-Client-App

## What is it?
A client program that controls a bot in the arena. The game's timing is based on the number of messages received by the server from the client. When movement rate, number of shots and recharge rate are discussed below, it is based on the messages received by the server, but the server protocol will determine a regular the amount of time between messages.

The client controls the movement and firing of the bot. You will also be able to scan
the area around you bot for other bots and bullets. During the setup of your bot, you will be able to
choose to add to the attributes of your bot.

## Running the Server
The server takes at least 1 parameters which is the number of players.
                  java -jar BatBot161.jar  2  
starts it up with 2 players
![image](https://user-images.githubusercontent.com/47125700/170398188-a8748d26-8ee0-4ad0-a172-c4260804e31f.png)

### Multiplayer Game
For teams you need an extra parameter, the number of players per team.
  2 teams of 2 players
                  java -jar BatBot161.jar  4  2
  2 teams, where it's 3 on one team and 1 on the second is
                  java -jar BatBot161.jar  4  3  

## Run an AI Bot to Play Against
Running the bot client jar takes 2 parameters : hostname and port number.  
The port is always 3012, localhost if you are running on the same machine.
                  java -jar strafe.jar localhost 3012
connects a bot called Strafe to the server
![image](https://user-images.githubusercontent.com/47125700/170398778-ae513395-739f-4663-a00d-4c9f4d60918a.png)
When a client is connected to the server succesfully, the server prints client PID: status

## Game Setup Stage
After the connection, server sends you:
PID WidthArena HeightArena NumberOfBots Team
*note: Team is your team number. You cannot hurt/be hurt by player on the same team. If Team is 0, there are no teams.

Example message: 1 250 250 2 1 *You are player 1, arena is 250x250, there are 2 players, you are on Team 1.

## Player Stats Customization
At this point, server expects client to send back:
NameOfBot ArmourValue BulletValue ScanValue
Example message: Superbot 0 0 3 *note: does not need to use all 5 points

## Start the Game
Once clients receive the first status message the game has started and the server sends a status
message every time the server is ready to accept a new action from the client.
status message:
Status X Y MoveCount ShotCount HP
                  example: Status 162 110 0 0 2 
at position 162,110 You can shoot and move, Armor value of 2
                  example: Status 162 110 0 -10 2
at position 162,110 You can move. 10 messages until you can shoot. Armor value of 2
Once the Status message is read, the server is now ready to receive one command. The client can
send 1 of 4 different messages: noop, move, fire, scan
1. Noop, do nothing, but MoveCount and ShotCount counts will increment if non-zero.
                  message: noop
2. Move, move 1 pixel in 1 of 8 directions, based on X and Y. If bot is able to move, then the bot
will be moved 1 pixel based on the direction indicated by x and y value. ShotCount will increment
if non-zero. If the bot cannot move, then MoveRate is decremented (as if you did move) but the
bot will not move and ShotCount will remain unchanged.
3. Fire, shoot a bullet based on an angle. If the bot is able to fire a bullet, then a bullet will be fired
in the direction of the angle. Remember, MoveCount will be decremented (see above). If the bot is
not able to fire, then ShotCount will be decremented ( as if you did shoot) and MoveCount will
remain unchanged.
                 message: fire ANGLE
4. Scan, then server sends information about the bullets, bots, and power ups within the scan
distance. Note if will return your bullets too. ShotCount and MoveCount will increment if nonzero.
                 message: scan

![image](https://user-images.githubusercontent.com/47125700/170397952-ae95e0c6-4f52-45b0-89ad-8f4a4bcea4ca.png)

### About how bots and bullets are drawn in the arena and collisions
All bots are drawn as a 10x10 square. The position show by the status message is the upper
left point. So if the bot status shows 150 150, the bot is drawn from 150,150 to 150,160, to 160,160
and 160,150, and back to 150,150. Bullets are also drawn as a square and the position shown in
the scan is the upper left point. The bullet power + 1 is the length of each side, so bullet power of 1
is drawn as 2x2 square and a bullet of 5 is a 6x6 square. Power Ups are drawn as a 10x10 square.
If a bot tries to move into a wall, then the move is prevented by the server (the message is
ignored) and no damage is taken by the bot.

