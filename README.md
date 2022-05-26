# Battle-Bot-Arena-Server-Client-App

## What is it?
A client program that controls a bot in the arena. The game's timing is based on the number of messages received by the server from the client. When movement rate, number of shots and recharge rate are discussed below, it is based on the messages received by the server, but the server protocol will determine a regular the amount of time between messages.

The client controls the movement and firing of the bot. You will also be able to scan
the area around you bot for other bots and bullets. During the setup of your bot, you will be able to
choose to add to the attributes of your bot.

## Running the Server
The server takes at least 1 parameters which is the number of players

![image](https://user-images.githubusercontent.com/47125700/170401685-37a1cae8-53e2-4254-a10e-3151fd7e1e21.png)

//This command starts a game with 2 players

## Connect from the Client App
Input the host server ip address in the Client Mobile App.
![download1](https://user-images.githubusercontent.com/47125700/170406345-1ff50584-a223-4cbf-b5b7-3dec3dfe7c99.png)

## Run an AI Bot to Play Against
Running the bot client jar takes 2 parameters : hostname and port number.  
The port is always 3012, localhost if you are running on the same machine
![image](https://user-images.githubusercontent.com/47125700/170398778-ae513395-739f-4663-a00d-4c9f4d60918a.png)
This command connects a bot called Strafe to the server
When a client is connected to the server succesfully, the server prints client PID: status

## ...Or Play Against Friends in Multiplayer Games
To assign teams you need an extra parameter, the number of players per team.

*  java -jar BatBot161.jar  4  2  (4 players in total, 2 teams of 2 players)     
*  java -jar BatBot161.jar  4  3  (4 players in total, 2 teams, where it's 3 on one team and 1 on the second)

## Game Setup Stage
After the connection, server sends you:   PID WidthArena HeightArena NumberOfBots Team
Team is your team number. You cannot hurt/be hurt by player on the same team. If Team is 0, there are no teams.
Messages to and from server are updated and displayed at the top right in the app.

### Bot Build Customizations
At this point, server expects client to send back:    NameOfBot ArmourValue BulletValue ScanValue
![dwld2](https://user-images.githubusercontent.com/47125700/170407022-8e3a73bc-9da5-4471-97ad-4c97f9463680.png)

## The Game Has Started!
Once clients receive the first status message the game has started and the server sends a status
message every time the server is ready to accept a new action from the client.
status message:
Status X Y MoveCount ShotCount HP

example: Status 162 110 0 0 2 
*means at position 162,110 You can shoot and move, Armor value of 2*
![download3](https://user-images.githubusercontent.com/47125700/170408031-f7639988-c7fc-497e-894e-ec0abd64fda2.png)

![image](https://user-images.githubusercontent.com/47125700/170403989-6f3d5f27-7925-4793-b156-50e811243a8a.png)

### Noop, Move, Fire, Scan!
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

## Server Client Activities Log
![image](https://user-images.githubusercontent.com/47125700/170403859-9cc90cfb-6536-4cbc-87f5-52c92ee93260.png)

## Lost to AI :(
![image](https://user-images.githubusercontent.com/47125700/170411498-753c0d0c-d513-4b96-889e-29c841f9f2b1.png)


## About how bots and bullets are drawn in the arena and collisions
All bots are drawn as a 10x10 square. The position show by the status message is the upper
left point. So if the bot status shows 150 150, the bot is drawn from 150,150 to 150,160, to 160,160
and 160,150, and back to 150,150. Bullets are also drawn as a square and the position shown in
the scan is the upper left point. The bullet power + 1 is the length of each side, so bullet power of 1
is drawn as 2x2 square and a bullet of 5 is a 6x6 square. Power Ups are drawn as a 10x10 square.
If a bot tries to move into a wall, then the move is prevented by the server (the message is
ignored) and no damage is taken by the bot.

