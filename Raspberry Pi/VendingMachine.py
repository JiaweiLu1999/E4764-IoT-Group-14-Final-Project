#!/usr/bin/python3
import RPi.GPIO as GPIO
import time
import socket

def run_step_motor(n):  
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
    
if __name__ == "__main__":
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

    ip_addr = "192.168.31.128"
    socket_addr = socket.getaddrinfo(ip_addr, 6060)[0][-1]
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(socket_addr)
    s.listen(1)
    s.settimeout(1)
    print('listening on', socket_addr)

    while True:
        try:
            cl, addr = s.accept()
            print('client connected from', addr)
            request = str(cl.recv(1024))

            if 'msg' in request:
                msg = request.split('/?msg=')[1].split('HTTP')[0]
                msg = msg.replace('%20', ' ')
                print(msg)
                resp_msg = 'Buy things successfully!'

                if "run motor 0" in msg:
                    run_step_motor(0)
                elif "run motor 1" in msg:
                    run_step_motor(1)
                elif "run motor 2" in msg:
                    run_step_motor(2)

                suc_response = "HTTP/1.1 200 OK\r\n\r\n%s" % resp_msg
                cl.send(str.encode(suc_response))

            else:
                fail_response = "HTTP/1.1 501 Implemented\r\n\r\nNo GET message!"
                cl.send(str.encode(fail_response))

            cl.close()

        except:
            time.sleep(0.5)