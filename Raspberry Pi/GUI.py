from guizero import App, PushButton, TextBox, Picture, Text
from util import *

root_dir = '/home/pi/IoT/'
dataset_path = root_dir + 'faces/'
camera = camera_init()
firebase = firebase_init()
db = realdb_init(firebase)
product_list = ["Doritos", "Cheetos: Crunchy", "Cheetos: Puff"]
price_list, quantity_list = items_init(db, product_list)
name = "Not Login"
balance = 0
ID = 0

dataset_encodings, dataset_files = dataset_init(dataset_path)
dataset_encodings, dataset_files = dataset_update(firebase, dataset_path, dataset_encodings, dataset_files)

def logout():
    global name, ID, balance
    name = "Not Login"
    balance = 0
    ID = 0
    txtNameVal.clear()
    txtNameVal.append(name)
    txtBalanceVal.clear()
    txtBalanceVal.append(str(balance))


def face_login():
    global name, ID, balance, dataset_encodings, dataset_files
    dataset_encodings, dataset_files = dataset_update(firebase, dataset_path, dataset_encodings, dataset_files)
    name, ID, balance = face_recog(db, camera, dataset_encodings, dataset_files)
    if name != "No Match!":
        txtNameVal.clear()
        txtNameVal.append(name)
        txtBalanceVal.clear()
        txtBalanceVal.append(str(balance))
        app.info("Face Login: ", "Welcome "+ name + "!")
    else:
        app.warn("Face Login: ", "No match! Try agian!")

def buy(i, txtQuantity):
    global balance, price_list, quantity_list
    if quantity_list[i] <= 0:
        app.warn("Buy: ", "Out of stock!")
    elif balance < price_list[i]:
        app.warn("Buy: ", "Not enough balance, please top up!")
    else:
        run_step_motor(i)
        push_data(db, ID, balance - price_list[i], product_list[i], quantity_list[i] - 1)
        balance = db.child("Users").child(ID).child("balance").get().val()
        price_list, quantity_list = items_update(db, product_list[i], product_list, price_list, quantity_list)
        txtQuantity.clear()
        txtQuantity.append("In stock: "+str(quantity_list[i]))
        txtBalanceVal.clear()
        txtBalanceVal.append(str(balance))

def refresh():
    if ID != 0:
        global balance
        balance = round(db.child("Users").child(ID).child("balance").get().val(),2)
        txtBalanceVal.clear()
        txtBalanceVal.append(str(balance))

    global quantity_list
    price_list, quantity_list = items_init(db, product_list)
    txtQuantity1.clear()
    txtQuantity1.append("In stock: "+str(quantity_list[0]))
    txtQuantity2.clear()
    txtQuantity2.append("In stock: "+str(quantity_list[1]))
    txtQuantity3.clear()
    txtQuantity3.append("In stock: "+str(quantity_list[2]))

def refresh_dataset():
    global dataset_encodings, dataset_files
    dataset_encodings, dataset_files = dataset_update(firebase, dataset_path, dataset_encodings, dataset_files)

app = App(title="Smart Vending Machine", layout="grid")
app.full_screen = True

title_vm = Text(app, text="Smart Vending Machine", grid=[0,0,2,1], width = 25, height = 2, size = 20, color = "green")

btnLogout = PushButton(app, text="Logout", command = logout, grid=[2,0])

txtNameString = Text(app, text="User: ", grid=[0,1], size = 14)
txtBalanceString = Text(app, text="Balance: ", grid=[0,2], size = 14)

txtNameVal = Text(app, text = name, grid=[1,1], size = 14)
txtBalanceVal = Text(app, text = str(balance), grid=[1,2], size = 14)

btnFacelogin = PushButton(app, text="Face Login", command = face_login, grid=[2,1,1,2], width = 15, height = 4)

img1 = Picture(app, image="/home/pi/IoT/items/1.png", grid=[0,3,1,1])
img2 = Picture(app, image="/home/pi/IoT/items/2.png", grid=[1,3,1,1])
img3 = Picture(app, image="/home/pi/IoT/items/3.png", grid=[2,3,1,1])

txtQuantity1 = Text(app, text="In stock: "+str(quantity_list[0]), grid=[0,4], size = 13)
txtQuantity2 = Text(app, text="In stock: "+str(quantity_list[1]), grid=[1,4], size = 13)
txtQuantity3 = Text(app, text="In stock: "+str(quantity_list[2]), grid=[2,4], size = 13)

txtPrice1 = Text(app, text="Price: $"+str(price_list[0]), grid=[0,5], size = 13)
txtPrice2 = Text(app, text="Price: $"+str(price_list[1]), grid=[1,5], size = 13)
txtPrice3 = Text(app, text="Price: $"+str(price_list[2]), grid=[2,5], size = 13)

btnBuy1 = PushButton(app, text="BUY", args = [0, txtQuantity1], command = buy, grid=[0,6])
btnBuy2 = PushButton(app, text="BUY", args = [1, txtQuantity2], command = buy, grid=[1,6])
btnBuy3 = PushButton(app, text="BUY", args = [2, txtQuantity3], command = buy, grid=[2,6])

title_vm.repeat(3000, refresh)

app.display()