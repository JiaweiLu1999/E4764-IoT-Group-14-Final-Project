import face_recognition
import picamera
import numpy as np
from PIL import Image
import pyrebase
import os
import RPi.GPIO as GPIO
import time

def camera_init():
    camera = picamera.PiCamera()
    camera.resolution = (320, 240)
    return camera

def firebase_init():
    config = {
      "apiKey": "AIzaSyC_UQjYvDX1uNGOo4p0hHsfi-egz1EV_OE",
      "authDomain": "vending-login.firebaseapp.com",
      "databaseURL": "https://vending-login-default-rtdb.firebaseio.com",
      "projectId": "vending-login",
      "storageBucket": "vending-login.appspot.com", 
      "serviceAccount": "/home/pi/IoT/vending-login-firebase-adminsdk-jw34y-d1b690e986.json"
    }

    firebase = pyrebase.initialize_app(config)
    return firebase

def realdb_init(firebase):
    db = firebase.database()
    return db

def items_update(db, item_name, item_list, price_list, quantity_list):
    price = db.child('Products').child(item_name).child('Price').get().val()
    quantity = db.child('Products').child(item_name).child('Quantity').get().val()
    for i in range(len(item_list)):
        if item_name == item_list[i]:
            price_list[i] = price
            quantity_list[i] = quantity
            
    return price_list, quantity_list

def items_init(db, item_list):
    price_list = np.zeros(len(item_list))
    quantity_list = a = np.zeros(len(item_list), dtype=np.uint8)
    for item_name in item_list:
        items_update(db, item_name, item_list, price_list, quantity_list)
    return price_list.tolist(), quantity_list.tolist()

def image_resize(image_name, dataset_path):
    # Opens a image in RGB mode
    im = Image.open(dataset_path + image_name)
    width, height = im.size
    alpha = 320/width
    width, height = int(alpha*width), int(alpha*height)
    newsize = (width, height)
    im1 = im.resize(newsize)
    im1.save(dataset_path + image_name)

def dataset_init(dataset_path):
    print("Initializing dataset...")
    dataset_encodings = []
    dataset_files = []
    dataset_filename = os.listdir(dataset_path)
    for file in dataset_filename:
        if file[-4:] == ".jpg":
            dataset_image = face_recognition.load_image_file(dataset_path + file)
            dataset_encoding = face_recognition.face_encodings(dataset_image)[0]
            dataset_encodings.append(dataset_encoding)
            dataset_files.append(file)
    
    return dataset_encodings, dataset_files
            

def dataset_update(firebase, dataset_path, dataset_encodings, dataset_files):
    print("Updating database...")
    storage = firebase.storage()
    # All files in firestore storage.
    all_files = storage.child("faces/").list_files()
    for file in all_files:
        tmp_name = file.name[6:] + ".jpg"
        if tmp_name in dataset_files:
            print(f"Already has file {tmp_name}.")
        else:
            file.download_to_filename(dataset_path + tmp_name)
            print(f"Downloading file {tmp_name}.")
            image_resize(tmp_name, dataset_path)
            dataset_image = face_recognition.load_image_file(dataset_path + tmp_name)
            dataset_encoding = face_recognition.face_encodings(dataset_image)[0]
            dataset_encodings.append(dataset_encoding)
            dataset_files.append(tmp_name)
    return dataset_encodings, dataset_files

def face_recog(db, camera, dataset_encodings, dataset_files):
    # Initialize some variables
    output = np.empty((240, 320, 3), dtype=np.uint8)
    camera.capture(output, format="rgb")
    face_locations = []
    face_encodings = []
    name = "No Match!"
    balance = 0
    ID = 0
    
    # Find all the faces and face encodings in the current frame of video
    face_locations = face_recognition.face_locations(output)
    print("Found {} faces in image.".format(len(face_locations)))
    face_encodings = face_recognition.face_encodings(output, face_locations)

    # Loop over each face found in the frame to see if it's someone we know.
    for i in range(len(face_encodings)):
        for j in range(len(dataset_encodings)):
            # See if the face is a match for the known face(s)
            match = face_recognition.compare_faces([dataset_encodings[j]], face_encodings[i], tolerance = 0.4)
            if match[0]:
                ID = dataset_files[j][:-4]
                name = db.child("Users").child(ID).child("name").get().val()
                balance = round(db.child("Users").child(ID).child("balance").get().val(), 2)
    return name, ID, balance

def run_step_motor(n):
    step_motor_pin_list = [[14, 15, 18,  4],
                       [17, 27, 22, 23],
                       [ 5,  6, 13, 12]]

    # defining stepper motor sequence (found in documentation http://www.4tronix.co.uk/arduino/Stepper-Motors.php)
    step_sequence = [[1,0,0,1],
                     [1,0,0,0],
                     [1,1,0,0],
                     [0,1,0,0],
                     [0,1,1,0],
                     [0,0,1,0],
                     [0,0,1,1],
                     [0,0,0,1]]

    # careful lowering this, at some point you run into the mechanical limitation of how quick your motor can move
    step_sleep = 0.002
    step_count = 4096 # 5.625*(1/64) per step, 4096 steps is 360°
    direction = True # True for clockwise, False for counter-clockwise
    
    in1, in2, in3, in4 = step_motor_pin_list[n]

    # setting up
    GPIO.setmode( GPIO.BCM )
    GPIO.setup( in1, GPIO.OUT )
    GPIO.setup( in2, GPIO.OUT )
    GPIO.setup( in3, GPIO.OUT )
    GPIO.setup( in4, GPIO.OUT )

    # initializing
    GPIO.output( in1, GPIO.LOW )
    GPIO.output( in2, GPIO.LOW )
    GPIO.output( in3, GPIO.LOW )
    GPIO.output( in4, GPIO.LOW )

    motor_pins = [in1,in2,in3,in4]
    motor_step_counter = 0

    def cleanup():
        GPIO.output( in1, GPIO.LOW )
        GPIO.output( in2, GPIO.LOW )
        GPIO.output( in3, GPIO.LOW )
        GPIO.output( in4, GPIO.LOW )
        GPIO.cleanup()

    # the meat
    try:
        i = 0
        for i in range(step_count):
            for pin in range(0, len(motor_pins)):
                GPIO.output( motor_pins[pin], step_sequence[motor_step_counter][pin] )
            if direction==True:
                motor_step_counter = (motor_step_counter - 1) % 8
            elif direction==False:
                motor_step_counter = (motor_step_counter + 1) % 8
            else: # defensive programming
                print( "uh oh... direction should *always* be either True or False" )
                cleanup()
            time.sleep( step_sleep )

    except KeyboardInterrupt:
        cleanup()
        exit( 1 )

    cleanup()

    
def push_data(db, ID, new_balance, item_name, new_quantity):
    db.child("Users").child(ID).child("balance").set(new_balance)
    db.child("Products").child(item_name).child("Quantity").set(new_quantity) 