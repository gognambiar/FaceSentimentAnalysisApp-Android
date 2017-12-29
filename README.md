# FaceSentimentAnalysisApp-Android
Implemented an Android App that predicts face sentiments(Angry, Disgust, Fear, Happy, Neutral, Sad, Surprise) of the faces in a photo. The App allows user to either upload a photo or take one. The photo is then searched for faces and they are cropped out. The faces are then sent to a server written in Python via REST. The server is a Convolutional Neural Network trained with 20000 face images. The server predicts the sentiment and sends the response back to the App.


The User interface provides the user with options to either take a picture using camera or upload a photo from the file system. Once the photo is uploaded/taken, the app detects all the faces in the picture(Maximum number of faces that can be detected is set to 3). If there are no faces is the picture, an alert message is thrown stating "No faces found, please take/upload another photo!" 

The faces detected in the pictures are then stored as seperate images and sent to a python backend server via a REST call which is a trained Convolution Neural Network with 20000 face images categorized into the following seven emotions - Angry, Disgust, Fear, Happy, Neutral, Sad, Surprise. The images sent are then tested aginst this net and the result/response is sent back to the android front-end. 

The Faces detected in the image and their correspoding sentiment results are displayed in the front end.
