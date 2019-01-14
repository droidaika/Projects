
import sqlite3
from sqlite3 import Error

Board = []

def SetUpBoard(db_file):

    db = sqlite3.connect(':memory:')
    db = sqlite3.connect(db_file)

    cursor = db.cursor()
    db.commit()
    cursor.execute('''SELECT  typeCode, mapNumber, positionX, positionY FROM gardens''')
    rows = cursor.fetchall()
    #print(rows)
    counter = 0
    for row in rows:
        print('{0} : {1}, {2}, {3}'.format(row[0], row[1], row[2], row[3]))
        if counter != row[0]:
            counter = counter + 1
        #print(counter)

        if row[1] == 0:
            Board.append([])
            for cellX in range(0,row[2]):
                Board[counter].append([])
                for cellY in range(0, row[3]):
                    Board[counter][cellX].append(0)
                    # print(Board)
        else:
            # print("b,x,y:", counter, row[2], row[3], row[1])

            Board[counter][row[2]][row[3]] = row[1]
            # print(Board)

        # row[0] returns the first column in the query (name), row[1] returns email column.



    db.close()




def main():
    SetUpBoard("C:\\Users\\Kevin\\PycharmProjects\\ZPG\\Images\\Rob.db")


if __name__ == '__main__':
    main()

