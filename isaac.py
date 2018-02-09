import numpy as np
import cv2
import math


CLOSE_AREA  = 233452.5

#yellow color ranges
lower_yellow = np.array([20, 140, 20])
upper_yellow = np.array([30, 255, 255])
loower_yellow = np.array([20, 100, 100])
uppper_yellow = np.array([60, 255, 255])
    
#image used in program
im = cv2.imread('C:/Users/GravitechX/Desktop/lit_pictures_1.jpg', 1)

#changing image colorspace from BGR to HSV
hsv = cv2.cvtColor(im, cv2.COLOR_BGR2HSV)
rgb = im

#resizes the images to 1/4 the size
resize = .25
hsv = cv2.resize(hsv, (0,0), fx=resize, fy=resize)
blite = cv2.resize(hsv, (0,0), fx=resize, fy=resize)
im = cv2.resize(im, (0,0), fx=resize, fy=resize)
rgb = cv2.resize(rgb, (0,0), fx=resize, fy=resize)

#gets the height, width and channels of the image. (I have no idea what the channels are) 
height, width, channels = im.shape
print (height, width)

#blurs the image
hsv = cv2.medianBlur (hsv, 5)

#locates all "yellow" and filters everything else out.  Results in a black and white image
blite = cv2.inRange(hsv, lower_yellow, upper_yellow)
rgb = cv2.inRange(rgb, loower_yellow, uppper_yellow)

#changes the colorspace of the filtered image to BGR allowing for the contours to be displayed in color
final = cv2.cvtColor(blite, cv2.COLOR_GRAY2BGR)

#sets up contour stuff
ret,thresh = cv2.threshold(blite, 255, 255, 255)
blite, contours, hierarchy = cv2.findContours(thresh,cv2.RETR_TREE,cv2.CHAIN_APPROX_NONE)


#variables to be used in for loop
biggest_contour = 0
other_contour = 0
y = 0

#loop that finds the contour with the biggest area.
#Remeber that everything but yellow has been filtered out reducing the contour count greatly
for x in range(0, len(contours)):
    if x == 0:
        biggest_contour = cv2.contourArea(contours[x])
        y = x
    elif cv2.contourArea(contours[x]) > cv2.contourArea(contours[y]):
        biggest_contour = cv2.contourArea(contours[x])
        y = x
        
    x += 1    

#variables to be used in finding the average point of the box
cnt = contours[y]
M = cv2.moments(cnt)



cx = int(M['m10']/M['m00']) #Average x point of contour box
cy = int(M['m01']/M['m00']) #Average y point of contour box

#variables to be used in for loop below
av_len = 0
total_lengths = 1
#puts the box contour in a multidimensional array including all points on the contour
contour = np.vstack(contours[y]).squeeze()
contour2 = contours[y]
new_contour = []
stuff = []
oldX = []
top = 0
bottom = 100000
this = True
for x in range(0, len(contour) - 1):
    stuff.append(x)
    for j in range(x + 1, len(contour) - 1):
        if contour[j][0] == contour[x][0]:
            stuff.append(j)
    for i in range(0, len(stuff) - 1):
        if contour[stuff[i]][1] > top:
            top = contour[stuff[i]][1]
        elif contour[stuff[i]][1] < bottom:
            bottom = contour[stuff[i]][1]
    for n in range(0, len(contour) - 1):
        if contour[n][0] == contour[x][0]:
            if contour[n][1] != top and contour[n][1] != bottom:
                this = True
            else:
                np.append(new_contour, [contour[n]])
    for h in range(0, len(stuff) - 1):
        del stuff[0]
    oldX.append(contour[x][0])
print(contours[y])
print(new_contour)
#loops through the box contour and finds the average distance from the centroid to the perimeter
for x in range(0, len(contour)):
    #distance formula
    av_len += ((contour[x][0] - cx)**2 + (contour[x][1] - cy)**2)**(1/2)
    total_lengths +=1
#finds the average
av_len = av_len/total_lengths
#draws the box contour onto final
final = cv2.drawContours(final, new_contour, -1, (0, 0, 255), 5)

#distances from centroid to edges of screen
distance_from_left = cx
distance_from_right = width - cx
distance_from_top = cy
distance_from_bottom = height - cy



#draws the centroid onto final
cv2.circle(final, (cx, cy), 1, (0,255,0), thickness=1, lineType=8, shift=0)

#draws box contour onto hsv and im
hsv = cv2.drawContours(hsv, new_contour, -1, (0, 0, 255), 5)
im = cv2.drawContours(im, new_contour, -1, (0, 0, 255), 5)
#draws the centroid onto im
cv2.circle(im, (cx, cy), 1, (0,255,0), thickness=10, lineType=8, shift=0)
print (cx, cy)

#draws center point of image
cv2.circle(im, (int(width/2), cy), 1, (255,0,0), thickness=10, lineType=8, shift=0)
distance = cx - int(width/2)

#this is necessary for the instantiation of the vision DO NOT DELETE OR ELSE EVERYTHING WILL BREAK
print("I chose to sat there - 1/21/18")



#displays the images
cv2.imshow('hsv', hsv)
cv2.imshow('blite', blite)
cv2.imshow('final', final)
cv2.imshow('im', im)
#cv2.imshow('rgb', rgb)

#kills the windows when a key is pressed
cv2.waitKey(0)
cv2.destroyAllWindows()