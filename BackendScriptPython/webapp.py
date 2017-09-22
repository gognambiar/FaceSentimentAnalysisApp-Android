import pickle
import urllib
import cv2
import numpy as np
from flask import Flask, jsonify

app = Flask(__name__) # create a Flask app

# helper
def url_to_image(url, resize=224):
  """
  downloads an image from url, converts to numpy array,
  resizes, and returns it
  """
  response = urllib.urlopen(url)
  img = np.asarray(bytearray(response.read()), dtype=np.uint8)
  img = cv2.imdecode(img, cv2.IMREAD_COLOR)
  img = cv2.resize(img, (resize, resize), interpolation=cv2.INTER_CUBIC)
  return img

def get_nnet():
  ... # a nerual network architecture

@app.route('/predict/<path:url>', methods=['POST'])
def predict(url):
  img = url_to_image(url) # image array
  ... # here to add some prep steps
  pred = model.predict(img).argmax() # get index of the class with highest prob
  return jsonify({'prediction': label[pred]})

if __name__ == '__main__':
  print 'initialize model...'
  model = get_nnet()
  print 'load weights...'
  model.load_weights(model_path)
  print 'load label...'
  with open('label.pkl', 'rb') as handle:
    label = pickle.load(handle)

  app.run(debug=True) # this will start a local server
