import os
import time
import win32api
import win32con
import sqlite3
from sqlite3 import Error
from PIL import ImageOps
from PIL import ImageGrab
from PIL import Image
from numpy import *
import cv2 as cv
import numpy as np
from matplotlib import pyplot as plt
x_pad = 3
y_pad = 5
Directory = os.getcwd() + '\\Images'




def create_connection(db_file):
    """ create a database connection to a SQLite database """
    db = sqlite3.connect(':memory:')
    db = sqlite3.connect(db_file)

    cursor = db.cursor()
    cursor.execute('''DROP TABLE gardens''')
    db.commit()
    cursor.execute('''
        CREATE TABLE gardens(id INTEGER PRIMARY KEY, mapNumber INTEGER, typeCode INTEGER,
                           positionX INTEGER, positionY INTEGER )
    ''')
    db.commit()



def printTables(db_file):
    db = sqlite3.connect(':memory:')
    db = sqlite3.connect(db_file)

    cursor = db.cursor()
    db.commit()
    cursor.execute('''SELECT mapNumber, typeCode,  positionX, positionY FROM gardens''')
    all_rows = cursor.fetchall()
    for row in all_rows:
        # row[0] returns the first column in the query (name), row[1] returns email column.
        print('{0} : {1}, {2}, {3}'.format(row[0], row[1], row[2], row[3]))

    #cursor.execute('''DROP TABLE gardens''')
    db.close()






def grab():
    box = (x_pad + 1,y_pad+1,x_pad+640,y_pad+480)
    im = ImageOps.grayscale(ImageGrab.grab(box))
    a = array(im.getcolors())
    a = a.sum()
    print (a)
    return a

def Levels():

    mousePos((x_pad + 390, y_pad + 263))
    leftClick()
    time.sleep(1)
    for i in range(0, 5):
        for j in range(0, 8):
            mousePos((x_pad + 174 + (j * 40), y_pad + 181 + (i * 30)))
            leftClick()
            time.sleep(1)
            mousePos((x_pad + 10, y_pad + 50))
            screenGrab(j+i*8)
            time.sleep(1)
            win32api.keybd_event(0x1B, 0, 0, 0)
            time.sleep(.05)
            win32api.keybd_event(0x1B, 0, win32con.KEYEVENTF_KEYUP, 0)


            time.sleep(1)


def screenGrab(MapNum):

    db = sqlite3.connect("C:\\Users\\Kevin\\PycharmProjects\\ZPG\\Images\\Rob.db")

    cursor = db.cursor()

    box = (x_pad,y_pad,x_pad + 640,y_pad + 500)
    im = ImageGrab.grab(box)
    imageLocation = Directory + '\\SnapShot\\full_snap__' + str(int(time.time())) + '.png'
    im.save(imageLocation, 'PNG')
    img_rgb = cv.imread(imageLocation)


    # define the list of color boundaries
    boundaries = [
        ([155, 155, 150], [220, 210, 200]),
        ([5, 15, 35], [85, 95, 115])
    ]

    CornerCoord = []
    OrnamentCoord = []
    RedLeafCoord = []
    YellowLeafCoord = []
    GreenLeafCoord = []
    BigRockCoord = []
    ObjectCoord = []



    # loop over the boundaries
    for (lower, upper) in boundaries:
        # create NumPy arrays from the boundaries
        lower = np.array(lower, dtype="uint8")
        upper = np.array(upper, dtype="uint8")

        # find the colors within the specified boundaries
        mask = cv.inRange(img_rgb, lower, upper)



        # merge the mask into the accumulated masks
        output = cv.bitwise_and(img_rgb, img_rgb, mask=mask)

        # show the images
        #cv.imshow("images", np.hstack([img_rgb, output]))
        #cv.waitKey(10000)


        im = np.asarray(Image.fromarray(output))
        im_grey = cv.cvtColor(im, cv.COLOR_BGR2GRAY)


        scanImage('\\Corners', CornerCoord,  im_grey, im)
        scanImage('\\Objects',  ObjectCoord,  im_grey,im)
        scanImage('\\BigRocks', BigRockCoord, im_grey, im)
        scanImage('\\Ornaments', OrnamentCoord, im_grey, im)
        scanImage('\\RedLeaf',  RedLeafCoord, im_grey, im)
        scanImage('\\YellowLeaf', YellowLeafCoord, im_grey, im)
        scanImage('\\GreenLeaf', GreenLeafCoord, im_grey, im)

    #print(CornerCoord)

    # CornerXCoord = math.floor((CornerCoord[0][1] - CornerCoord[1][1]) / 40)
    # CornerYCoord = math.floor((CornerCoord[0][0] - CornerCoord[1][0])/30)
    #print(math.floor((CornerCoord[0][1] - CornerCoord[1][1]) / 40))
    #print(math.floor((CornerCoord[0][0]- CornerCoord[1][0])/30))
    #print(ObjectCoord)
    #print(ObjectCoord)
