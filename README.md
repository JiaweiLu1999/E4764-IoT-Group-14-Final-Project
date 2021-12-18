# Smart Vending Machine

## Preface

- This is the final project of Columbia University EECS E4764 Fall'21 IOT. 
- We are Team #14:
    - [Jiawei Lu](https://www.linkedin.com/in/jiaweilucolumbia/)
    - [Chenhao Wang](https://www.linkedin.com/in/chenhaowangcu/)
    - [Yuxin Li](http://linkedin.com/in/yuxin-li-430a97185)
- If you have any questions, please email me at jl5999@columbia.edu.

## Abstract

The Smart Vending Machine has two ways of interaction.

1. The first way doesn’t require anything in hand during the whole buying because we built in a camera with face recognition and a touch screen with beautiful UI. Customers can buy and pay simply with their face. 

2. Users can also use a smartphone if they don’t want to touch the screen. Any operation like shopping, registering, topping up and uploading a photo for face login, can be easily done in the app. 

All user profiles and stock information are stored in a secure and real-time cloud database. Our pursuit is to provide users and maintainers a comfortable using experience.

Here is a [Live Demo Video](https://www.youtube.com/watch?v=YiR1l8RYqwU) to give you an overview of our Smart Vending Machine.

## Motivation

Imagine you are running outside and feel thirsty. Luckily, there’s a vending machine with various drinks. You want to buy a bottle of water but you realize that you didn’t bring your wallet. So sad!

However, if it happens to be our vending machine, congratulations!

You just need to log in to our machine with your lovely face and select the item on the touch screen, and everything is done! By the way, any touch could increase the risk of getting Covid, so we also designed a vending machine app for smartphones to help protect your health.

## System

### Architecture

The architecture of our Smart Vending Machine is summarized in the figure below. We will describe each component of our system in detail in the next section.

<img src="project_website/images/architecture.png" width=100% />


### Technical Components

- Embedded systems
    - Raspberry PI 4 Model B

    <p align="center">
    <img src="project_website/images/raspberrypi.jpeg" width=20%/>
    </p>

    - Raspberry PI Camera with 5 Million Pixels

    <p align="center">
    <img src="project_website/images/picamera.jpeg" width=20%/>
    </p>

    - Raspberry PI 4-Phase Stepper Motor

    <p align="center">
    <img src="project_website/images/motor.jpg" width=20%/>
    </p>

    - 320x240 2.8" TFT+Touchscreen

    <p align="center">
    <img src="project_website/images/tsada.jpeg" width=20%/>
    </p>

    
- Cloud Database

    - Firebase Realtime DB + Storage

    <p align="center">
    <img src="project_website/images/fblogo.svg" width=20%/>
    </p>

- Software

    - App Interacting with Smart Vending Machine

    <p align="center">
    <img src="project_website/images/androidlogo.png" width=30%/>
    </p>

    - Face Recognition Algorithm based on dlib

    <p align="center">
    <img src="project_website/images/dliblogo.png" width=10%/>
    </p>


### Prototype

<p align="center">
<img src="project_website/images/prototype.png" width=20%/>
<img src="project_website/images/appproto.png" width=15%/>
</p>

<p align="center">Smart Vending Machine with Face Recognition 
<br>+
<br>Android App</p>


## Results

- Android App

    <p align="center">
    <img src="project_website/images/app1.png" width=15%/>
    <img src="project_website/images/app2.png" width=15%/>
    </p>
    
    - We designed an Android App shown as above.
    - As a new user, we can Register and Login to the shopping page.
    - We can TOP UP to buy things in Smart Vending Machine.
    - If we want to enable the Face Recognition function of Smart Vending Machine, we need to upload a profile photo. 

- Cloud Data Base

    <p align="center">
    <img src="project_website/images/fb1.png" width=50%/>
    </p>
    <p align="center">
    <img src="project_website/images/fb2.png" width=50%/>
    </p>
    
    - We can manage our Firebase RealtimeDB and Storage.
    - We use RealtimeDB to track Product and User information.
    - We use Storage to save the profile photos of our users.

- Face Recognition

    <p align="center">
    <img src="project_website/images/face1.png" width=50%/>
    </p>
    <p align="center">
    <img src="project_website/images/face2.png" width=50%/>
    </p>
    
    - Firstly, we use a Deep Neural Network to detect the face location in one image.
    - Then, we encoding the face to a Tensor.
    - After updating the Dataset and keeping it the same as the Cloud Storage, we can comparing the encoded image with every image in our storage, then choose the one has the largest confidence.
    - Finally, we can print the related username and account information.


- Example A: Buy things using Android App

    - Please watch [this video](https://youtu.be/3ETGM1LA1Rc) on YouTube.

- Example B: Buy things with Face Recoginition

    - Please watch [this video](https://youtu.be/b2_GYJCJxdI) on YouTube.


## References

1. [Modern Face Recognition with Deep Learning](https://medium.com/@ageitgey/machine-learning-is-fun-part-4-modern-face-recognition-with-deep-learning-c3cffc121d78)

2. [Recognize and manipulate faces from Python or from the command line with the world's simplest face recognition library](https://github.com/ageitgey/face_recognition)

3. [Firebase Documentation](https://firebase.google.com/docs)

4. [GUIZERO Documentation](https://lawsie.github.io/guizero/about/)

## Contact

- Jiawei Lu:   jl5999@columbia.edu
- Chenhao Wang:   cw3355@columbia.edu
- Yuxin Li:   yl4873@columbia.edu

- [Columbia University Department of Electrical Engineering](https://www.ee.columbia.edu/)
- Instructor: Professsor [Xiaofan (Fred) Jiang](http://fredjiang.com/)

