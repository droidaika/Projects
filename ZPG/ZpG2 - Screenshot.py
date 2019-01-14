import os
import time
import win32api, win32con
from PIL import ImageOps
from PIL import ImageGrab
from numpy import *
from PIL import Image
import cv2 as cv
import numpy as np
from matplotlib import pyplot as plt
x_pad = 3
y_pad = 25

def screenGrab():
    Directory = os.getcwd() + '\\Images'
    box = (x_pad, y_pad, x_pad + 640, y_pad + 480)
    im = ImageGrab.grab(box)
    # load the image and up some tracking variables
    #image = cv2.imread(args["image"])
    im.save(Directory + '\\SnapShot\\full_snap__' + str(int(time.time())) +
            '.png', 'PNG')
    img_rgb = cv.imread(Directory + '\\SnapShot\\full_snap__' + str(int(time.time())) +
                        '.png')


    # define the list of color boundaries
    boundaries = [
        ([155, 155, 150], [220, 210, 200])
    ]

    # loop over the boundaries
    for (lower, upper) in boundaries:
        # create NumPy arrays from the boundaries
        lower = np.array(lower, dtype="uint8")
        upper = np.array(upper, dtype="uint8")


        # find the colors within the specified boundaries
        mask = cv.inRange(img_rgb, lower, upper)

        print(mask)

        # merge the mask into the accumulated masks
        output = cv.bitwise_and(img_rgb, img_rgb, mask=mask)


        im = Image.fromarray(output)

        im.save(Directory + '\\SnapShot\\snap__' + str(int(time.time())) +
            '.png', 'PNG')









def main():
    screenGrab()


if __name__ == '__main__':
    main()