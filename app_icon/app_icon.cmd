@echo off

call ic_launcher 48
move ic_launcher.png ..\android\app\src\main\res\mipmap-mdpi\ic_launcher.png
call ic_launcher 72
move ic_launcher.png ..\android\app\src\main\res\mipmap-hdpi\ic_launcher.png
call ic_launcher 96
move ic_launcher.png ..\android\app\src\main\res\mipmap-xhdpi\ic_launcher.png
call ic_launcher 144
move ic_launcher.png ..\android\app\src\main\res\mipmap-xxhdpi\ic_launcher.png
call ic_launcher 192
move ic_launcher.png ..\android\app\src\main\res\mipmap-xxxhdpi\ic_launcher.png
call ic_launcher 512
move ic_launcher.png ..\avalon.png
