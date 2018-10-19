from datetime import datetime
from flask import jsonify
from webvad import app
from webvad import action

@app.route('/time',methods=["GET"])
def home():
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")

@app.route('/vad',methods=["GET"])
def splitWav():
	action.splitWav()
	return jsonify({'status':200})

@app.route('/clear',methods=["GET"])
def clear():
	action.clear()
	return jsonify({'status':200})