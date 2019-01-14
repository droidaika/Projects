import pyautogui
import os
import time
os.startfile("C:\Program Files (x86)\Zen Puzzle Garden Demo\zen.exe")
import subprocess

#pid=subprocess.Popen(["C:\Program Files (x86)\Zen Puzzle Garden Demo\zen.exe"]).pid
#time.sleep(2)
screenWidth, screenHeight = pid.size()



pyautogui.moveTo(screenWidth / 2, screenHeight / 2)

print(screenWidth, 'works')