// Import the Firebase SDK for Google Cloud Functions
const functions = require('firebase-functions');
// Import and initialize the Firebase Admin SDK.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.sendNotification = functions.database.ref('/Books/{pushID}').onWrite((event) => {
    const snapshot = event.data;

    //Book Details
    const subject = snapshot.val().subject;
    const module = snapshot.val().modules;
    console.log("Subject: " + subject);
    console.log("Module: " + module);

    //Notification details
    const payload = {
        notification: {
            title: `A new book has been uploaded in ${module}`,
            body: 'Get it now before its gone!'
        }
    };

    return admin.messaging().sendToTopic(module,payload);

});

const contentBasedRecommender = require("content-based-recommender");
//Database reference to "UserProfile" location
var userProfile = admin.database().ref('/UserProfile');

//Object to be used to store "UserProfile" values
var users = new Object;
var books = new Object;

//Array to store "userProfile" and "booksProfile" item for
//Comparison in Recommeder 
var userPro = new Array();
var booksProfile = new Array();

//Object to store similar "Book" objects
var similar = new Object();

//Store "Book"objects with DB key
var bookList  = new Object;

//Firebase cloud function. Triggers everytime new object is written in DB "Books/" location
exports.recommenderSys = functions.database.ref('Books/').onWrite(event => {

	const root = event.data.ref.root;

	const userProfileReference = root.child('/UserProfile').once('value');
	const bookReference = root.child('/Books').once('value');
	bookList = bookReference;

	userPro = [];
	booksProfile = [];
	bookList = [];

	return prep = Promise.all([userProfileReference, bookReference]).then(results =>{
		const userP = results[0];
		const bookR = results[1];

		userP.forEach(function(childSnapshot)
		{
			users = childSnapshot.val()["year"] + ", " + childSnapshot.val()["department"] + ", " + childSnapshot.val()["modules"];
			userPro.push({content: users.toString(),id: childSnapshot.key});		
		})
		bookR.forEach(function(childSnapshot)
		{
			bookList[childSnapshot.key] = childSnapshot.val();
			books = childSnapshot.val()["year"] + ", " + childSnapshot.val()["subject"] + ", " + childSnapshot.val()["module"];
			booksProfile.push({content: books.toString(),id: childSnapshot.key});	
		})
		return
	}).then(method =>{
		return itemRec();
	})
 })


//Function to calculate distance between 2 vectors "User" and "Book" using cosine distance
//Write to DB location "UserPersonalisedFeed/" book objects(vectors) that are similar to
//user object(vector) after comparison.
function itemRec()
{
	//Class contentBasedRecommender. Providers method for items(vectors) comparison.
	//@minScore: lowest score to consider. Treshold value.
	//@maxSimilarDocuments: max number of result to return after comparison	
	const recommender = new contentBasedRecommender({minScore: 0, maxSimilarDocuments:100});

	  for(var i = 0; i < userPro.length; i++)
	  {

	    booksProfile.unshift({content: userPro[i]["content"], id: userPro[i]["id"]});

	    recommender.train(booksProfile);

	    similarDocuments = recommender.getSimilarDocuments(userPro[i]["id"],0,100);

	    
	    for(var y = 0; y < similarDocuments.length; y++)
	    {
	    var id = similarDocuments[y]["id"];

	    similar[y] = bookList[id];
	    admin.database().ref('UserPersonalisedFeed/').child(userPro[i]["id"]).set(similar);
		}

	 	booksProfile.shift();
	 	similar = [];
	  }
	  

}