#    print("here", CornerXCoord,CornerXCoord)
    #print("objects:", len(ObjectCoord))

    InsertTable(cursor, CornerCoord[1], [CornerCoord[0]], 0, MapNum)
    InsertTable(cursor, CornerCoord[1], ObjectCoord, 1, MapNum)
    InsertTable(cursor, CornerCoord[1], BigRockCoord, 2, MapNum)
    InsertTable(cursor, CornerCoord[1], OrnamentCoord, 3, MapNum)
    InsertTable(cursor, CornerCoord[1], GreenLeafCoord, 4, MapNum)
    InsertTable(cursor, CornerCoord[1], YellowLeafCoord, 5, MapNum)
    InsertTable(cursor, CornerCoord[1], RedLeafCoord, 6, MapNum)

    db.commit()


    #print('First user inserted')


def InsertTable(cursor, CornerCoord, ObjectArray, TypeCode, MapNum):
    for object in ObjectArray:
        #print(object)
        # print(math.floor((object[1] - CornerCoord[1][1]) / 40), ",", math.floor((object[0] - CornerCoord[1][0]) / 30), "\n")
        ObjectXCoord = math.floor((object[1] - CornerCoord[1]) / 40)
        ObjectYCoord = math.floor((object[0] - CornerCoord[0]) / 30)
        cursor.execute('''INSERT INTO gardens(mapNumber, typeCode, positionX, positionY)
                             VALUES(?,?,?,?)''', (MapNum, TypeCode, ObjectXCoord, ObjectYCoord))




def scanImage(local, OutputArray, image_grey, outputImage):
    for filename in os.listdir(Directory + local):
        if filename.endswith(".PNG"):
           # print(filename)
            template = cv.imread(Directory + local + '\\' + filename, 0)
            w, h = template.shape[::-1]
            res = cv.matchTemplate(image_grey, template, cv.TM_CCOEFF_NORMED)
            threshold = 0.97
            loc = np.where(res >= threshold)
            #print(zip(*loc))
            if list(zip(*loc)):
                for rock in zip(*loc):
                    #print("rock:",rock)
                    OutputArray.append(rock)


                cv.imwrite('rea.png', outputImage)

            for pt in zip(*loc[::-1]):
                cv.rectangle(outputImage, pt, (pt[0] + w, pt[1] + h), (0, 0, 255), 2)

        else:
            print("wrong extension")

def leftClick():
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN,0,0)
    time.sleep(.1)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP,0,0)
    print ("Click.")



def mousePos(cord):
    win32api.SetCursorPos((x_pad + cord[0], y_pad + cord[1]))

def get_cords():
    x, y = win32api.GetCursorPos()
    x = x - x_pad
    y = y - y_pad
    print (x, y)

def main():
    Levels()


if __name__ == '__main__':
    create_connection("C:\\Users\\Kevin\\PycharmProjects\\ZPG\\Images\\Rob.db")
    main()
    printTables("C:\\Users\\Kevin\\PycharmProjects\\ZPG\\Images\\Rob.db")